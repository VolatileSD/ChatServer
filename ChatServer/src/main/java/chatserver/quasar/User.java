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
import chatserver.util.State;
import chatserver.util.Pigeon;
import chatserver.gui.AdminGUI;

public class User extends BasicActor<Msg, Void> {
  static int MAXLEN = 1024;
  
  private ActorRef room;
  private ActorRef roomManager;
  private ActorRef loginManager;
  private String uname;
  private State state;
  final FiberSocketChannel socket;

  public User(ActorRef room, ActorRef roomManager, ActorRef loginManager, FiberSocketChannel socket) { 
    this.room = room; 
    this.roomManager = roomManager;
    this.loginManager = loginManager;
    this.socket = socket; 
    state = State.LOGGED_OUT;
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
            String[] parts = (line.substring(0, line.length()-2)).split(" ");
            switch (util.getCommandType(parts[0])){
              //  HELP, LIST_ROOMS, LIST_USERS, CHANGE_ROOM, LOGIN,LOGOUT, UNKNOWN
              case LIST_ROOMS:
                break;
              case LIST_USERS:
                break;
              case CREATE:
                create(parts);
                break;
              case LOGIN:
                // check if is already logged in
                boolean b = login(parts);
                if(b) state = State.LOGGED_IN;
                break;
              case CHANGE_ROOM:
                // check if is already logged in
                break;
              case HELP:
                say("Available commands\n");
                //Help command: returns a list of all available commands
                break;
              case UNKNOWN:
                say("Unknown Command\n");
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
          say((byte[]) msg.getContent());
          return true;
      }
      } 
      catch (IOException ioe) { room.send(new Msg(MsgType.LEAVE, self(),uname)); }
      catch (ExecutionException ee) { System.out.println("PIGEON: " + ee.getMessage()); }
      
      return false;  // stops the actor if some unexpected message is received
    }));
    return null;
  }

  void create(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution{
    if(parts.length != 3) say("Unknown Command\n");
    else{
      Msg reply = new Pigeon(loginManager).carry(MsgType.CREATE, parts);
      switch(reply.getType()){
        case OK:
          say("OK\n");
          break;
        case INVALID:
          say("INVALID\n");
          break;
      }
    }
  }

  boolean login(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution{
    boolean b = false;
    if(parts.length != 3) say("Unknown Command\n");
    else{
      Msg reply = new Pigeon(loginManager).carry(MsgType.LOGIN, parts);
      switch(reply.getType()){
        case OK:
          say("OK\n");
          setUsername(parts[1]);
          b = true;
          break;
        case INVALID:
          say("INVALID\n");
          break;
      }
    }

    return b;
  }

  void say(byte[] whatToSay) throws IOException, SuspendExecution{
    socket.write(ByteBuffer.wrap(whatToSay));
  }

  void say(String whatToSay) throws IOException, SuspendExecution{
    say(whatToSay.getBytes());
  }

  void setUsername(String uname){
    this.uname = uname;
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