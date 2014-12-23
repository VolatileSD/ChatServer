package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;

public class RoomManager extends BasicActor<Msg, Void> {

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
            String roomName = (String) msg.getContent();
            switch (msg.getType()) {
               case ROOM_INFO:
                  if (rooms.containsKey(roomName)) {
                     // he already tested this in rest
                     Msg reply = new Pigeon(rooms.get(roomName)).carry(MsgType.ROOM_INFO);
                     msg.getFrom().send(reply);
                  } else {
                     // so if it enters here there's a big problem
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
                  return true;
               case CREATE_ROOM:
                  if (!rooms.containsKey(roomName)) {
                     Msg reply = new Pigeon(manager).carry(MsgType.CREATE_ROOM, null, roomName);
                     switch (msg.getType()) {
                        case OK:
                           msg.getFrom().send(new Msg(MsgType.OK));
                           ActorRef newRoomRef = new Room(roomName, (String) reply.getContent(), manager, notificationManager).spawn();
                           rooms.put(roomName, newRoomRef);
                           notificationManager.send(msg);
                           break;
                        case INVALID: // db problem
                           // maybe instead of invalid return a new message type
                           // internal error or something
                           msg.getFrom().send(new Msg(MsgType.INVALID));
                           break;
                     }
                  } else {
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
                  return true;
               case DELETE_ROOM:
                  if (rooms.containsKey(roomName)) {
                     Msg reply = new Pigeon(rooms.get(roomName)).carry(MsgType.DELETE_ROOM);
                     if (reply.getType().equals(MsgType.OK)) {
                        rooms.remove(roomName);
                     }
                     msg.getFrom().send(reply);
                  } else {
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
                  return true;
               case CHANGE_ROOM:
                  if (rooms.containsKey(roomName)) {
                     Msg reply = new Pigeon(rooms.get(roomName)).carry(MsgType.CHANGE_ROOM);
                     msg.getFrom().send(reply);
                  } else {
                     msg.getFrom().send(new Msg(MsgType.INVALID));
                  }
                  return true;
            }
         } catch (ExecutionException ee) {
            System.out.println("PIGEON: " + ee.getMessage());
         }
         return false;
      }));
      return null;
   }
}
