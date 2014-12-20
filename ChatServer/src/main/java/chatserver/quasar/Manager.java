package chatserver.quasar;

import chatserver.db.MessageDB;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.db.UserDB;

/**
 * Manager deals with the creation, removal and login of users. It also handles
 * private messaging, giving users access to a map that
 *
 *
 *
 */
public class Manager extends BasicActor<Msg, Void> {

   private final Map<String, UserDB> users = new HashMap();

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {

         System.out.println(msg.toString());

         String[] parts = (String[]) msg.getContent();
         // what if msg.getContent is null?
         // btw: is it okay to send string[]?
         // concurrent exception is possible?
         switch (msg.getType()) {
            case CREATE:
               if (!users.containsKey(parts[1])) { // If the username does not exists, creates a new user
                  users.put(parts[1], new UserDB(parts[1], parts[2]));
                  msg.getFrom().send(new Msg(MsgType.OK));
               } else { // if the username is in use, emit a warning
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case LOGIN:
               if (users.containsKey(parts[1])) {
                  UserDB user = users.get(parts[1]);
                  if (user.getPassword().equals(parts[2])) {
                     msg.getFrom().send(new Msg(MsgType.OK));
                     user.setLoggedIn(true);
                  }
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case LOGIN_OK: // login_ok?
               users.get(msg.getFromUsername()).setActorRef(msg.getFrom());
               return true;
            case PRIVATE:
               // parts here is something like: ["usernameTo", "message"]
               if (users.containsKey(parts[0])) {
                  msg.getFrom().send(new Msg(MsgType.OK));
                  UserDB dest = users.get(parts[0]);
                  dest.addPrivateMessage(msg.getFromUsername(), parts[1]);
                  if (dest.isLoggedIn()) {
                     dest.getActorRef().send(new Msg(MsgType.PRIVATE, null, msg.getFromUsername(), null));
                  }
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case INBOX:
               // if a user can do :inbox then he is in the users map
               // so this will always enter in the first can of the if
               UserDB u = users.get(msg.getFromUsername());
               msg.getFrom().send(new Msg(MsgType.OK, null, null, u.showInbox()));
               return true;
            case REMOVE:
               // in this case, parts has only 2 elements
               // in this case, could be a only a String instead of String[]
               if (users.containsKey(parts[1])) {
                  users.remove(parts[1]);
                  msg.getFrom().send(new Msg(MsgType.OK));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case ENTER: // when someone enters a room we add to the user's history the message number of that room
               // same with leave
               return true;
            case LEAVE:
               return true;
         }
         return false;
      }));
      return null;
   }

   /**
    * Returns an user ActorRef given the username.
    *
    * @param user username of the user
    * @return user's s
    */
   public ActorRef getUsersRef(String user) {
      // this method was used evertime we had an instance of db.User
      // we could just call db.User.getActorRef() right?
      if (users.containsKey(user)) {
         return (users.get(user)).getActorRef();
      } else {
         return null;
      }
   }

}
