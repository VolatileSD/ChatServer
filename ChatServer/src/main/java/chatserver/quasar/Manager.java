package chatserver.quasar;

import chatserver.db.MessageODB;
import chatserver.db.RoomODB;
import chatserver.db.entity.User;
import chatserver.db.UserODB;
import chatserver.db.entity.Message;
import chatserver.util.Msg;
import chatserver.util.MsgType;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager extends BasicActor<Msg, Void> {
   
   private final Map<String, ActorRef> users = new HashMap();
   private final UserODB userODB = new UserODB();
   private final MessageODB messageODB = new MessageODB();
   private final RoomODB roomODB = new RoomODB();
   
   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
         User user;
         String[] parts = (String[]) msg.getContent();
         switch (msg.getType()) {
            case RESTORE:
               Map<String, String> rooms = roomODB.getActiveRooms();
               Msg restoreMsg = new Msg(MsgType.RESTORE, null, null, rooms);
               msg.getFrom().send(restoreMsg);
               return true;
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
               user = userODB.login(parts[1], parts[2]);
               if (user != null) { // if username and password are correct and the user is not logged in
                  msg.getFrom().send(new Msg(MsgType.OK, null, null, user.getRid()));
                  // send the rid to the user to speed future queries
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case LOGIN_OK:
               users.put(msg.getFromUsername(), msg.getFrom());
               // we will use this actor ref to notify possible private messages
               return true;
            case LOGOUT:
               userODB.logout(parts[0]);
               return true;
            case REMOVE:
               // authenticate first
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
            case CREATE_ROOM:
               chatserver.db.entity.Room room = roomODB.create(parts[0]);
               if (room != null) {
                  msg.getFrom().send(new Msg(MsgType.OK, null, null, room.getRid()));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case LINE:
               // parts = ["roomRid", "message"]
               Message m = messageODB.create(msg.getFromUsername(), parts[1]);
               roomODB.addMessage(parts[0], m);
               return true;
         }
         return false;
      }));
      return null;
   }
}
