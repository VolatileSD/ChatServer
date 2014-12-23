package chatserver.quasar;

import chatserver.db.MessageODB;
import chatserver.db.entity.User;
import chatserver.db.UserODB;
import chatserver.db.entity.Message;
import chatserver.util.Msg;
import chatserver.util.MsgType;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager extends BasicActor<Msg, Void> {

   private final Map<String, ActorRef> users = new HashMap();
   private final UserODB userODB = new UserODB();
   private final MessageODB messageODB = new MessageODB();

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
         User user;
         String[] parts = (String[]) msg.getContent();
         switch (msg.getType()) {
            case CREATE:
               user = userODB.findByUsername(parts[1]);
               if (user == null) { // If the username does not exists, creates a new user
                  userODB.create(parts[1], parts[2]);
                  msg.getFrom().send(new Msg(MsgType.OK));
               } else { // if the username is in use, emit a warning
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case LOGIN:
               user = userODB.findByUsernameAndPassword(parts[1], parts[2]);
               if (user != null) {
                  msg.getFrom().send(new Msg(MsgType.OK, null, null, user.getRid()));
                  // send the rid to the user to speed future queries
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case LOGIN_OK:
               users.put(msg.getFromUsername(), msg.getFrom());
               return true;
            case PRIVATE:
               // parts here is something like: ["usernameTo", "message"]
               user = userODB.findByUsername(parts[0]);
               if (user != null) {
                  msg.getFrom().send(new Msg(MsgType.OK));
                  Message message = messageODB.create(msg.getFromUsername(), parts[1]);
                  userODB.addPrivateMessage(user, message);
                  if (users.containsKey(parts[0])) { // if its logged in
                     users.get(parts[0]).send(new Msg(MsgType.PRIVATE, null, msg.getFromUsername(), null));
                  }
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case INBOX:
               List<Message> inbox = userODB.getInbox(parts[0]);
               msg.getFrom().send(new Msg(MsgType.OK, null, null, inbox));
               return true;
             /*
             case REMOVE:
             // authenticate first
             return true;
             case LINE:
             rooms.get(parts[0]).addMessage(msg.getFromUsername(), parts[1]);
             return true;
               */
         }
         return false;
      }));
      return null;
   }
}
