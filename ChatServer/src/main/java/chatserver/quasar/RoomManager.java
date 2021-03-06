package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RoomManager extends BasicActor<Msg, Void> {

   private static final Logger logger = Logger.getLogger(RoomManager.class.getName());
   private final Map<String, ActorRef> rooms = new HashMap();
   private final ActorRef manager;
   private final ActorRef notificationManager;

   public RoomManager(ActorRef manager, ActorRef notificationManager) {
      this.manager = manager;
      this.notificationManager = notificationManager;
   }

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {

      while (receive(msg -> {
         try {
            String roomName;
            Msg isAdmin;
            String[] content;
            switch (msg.getType()) {
               case RESTORE:
                  Map<String, String> activeRooms = (Map) msg.getContent();
                  for (String s : activeRooms.keySet()) {
                     ActorRef roomRef = new Room(s, activeRooms.get(s), manager, notificationManager).spawn();
                     rooms.put(s, roomRef);
                  }
                  msg.getFrom().send(new Msg(MsgType.RESTORE, null, null, rooms));
                  return true;
               case CHANGE_ROOM:
                  roomName = (String) msg.getContent();
                  if (rooms.containsKey(roomName)) {
                     Msg reply = new Pigeon(rooms.get(roomName)).carry(MsgType.CHANGE_ROOM);
                     msg.getFrom().send(reply);
                  } else {
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
                  return true;
               case ROOM_INFO:
                  roomName = (String) msg.getContent();
                  if (rooms.containsKey(roomName)) {
                     // he already tested this in rest
                     Msg reply = new Pigeon(rooms.get(roomName)).carry(MsgType.ROOM_INFO);
                     msg.getFrom().send(reply);
                  } else {
                     // so if it enters here there's a big problem
                     logger.severe("Error trying to show info about an existing room");
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
                  return true;
               case CREATE_ROOM:
                  content = (String[]) msg.getContent();
                  isAdmin = new Pigeon(manager).carry(MsgType.AUTH, null, content);
                  switch (isAdmin.getType()) {
                     case OK:
                        roomName = content[0];
                        if (!rooms.containsKey(roomName)) {
                           Msg reply = new Pigeon(manager).carry(MsgType.CREATE_ROOM, null, new String[]{roomName});
                           msg.getFrom().send(reply);
                           switch (reply.getType()) {
                              case OK:
                                 rooms.put(roomName, new Room(roomName, (String) reply.getContent(), manager, notificationManager).spawn());
                                 notificationManager.send(msg);
                                 logger.info("Room Successfully created in DB");
                                 break;
                              case INVALID:
                                 logger.severe("DB error trying to create a room");
                                 break;
                           }
                        } else {
                           msg.getFrom().send(new Msg(MsgType.INVALID));
                        }
                        break;
                     case INVALID:
                        msg.getFrom().send(new Msg(MsgType.UNAUTHORIZED));
                        break;
                  }
                  return true;
               case DELETE_ROOM:
                  content = (String[]) msg.getContent();
                  isAdmin = new Pigeon(manager).carry(MsgType.AUTH, null, content);
                  switch (isAdmin.getType()) {
                     case OK:
                        roomName = content[0];
                        if (rooms.containsKey(roomName)) {
                           Msg reply = new Pigeon(rooms.get(roomName)).carry(MsgType.DELETE_ROOM);
                           msg.getFrom().send(reply);
                           if (reply.getType().equals(MsgType.OK)) {
                              rooms.remove(roomName);
                              notificationManager.send(msg);
                              manager.send(new Msg(MsgType.DELETE_ROOM, null, null, reply.getContent()));
                           }
                        } else {
                           msg.getFrom().send(new Msg(MsgType.INVALID));
                        }
                        break;
                     case INVALID:
                        msg.getFrom().send(new Msg(MsgType.UNAUTHORIZED));
                        break;
                  }
                  return true;
            }
         } catch (ExecutionException ee) {
            logger.log(Level.SEVERE, "Pigeon error - {0}", ee.getMessage());
         }
         return false;
      }));
      return null;
   }
}
