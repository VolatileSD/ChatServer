package chatserver.quasar;

import java.nio.ByteBuffer;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Util;
import chatserver.util.Pigeon;

public class User extends BasicActor<Msg, Void> {

   private static final int MAXLEN = 1024;

   private ActorRef room;
   private final ActorRef roomManager;
   private final ActorRef manager;
   private String username;
   private final FiberSocketChannel socket;
   private final Util util;

   public User(ActorRef room, ActorRef roomManager, ActorRef manager, FiberSocketChannel socket) {
      this.room = room;
      this.roomManager = roomManager;
      this.manager = manager;
      this.socket = socket;
      this.util = new Util();
   }

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      new LineReader(self(), socket).spawn();
      runLogin();

      return null;
   }

   @SuppressWarnings("empty-statement")
   protected void runLogin() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
         try {
            switch (msg.getType()) {
               case DATA:
                  String line = new String((byte[]) msg.getContent());
                  String[] parts = (line.substring(0, line.length() - 2)).split(" ");
                  if (line.startsWith(":")) {
                     switch (util.getCommandType(parts[0])) {
                        case CREATE:
                           System.out.println("CASE LOGIN");
                           create(parts);
                           return true;
                        case LOGIN:
                           login(parts);
                           return true;
                        case REMOVE:
                           //remove(parts);
                           return true;
                        case HELP:
                           say("Available commands\n:create user pass\n:login user pass - if you already registered\n");
                           return true;
                     }
                  } else {
                     say("You have to login to chat. Type :h for help.\n");
                  }
                  return true;
               case EOF:
               case IOE:
                  return false;
               case LINE:
                  say((byte[]) msg.getContent());
                  return true;
            }
         } catch (IOException ioe) { // do something?
         } catch (ExecutionException ee) {
            System.out.println("PIGEON: " + ee.getMessage());
         }

         return false;  // stops the actor if some unexpected message is received
      }));
   }

   @SuppressWarnings("empty-statement")
   protected void runChat() throws InterruptedException, SuspendExecution { //Exceptions
      while (receive(msg -> {
         try {
            switch (msg.getType()) {
               case DATA:
                  String line = new String((byte[]) msg.getContent());
                  String[] parts = (line.substring(0, line.length() - 2)).split(" ");
                  if (line.startsWith(":")) {
                     switch (util.getCommandType(parts[0])) {
                        case CREATE:
                           say("You are signed in. Logout first to create another account\n");
                           break;
                        case LOGIN:
                           // check if is already logged in
                           say("You are already logged in.");
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
                        case PRIVATE:
                           privateMessage(parts);
                           break;
                        case INBOX:
                           readInbox();
                           break;
                        case HELP:
                           say("Available commands\n");
                           //Help command: returns a list of all available commands
                           break;
                        case UNKNOWN:
                           say("Unknown Command\n");
                           break;
                     }
                  } else {
                     byte[] messcont = ("@" + username + ": " + line).getBytes();
                     room.send(new Msg(MsgType.LINE, null, messcont));
                  }
                  return true;
               case LINE:
                  say((byte[]) msg.getContent());
                  return true;
               case NEW_PRIVATE_MESSAGE:
                  say("You've got a message from " + msg.getContent() + ". Type :inbox to read it.\n");
                  return true;
               case EOF:
               case IOE:
                  room.send(new Msg(MsgType.LEAVE, self(), username));
                  socket.close();
                  return false;
            }
         } catch (IOException ioe) {
            room.send(new Msg(MsgType.LEAVE, self(), null));
         } catch (ExecutionException ee) {
            System.out.println("PIGEON: " + ee.getMessage());
         }

         return false;  // stops the actor if some unexpected message is received
      }));
   }

   private void create(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      System.out.println("CREATE FUNCTION");
      if (parts.length != 3) {
         say("Unknown Command\n");
      } else {
         Pigeon pigeon = new Pigeon(manager);
         Msg reply = pigeon.carry(MsgType.CREATE, parts);
         switch (reply.getType()) {
            case OK:
               say("New user " + parts[1] + " created successfully\n");
               break;
            case INVALID:
               say("Username already exists\n");
               break;
         }
      }
   }

   private void login(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      boolean b = false;
      if (parts.length != 3) {
         say("Unknown Command\n");
      } else {
         Msg reply = new Pigeon(manager).carry(MsgType.LOGIN, parts);
         switch (reply.getType()) {
            case OK:
               say(parts[1] + ", you are logged in.\n");
               setUsername(parts[1]);
               room.send(new Msg(MsgType.ENTER, self(), username));
               manager.send(new Msg(MsgType.LOGIN_OK, self(), parts)); // sends itself to room manager
               runChat();
               break;
            case INVALID:
               say("Login invalid. Check valid username or password.\n");
               break;
         }
      }
   }

   private void logout(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 1) {
         say("Unknown Command\n");
      } else {
         runLogin();
         // update Manager map : set loggedIn = false;
      }
   }

   private void changeRoom(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 2) {
         say("Uknown Command\n");
      } else {
         String[] roomAndUsername = new String[2];
         roomAndUsername[0] = parts[1];
         roomAndUsername[1] = username;
         Msg reply = new Pigeon(roomManager).carry(MsgType.CHANGE_ROOM, roomAndUsername);
         switch (reply.getType()) {
            case OK:
               room.send(new Msg(MsgType.LEAVE, self(), username));
               room = reply.getFrom();
               room.send(new Msg(MsgType.ENTER, self(), username));
               say("Room changed successfully.\n");
               break;
            case INVALID:
               say("Room " + parts[1] + " does not exist\n");
               break;
         }
      }
   }

   private void privateMessage(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 3) { // ? what if the message has more than one word? maybe < 3 ?
         say("Unknown Command\n");
      } else {
         Msg reply = new Pigeon(manager).carry(MsgType.PRIVATE, parts, username);
         switch (reply.getType()) {
            case OK:
               say("Message successfully sent to " + parts[1] + ".\n");
               break;
            case INVALID:
               say("Unknown user " + parts[1] + ".\n");
               break;
         }
      }
   }

   private void readInbox() throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      Msg reply = new Pigeon(manager).carry(MsgType.INBOX, null, username);
      switch (reply.getType()) {
         case OK:
            say(" -------------------\n");
            say("|   Inbox Content   |\n");
            say(" -------------------\n");
            say(reply.getContent() + "\n");
            break;
         case INVALID:
            say("Something went wrong.\n");
            break;
      }

   }

   private void say(byte[] whatToSay) throws IOException, SuspendExecution {
      socket.write(ByteBuffer.wrap(whatToSay));
   }

   private void say(String whatToSay) throws IOException, SuspendExecution {
      say(whatToSay.getBytes());
   }

   private void setUsername(String username) {
      this.username = username;
   }

   static class LineReader extends BasicActor<Msg, Void> {

      final ActorRef<Msg> dest;
      final FiberSocketChannel socket;
      ByteBuffer in = ByteBuffer.allocate(MAXLEN);
      ByteBuffer out = ByteBuffer.allocate(MAXLEN);

      LineReader(ActorRef<Msg> dest, FiberSocketChannel socket) {
         this.dest = dest;
         this.socket = socket;
      }

      @Override
      protected Void doRun() throws InterruptedException, SuspendExecution {
         boolean eof = false;
         byte b = 0;
         try {
            for (;;) {
               if (socket.read(in) <= 0) {
                  eof = true;
               }
               in.flip();
               while (in.hasRemaining()) {
                  b = in.get();
                  out.put(b);
                  if (b == '\n') {
                     break;
                  }
               }
               if (eof || b == '\n') { // send line
                  out.flip();
                  if (out.remaining() > 0) {
                     byte[] ba = new byte[out.remaining()];
                     out.get(ba);
                     out.clear();
                     dest.send(new Msg(MsgType.DATA, null, ba));
                  }
               }
               if (eof && !in.hasRemaining()) {
                  break;
               }
               in.compact();
            }
            dest.send(new Msg(MsgType.EOF, null, null));
            return null;
         } catch (IOException e) {
            dest.send(new Msg(MsgType.IOE, null, null));
            return null;
         }
      }
   }
}
