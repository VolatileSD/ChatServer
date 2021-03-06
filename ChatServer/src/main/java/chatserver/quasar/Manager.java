package chatserver.quasar;

import chatserver.db.MessageODB;
import chatserver.db.RoomODB;
import chatserver.db.UserODB;
import chatserver.db.entity.Message;
import chatserver.util.Msg;
import chatserver.util.MsgType;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import common.representation.TalkRepresentation;
import common.representation.UsersRepresentation;
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
         chatserver.db.entity.User user;
         String[] parts = (String[]) msg.getContent();
         switch (msg.getType()) {
            case RESTORE:
               userODB.logoutEveryone();
               // map<roomName, roomRid>
               Map<String, String> rooms = roomODB.getActiveRooms();
               msg.getFrom().send(new Msg(MsgType.RESTORE, null, null, rooms));
               return true;
            case CREATE:
               user = userODB.create(parts[1], parts[2], false);
               if (user != null) {
                  msg.getFrom().send(new Msg(MsgType.OK));
               } else { // if the username is in use, emit a warning
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case CREATE_ADMIN: // maybe add an option MAKE_ADMIN
               user = userODB.create(parts[1], parts[2], true);
               if (user != null) {
                  msg.getFrom().send(new Msg(MsgType.OK));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case REMOVE:
               user = userODB.remove(parts[1], parts[2]);
               if (user != null) {
                  msg.getFrom().send(new Msg(MsgType.OK));
               } else { // if password is wrong or the user is already inactive
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case LOGIN:
               user = userODB.login(parts[1], parts[2]);
               if (user != null) { // if username and password are correct and the user is not logged in
                  MsgType isAdmin = user.isAdmin() ? MsgType.ADMIN_OK : MsgType.OK;
                  msg.getFrom().send(new Msg(isAdmin, null, null, user.getRid()));
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
               users.remove(msg.getFromUsername());
               userODB.logout(parts[0]);
               return true;
            case MAKE_ADMIN:
               if (userODB.isAdmin(parts[1], parts[2])) {
                  user = userODB.makeAdmin(parts[0]);
                  if (user != null) {
                     msg.getFrom().send(new Msg(MsgType.OK));
                  } else {
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
               } else {
                  msg.getFrom().send(new Msg(MsgType.UNAUTHORIZED));
               }
               return true;
            case REMOVE_ADMIN:
               if (userODB.isAdmin(parts[1], parts[2])) {
                  user = userODB.removeAdmin(parts[0]);
                  if(user != null){
                     msg.getFrom().send(new Msg(MsgType.OK));
                  } else {
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
               } else {
                  msg.getFrom().send(new Msg(MsgType.UNAUTHORIZED));
               }
            case AUTH:
               // parts = [roomName, username, password]
               Msg isAdmin = userODB.isAdmin(parts[1], parts[2]) ? new Msg(MsgType.OK) : new Msg(MsgType.INVALID);
               msg.getFrom().send(isAdmin);
            case CREATE_ROOM:
               chatserver.db.entity.Room room = roomODB.create(parts[0]);
               if (room != null) {
                  msg.getFrom().send(new Msg(MsgType.OK, null, null, room.getRid()));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               }
               return true;
            case DELETE_ROOM:
               roomODB.setActive(parts[0], false);
               return true;
            case LINE:
               if (parts[0] != null) { // because of main room
                  // parts = ["roomRid", "message"]
                  Message m = messageODB.create(msg.getFromUsername(), "", parts[1]);
                  roomODB.addMessage(parts[0], m);
               }
               return true;
            case PRIVATE:
               // parts here is something like: ["usernameTo", "message"]
               user = userODB.findByUsername(parts[0]);
               if (user != null) {
                  chatserver.db.entity.User userFrom = userODB.findByRid(parts[2]);
                  msg.getFrom().send(new Msg(MsgType.OK));
                  Message message = messageODB.create(msg.getFromUsername(), parts[0], parts[1]);
                  userODB.addPrivateMessage(userFrom, user, message);
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
            case INBOX_USERS:
               UsersRepresentation inboxUsers = userODB.getInboxUsers(parts[0]);
               msg.getFrom().send(new Msg(MsgType.OK, null, null, inboxUsers));
               return true;
            case TALK:
               TalkRepresentation talk = userODB.getTalk(parts[0], parts[1], parts[2]);
               msg.getFrom().send(new Msg(MsgType.OK, null, null, talk));
               return true;
         }
         return false;
      }));
      return null;
   }
}
