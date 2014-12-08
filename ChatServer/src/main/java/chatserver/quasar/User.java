package chatserver.quasar;

import java.nio.ByteBuffer;
import java.io.IOException;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.data.Msg;
import chatserver.data.MsgType;
import chatserver.data.Util;
import chatserver.data.CommandType;


public class User extends BasicActor<Msg, Void> {
  static int MAXLEN = 1024;
  
  private ActorRef room;
  private String uname;
  final FiberSocketChannel socket;

  public User(ActorRef room, FiberSocketChannel socket, String uname) { 
    this.room = room; 
    this.socket = socket; 
    this.uname=uname;
  }

  protected Void doRun() throws InterruptedException, SuspendExecution { //Exceptions
    Util util = new Util();
    new LineReader(self(), socket).spawn();
    room.send(new Msg(MsgType.ENTER, self(), uname));
    while (receive(msg -> {
      try {
      switch (msg.getType()) {
        case DATA:
          String line = new String((byte[]) msg.getContent());
          if(line.startsWith(":")){
            String[] parts = line.split(" ");
            switch (util.getCommandType(parts[0])){
              //  HELP, LIST_ROOMS, LIST_USERS, CHANGE_ROOM, LOGIN,LOGOUT, UNKNOWN
              case LIST_ROOMS:
                break;
              case LIST_USERS:
                break;
              case LOGIN:
                break;
              case CHANGE_ROOM:
                room.send(new Msg(MsgType.LEAVE, self(),uname));
                ActorRef newroom = new Room("Expc").spawn();
                this.room=newroom;
                room.send(new Msg(MsgType.ENTER, self(),uname));
                break;
              case HELP:
                byte[] uc1 = "Available commands\t".getBytes();
                socket.write(ByteBuffer.wrap(uc1));
                break;
                //Help command: returns a list of all available commands
              case UNKNOWN:
              byte[] uc = "Unknown command\t".getBytes();
              socket.write(ByteBuffer.wrap(uc));
              socket.write(ByteBuffer.wrap((byte[]) msg.getContent()));
              break;
                
            }
        
          } else{
            String mess = new String((byte[]) msg.getContent());
            byte[] messcont= ("@"+uname+": "+mess).getBytes();
            room.send(new Msg(MsgType.LINE,null, messcont));            
          }
          return true;
        case EOF:
        case IOE:
          room.send(new Msg(MsgType.LEAVE, self(),uname));
          socket.close();
          return false;
        case LINE:
            socket.write(ByteBuffer.wrap((byte[]) msg.getContent()));
          return true;
      }
      } catch (IOException e) {
        room.send(new Msg(MsgType.LEAVE, self(),uname));
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
              dest.send(new Msg(MsgType.DATA,null, ba));
            }
          }
          if (eof && !in.hasRemaining()) break;
          in.compact();
        }
        dest.send(new Msg(MsgType.EOF, null,null));
        return null;
      } catch (IOException e) {
        dest.send(new Msg(MsgType.IOE, null,null));
        return null;
      }
    }
  } 
}