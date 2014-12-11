package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;

public class RoomManager extends BasicActor<Msg, Void> {
  private Map<String, ActorRef> rooms = new HashMap();

  protected Void doRun() throws InterruptedException, SuspendExecution { //Exceptions
    while (receive(msg -> {
      switch (msg.getType()) {
        case ROOM_INFO:
          return true;
        case CREATE_ROOM:
          String roomName = (String) msg.getContent();
          if(rooms.containsKey(roomName))
            msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
          else{
            rooms.put(roomName, new Room(roomName).spawn());
            msg.getFrom().send(new Msg(MsgType.OK, null, null));
          }
          return true;
        case DELETE_ROOM:
          return true;
        case CHANGE_ROOM:
          return true;
      }
      return false;  // stops the actor if some unexpected message is received
    }));
    return null;
  }
}