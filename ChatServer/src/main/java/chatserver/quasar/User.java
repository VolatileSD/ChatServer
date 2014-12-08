package chatserver.quasar;

import java.nio.ByteBuffer;
import java.io.IOException;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.data.Msg;
import chatserver.data.MsgType;


public class User extends BasicActor<Msg, Void> {
  static int MAXLEN = 1024;
  
  final ActorRef room;
  final FiberSocketChannel socket;

  public User(ActorRef room, FiberSocketChannel socket) { 
    this.room = room; 
    this.socket = socket; 
  }

  protected Void doRun() throws InterruptedException, SuspendExecution { //Exceptions
    new LineReader(self(), socket).spawn();
    room.send(new Msg(MsgType.ENTER, self()));
    socket.write(ByteBuffer.wrap("Welcome\n".getBytes()));
   
    while (receive(msg -> {
      try {
      switch (msg.getType()) {
        case DATA:
          String line = msg.getO().toString();
          if(line.startsWith(":")){
            String[] parts = line.split(" ");
            Command cmd = new Command(parts[0]);

            switch (cmd.getType()){
              //  HELP, LIST_ROOMS, LIST_USERS, CHANGE_ROOM, LOGIN,LOGOUT, UNKNOWN
              case LIST_ROOMS:
              case LIST_USERS:
              case LOGIN:
              case CHANGE_ROOM:
                byte[] b ="Changing rooms\n".getBytes();
                room.send(new Msg(MsgType.LINE,b ));
                room.send(new Msg(MsgType.LEAVE, self()));
                ActorRef newroom = new Room("Another").spawn();
                room=newroom;
                room.send(new Msg(MsgType.ENTER, self()));
              case HELP:
                //Help command: returns a list of all available commands
              case UNKNOWN:
              byte[] uc = "Unknown command\t".getBytes();
              room.send(new Msg(MsgType.LINE, uc));
              room.send(new Msg(MsgType.LINE, msg.getO()));
              return true;
                
            }
          } else{
            room.send(new Msg(MsgType.LINE, msg.getO()));            
          }
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
            out.put(b);
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
}