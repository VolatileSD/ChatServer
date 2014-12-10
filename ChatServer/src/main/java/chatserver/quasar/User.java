package chatserver.quasar;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.ExecutionException;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.CommandType;
import chatserver.util.Util;
import chatserver.util.Pigeon;

public class User extends BasicActor<Msg, Void> {
  static int MAXLEN = 1024;
  
  private ActorRef room;
  private ActorRef roomManager;
  private ActorRef loginManager;
  private String uname;
  final FiberSocketChannel socket;

  public User(ActorRef room, ActorRef roomManager, ActorRef loginManager, FiberSocketChannel socket) { 
    this.room = room; 
    this.roomManager = roomManager;
    this.loginManager = loginManager;
    this.socket = socket; 
  }

  // where the user logs in
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
            String[] parts = (line.substring(0, line.length()-2)).split(" ");
            switch (util.getCommandType(parts[0])){
              //  HELP, LIST_ROOMS, LIST_USERS, CHANGE_ROOM, LOGIN,LOGOUT, UNKNOWN
              case LIST_ROOMS:
                break;
              case LIST_USERS:
                break;
              case LOGIN:
                if(parts.length != 3) socket.write(ByteBuffer.wrap("Uknown command\n".getBytes()));
                else{
                  Msg reply = new Pigeon(loginManager).carry(MsgType.LOGIN, parts);
                  switch(reply.getType()){
                    case OK:
                      socket.write(ByteBuffer.wrap("OK\n".getBytes()));
                      uname = parts[1];
                      // dont know if this ok is create ok or login ok
                      running();
                    case INVALID:
                      socket.write(ByteBuffer.wrap("INVALID\n".getBytes()));
                      break;
                  }
                }
                break;
              case CHANGE_ROOM:
                // some message: log in first
                break;
              case HELP:
                byte[] uc1 = "Available commands\n".getBytes();
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
      } catch (IOException ioe) { room.send(new Msg(MsgType.LEAVE, self(),uname)); }
      catch (ExecutionException ee) { System.out.println("PIGEON: " + ee.getMessage()); }
      return false;  // stops the actor if some unexpected message is received
    }));
    return null;
  }


  // after login
  protected Void running() throws InterruptedException, SuspendExecution { //Exceptions
    Util util = new Util();
    while (receive(msg -> {
      try {
      switch (msg.getType()) {
        case DATA:
          String line = new String((byte[]) msg.getContent());
          if(line.startsWith(":")){
            String[] parts = (line.substring(0, line.length()-2)).split(" ");
            switch (util.getCommandType(parts[0])){
              //  HELP, LIST_ROOMS, LIST_USERS, CHANGE_ROOM, LOGIN,LOGOUT, UNKNOWN
              case LIST_ROOMS:
                break;
              case LIST_USERS:
                break;
              case LOGIN:
                // some message: you're already logged in
                break;
              case CHANGE_ROOM:
                // instead of this the user should talk to the roomManager
                /*
                Iterator<String> it = ac.map.keySet().iterator();
                while(it.hasNext()){
                  System.out.print((String)it.next());
                }
                byte[] er1= parts[1].getBytes();
                  socket.write(ByteBuffer.wrap(er1));
                  
                  
                if (ac.map.containsKey(parts[1])){ 
                  room.send(new Msg(MsgType.LEAVE, self(),uname));
                  room=ac.map.get(parts[1]);
                  room.send(new Msg(MsgType.ENTER, self(),uname));
                }
                else{
                  byte[] er= "Room does not exist. Try again.\n".getBytes();
                  socket.write(ByteBuffer.wrap(er));
                }
                */
              break;
              case HELP:
                byte[] uc1 = "Available commands\n".getBytes();
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