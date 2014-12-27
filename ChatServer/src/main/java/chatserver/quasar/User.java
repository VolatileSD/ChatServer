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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User extends BasicActor<Msg, Void> {

   private static final int MAXLEN = 1024;
   private static final Logger logger = Logger.getLogger(User.class.getName());

   private ActorRef room;
   private final ActorRef roomManager;
   private final ActorRef manager;
   private String rid;
   private String username;
   private final FiberSocketChannel socket;

   public User(ActorRef room, ActorRef roomManager, ActorRef manager, FiberSocketChannel socket) {
      this.room = room;
      this.roomManager = roomManager;
      this.manager = manager;
      this.socket = socket;
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
                  String[] parts = (line.substring(0, line.length() - 1)).split(" ");
                  //for (char c : line.toCharArray()) {
                  //   logger.log(Level.INFO, "!{0}!", c);
                  //}
                  if (line.startsWith(":")) {
                     switch (Util.getCommandType(parts[0])) {
                        case CREATE:
                           create(parts);
                           return true;
                        case REMOVE:
                           remove(parts);
                           return true;
                        case LOGIN:
                           login(parts);
                           return true;
                        case HELP:
                           say("Available commands\n:create user pass\n:login user pass - if you already registered\n:remove user pass\n");
                           return true;
                        default:
                           say("Login before anything else.\n");
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
            logger.log(Level.SEVERE, "Pigeon error - {0}", ee.getMessage());
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
                  String[] parts = (line.substring(0, line.length() - 1)).split(" ");
                  if (line.startsWith(":")) {
                     switch (Util.getCommandType(parts[0])) {
                        case CREATE:
                           say("You are signed in. Logout first to create another account.\n");
                           break;
                        case REMOVE:
                           say("Logout first.\n");
                           break;
                        case LOGIN:
                           say("You are already logged in.\n");
                           break;
                        case LOGOUT:
                           logout(parts);
                           break;
                        case CHANGE_ROOM:
                           changeRoom(parts);
                           break;
                        case PRIVATE:
                           privateMessage(parts);
                           break;
                        case INBOX:
                           readInbox();
                           break;
                        case HELP:
                           say("Available commands.\n");
                           //Help command: returns a list of all available commands
                           break;
                        case UNKNOWN:
                           say("Unknown Command.\n");
                           break;
                     }
                  } else {
                     room.send(new Msg(MsgType.LINE, null, username, line));
                  }
                  return true;
               case LINE:
                  say((byte[]) msg.getContent());
                  return true;
               case PRIVATE:
                  say(new StringBuilder("You've got a message from @").append(msg.getFromUsername()).append(". Type :inbox to read it.\n").toString());
                  return true;
               case EOF:
               case IOE:
                  room.send(new Msg(MsgType.LEAVE, self(), username, null));
                  manager.send(new Msg(MsgType.LOGOUT, null, null, new String[]{rid}));
                  socket.close();
                  return false;
            }
         } catch (IOException ioe) {
            room.send(new Msg(MsgType.LEAVE, self(), username, null));
            manager.send(new Msg(MsgType.LOGOUT, null, null, new String[]{rid}));
         } catch (ExecutionException ee) {
            logger.log(Level.SEVERE, "Pigeon error - {0}", ee.getMessage());
         }

         return false;  // stops the actor if some unexpected message is received
      }));
   }

   private void create(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 3) {
         say("Unknown Command.\n");
      } else {
         Msg reply = new Pigeon(manager).carry(MsgType.CREATE, null, parts);
         switch (reply.getType()) {
            case OK:
               ok();
               //say(new StringBuilder("New user @").append(parts[1]).append(" created successfully.\n").toString());
               break;
            case INVALID:
               ko();
               //say("Username already exists.\n");
               break;
         }
      }
   }

   private void remove(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 3) {
         say("Unknown Command.\n");
      } else {
         Msg reply = new Pigeon(manager).carry(MsgType.REMOVE, null, parts);
         switch (reply.getType()) {
            case OK:
               ok();
               //say("User removed successfully.\n");
               room.send(new Msg(MsgType.LEAVE, self(), username, null));
               runLogin();
               break;
            case INVALID:
               ko();
               //say("Invalid.\n");
               // the password could be wrong
               // or the user could be already inactive
               break;
         }
      }

   }

   private void login(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 3) {
         say("Unknown Command.\n");
      } else {
         Msg reply = new Pigeon(manager).carry(MsgType.LOGIN, null, parts);
         switch (reply.getType()) {
            case OK:
               //say(parts[1] + ", you are logged in.\n");
               ok();
               setUsername(parts[1]);
               setRid((String) reply.getContent());
               room.send(new Msg(MsgType.ENTER, self(), username, null));
               manager.send(new Msg(MsgType.LOGIN_OK, self(), username, null)); // sends its actoref to manager
               runChat();
               break;
            case INVALID:
               ko();
               //say("Login invalid. Check valid username or password.\n");
               break;
         }
      }
   }

   private void logout(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 1) {
         say("Unknown Command.\n");
      } else {
         room.send(new Msg(MsgType.LEAVE, self(), username, null));
         manager.send(new Msg(MsgType.LOGOUT, null, null, new String[]{rid}));
         //ok();
         //say("Successfully logged out.\n");
         runLogin();
      }
   }

   private void changeRoom(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length != 2) {
         say("Uknown Command\n");
      } else {
         Msg reply = new Pigeon(roomManager).carry(MsgType.CHANGE_ROOM, username, parts[1]);
         switch (reply.getType()) {
            case OK:
               room.send(new Msg(MsgType.LEAVE, self(), username, null));
               room = reply.getFrom();
               room.send(new Msg(MsgType.ENTER, self(), username, null));
               say("Room changed successfully.\n");
               break;
            case INVALID:
               say(new StringBuilder("Room ").append(parts[1]).append(" does not exist\n").toString());
               break;
         }
      }
   }

   private void privateMessage(String[] parts) throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      if (parts.length < 3) {
         say("Unknown Command\n");
      } else {
         StringBuilder sb = new StringBuilder();
         // problem: two consecutive spaces will be one space now, e.g
         for (int i = 2; i < parts.length; i++) {
            sb.append(parts[i]).append(" ");
         }
         // the way it is now, i can send a message to myself
         // is that ok?
         Msg reply = new Pigeon(manager).carry(MsgType.PRIVATE, username, new String[]{parts[1], sb.toString()});
         switch (reply.getType()) {
            case OK:
               say(new StringBuilder("Message successfully sent to @").append(parts[1]).append(".\n").toString());
               break;
            case INVALID:
               say(new StringBuilder("Unknown user @").append("parts[1").append(".\n").toString());
               break;
         }
      }
   }

   private void readInbox() throws IOException, ExecutionException, InterruptedException, SuspendExecution {
      Msg reply = new Pigeon(manager).carry(MsgType.INBOX, null, new String[]{rid});
      switch (reply.getType()) {
         case OK:
            List<chatserver.db.entity.Message> inbox = (List) reply.getContent();
            if (inbox.isEmpty()) {
               say("Your inbox is empty.\n");
            } else {
               StringBuilder sb = new StringBuilder();
               sb.append(" -------------------\n");
               sb.append("|   Inbox Content   |\n");
               sb.append(" -------------------\n\n");
               for (chatserver.db.entity.Message m : inbox) {
                  sb.append(m.toString()).append("\n");
               }

               say(sb.toString());
            }
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
   
   private void ok() throws IOException, SuspendExecution{
      say(":ok");
   }
   
   private void ko() throws IOException, SuspendExecution{
      say(":ko");
   }

   private void setRid(String rid) {
      this.rid = rid;
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
                     dest.send(new Msg(MsgType.DATA, null, null, ba));
                  }
               }
               if (eof && !in.hasRemaining()) {
                  break;
               }
               in.compact();
            }
            dest.send(new Msg(MsgType.EOF));
            return null;
         } catch (IOException e) {
            dest.send(new Msg(MsgType.IOE));
            return null;
         }
      }
   }
}
