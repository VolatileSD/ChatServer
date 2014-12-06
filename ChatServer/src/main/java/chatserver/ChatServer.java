package chatserver;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.net.InetSocketAddress;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;
import java.util.*;

import chatserver.data.Msg;
import chatserver.data.MsgType;
import chatserver.rest.ChatServerApplication;


public class ChatServer {
  static int MAXLEN = 1024;

  static class LineReader extends BasicActor<Msg, Void> {
    final ActorRef<Msg> dest;
    final FiberSocketChannel socket;
    ByteBuffer in = ByteBuffer.allocate(MAXLEN);
    ByteBuffer out = ByteBuffer.allocate(MAXLEN);

    LineReader(ActorRef<Msg> dest, FiberSocketChannel socket) {
      this.dest = dest; this.socket = socket;
    }

    protected Void doRun() throws InterruptedException, SuspendExecution {
      boolean eof = false;
      byte b = 0;
      try {
        for(;;) {
          if (socket.read(in) <= 0) eof = true;
          in.flip();
          while(in.hasRemaining()) {
            b = in.get();
            out.put(b+"0");
            if (b == '\n') break;
          }
          if (eof || b == '\n') { // send line
            out.flip();
            if (out.remaining() > 0) {
              byte[] ba = new byte[out.remaining()];
              out.get(ba);
              out.clear();
              dest.send(new Msg(MsgType.DATA, ba));
            }
          }
          if (eof && !in.hasRemaining()) break;
          in.compact();
        }
        dest.send(new Msg(MsgType.EOF, null));
        return null;
      } catch (IOException e) {
        dest.send(new Msg(MsgType.IOE, null));
        return null;
      }
    }

  }

  static class User extends BasicActor<Msg, Void> {
    final ActorRef room;
    final FiberSocketChannel socket;
    User(ActorRef room, FiberSocketChannel socket) { this.room = room; this.socket = socket; }

    protected Void doRun() throws InterruptedException, SuspendExecution {
      new LineReader(self(), socket).spawn();
      room.send(new Msg(MsgType.ENTER, self()));
      while (receive(msg -> {
        try {
        switch (msg.getType()) {
          case DATA:
            room.send(new Msg(MsgType.LINE, msg.getO()));
            return true;
          case EOF:
          case IOE:
            room.send(new Msg(MsgType.LEAVE, self()));
            socket.close();
            return false;
          case LINE:
              socket.write(ByteBuffer.wrap((byte[]) msg.getO()));
            return true;
        }
        } catch (IOException e) {
          room.send(new Msg(MsgType.LEAVE, self()));
        }
        return false;  // stops the actor if some unexpected message is received
      }));
      return null;
    }
  }

  static class Room extends BasicActor<Msg, Void> {
    private Set<ActorRef> users = new HashSet();

    protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
        switch (msg.getType()) {
          case ENTER:
            users.add((ActorRef) msg.getO());
            return true;
          case LEAVE:
            users.remove((ActorRef) msg.getO());
            return true;
          case LINE:
            for (ActorRef u : users) u.send(msg); // danger!?!?
            // concurrent exception can be thrown here right?
            // clone might solve it
            return true;
        }
        return false;
      }));
      return null;
    }
  }

  static class Acceptor extends BasicActor {
    final int port;
    final ActorRef room;
    Acceptor(int port, ActorRef room) { this.port = port; this.room = room; }

    protected Void doRun() throws InterruptedException, SuspendExecution {
      try {
        FiberServerSocketChannel ss = FiberServerSocketChannel.open();
        ss.bind(new InetSocketAddress(port));
        while (true) {
          FiberSocketChannel socket = ss.accept();
          ActorRef user = new User(room, socket).spawn();
          room.send(new Msg(MsgType.ENTER, user)); 
        }
      } catch (IOException e) { }
      return null;
    }
  }

  public static void main(String[] args) throws Exception {
    int port = 12345; //Integer.parseInt(args[0]);
    ActorRef room = new Room().spawn();
    Acceptor acceptor = new Acceptor(port, room);
    new ChatServerApplication().run(args); // starts rest
    acceptor.spawn();
    acceptor.join();
  }

}
