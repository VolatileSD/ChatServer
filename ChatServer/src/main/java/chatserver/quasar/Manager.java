package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.db.User;

/**
 * Manager deals with the creation, removal and login of users. It also handles
 * private messaging, giving users access to a map that
 *
 *
 *
 */
public class Manager extends BasicActor<Msg, Void> {

   private final Map<String, User> users = new HashMap();

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
         String[] parts = (String[]) msg.getContent();
         switch (msg.getType()) {
            case CREATE:
               if (!users.containsKey(parts[1])) { // If the username does not exists, creates a new user
                  users.put(parts[1], new User(parts[1], parts[2]));
                  msg.getFrom().send(new Msg(MsgType.OK, null, null));
               } else { // if the username is in use, emit a warning
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
               return true;
            case LOGIN:
               if (users.containsKey(parts[1]) && users.get(parts[1]).getPassword().equals(parts[2])) {
                  msg.getFrom().send(new Msg(MsgType.OK, null, null));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
               return true;
            case LOGIN_OK:
               users.get(parts[1]).setActorRef(msg.getFrom());
               return true;
            case PRIVATE:
               ActorRef actref = getUsersRef(parts[1]);
               User dest = users.get(parts[1]);
               String rec = msg.getUsername();
               if (actref != null) {
                  actref.send(new Msg(MsgType.NEW_PRIVATE_MESSAGE, null, rec));
                  msg.getFrom().send(new Msg(MsgType.OK, null, null));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
               if (dest != null) {
                  dest.addMessage(rec, parts[2]);
               }
               return true;
            case INBOX:
               User u = users.get(msg.getUsername());
               if (u != null) {
                  msg.getFrom().send(new Msg(MsgType.OK, null, u.showInbox()));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
               return true;
            case REMOVE:
               // in this case, parts has only 2 elements
               // in this case, could be a only a String instead of String[]
               if (users.containsKey(parts[1])) {
                  users.remove(parts[1]);
                  msg.getFrom().send(new Msg(MsgType.OK, null, null));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
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
    * @return user's ActorRef
    */
   public ActorRef getUsersRef(String user) {
      if (users.containsKey(user)) {
         return (users.get(user)).getActorRef();
      } else {
         return null;
      }
   }

}
