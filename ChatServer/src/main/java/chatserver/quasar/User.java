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
  private String username;
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
    String welcomeMessage = ("------ Welcome to an awesome chat service! ------\n #Please login to chat. Type :h for help.\n");
    try{
    say(welcomeMessage);
    }
    catch (IOException ee) {System.out.println("error" + ee.getMessage());}
    new LineReader(self(), socket).spawn();
    while (receive(msg -> {
      try {
      switch (msg.getType()) {
        case DATA:
          String line = new String((byte[]) msg.getContent());
          String[] parts = (line.substring(0, line.length()-2)).split(" ");
          if(state==State.LOGGED_IN){
            Util util = new Util();
            if(line.startsWith(":")){
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
                  say("you are already logged in.");
                  break;
                case LOGOUT:
                  // check if is already logged out
                  // if is logged in, send a LEAVE message to his room
                  logout(parts);
                  break;
                case CHANGE_ROOM:
                  // check if is already logged in
                  changeRoom(parts);
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
            byte[] messcont= ("@"+username+": "+line).getBytes();
            room.send(new Msg(MsgType.LINE,null, messcont));
          }
          }
          else{
            if(line.startsWith(":login")){login(parts);}
            else if(line.startsWith(":create")){create(parts);}
            else if (line.startsWith(":h")){say("Available commands\n :create user pass - if you already registered \n :login user pass\n");}
            else{say("You have to login to chat. Type :h for help.\n");}
          }
        return true;
        case EOF:
        case IOE:
          room.send(new Msg(MsgType.LEAVE, self(),username));
          socket.close();
          return false;
        case LINE:
          say((byte[]) msg.getContent());
          return true;
      } }
      catch (IOException ioe) { room.send(new Msg(MsgType.LEAVE, self(),username)); }
      catch (ExecutionException ee) { System.out.println("PIGEON: " + ee.getMessage()); }
      
      return false;  // stops the actor if some unexpected message is received
    }));
    return null;
  }
  
  

  private void create(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution{
    if(parts.length != 3) say("Unknown Command "+ parts[0]+"\n");
    else{
      Msg reply = new Pigeon(loginManager).carry(MsgType.CREATE, parts);
      switch(reply.getType()){
        case OK:
          say("New user "+parts[1]+" created successfully\n");
          break;
        case INVALID:
          say("Something went wrong\n");
          break;
      }
    }
  }

  private void login(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution{
    boolean b = false;
    if(parts.length != 3) say("Unknown Command "+ parts[0]+"\n");
    else{
      Msg reply = new Pigeon(loginManager).carry(MsgType.LOGIN, parts);
      switch(reply.getType()){
        case OK:
          say("User "+parts[1]+", you are logged in.\n" );
          setUsername(parts[1]);
          state = State.LOGGED_IN;
          room.send(new Msg(MsgType.ENTER, self(), username));
          break;
        case INVALID:
          say("Login invalid. Check valid username or password.\n");
          break;
      }
    }

  }
  
  private void logout(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution{
    if(parts.length != 1) say("Unknown Command\n");
    else state = State.LOGGED_OUT;
  }

  private void changeRoom(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution{
    if(parts.length != 2) say("Uknown Command\n");
    else{
      String[] roomAndUsername = new String[2];
      roomAndUsername[0] = parts[1];
      roomAndUsername[1] = username;
      Msg reply = new Pigeon(roomManager).carry(MsgType.CHANGE_ROOM, roomAndUsername);
      System.out.print("Changing room");
      switch(reply.getType()){
        case OK:
          room.send(new Msg(MsgType.LEAVE, self(),username));
          room = reply.getFrom();
          room.send(new Msg(MsgType.ENTER, self(), username));
          say("Room changed successfully.\n");
          break;
        case INVALID:
          say("Room " + parts[1] + " does not exists\n");
          break;
      }
    }
  }

  private void say(byte[] whatToSay) throws IOException, SuspendExecution{
    socket.write(ByteBuffer.wrap(whatToSay));
  }

  private void say(String whatToSay) throws IOException, SuspendExecution{
    say(whatToSay.getBytes());
  }

  private String getUsername(){
    return this.username;
  }

  private void setUsername(String username){
    this.username = username;
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