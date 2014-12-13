package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;
import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;

public class RoomManager extends BasicActor<Msg, Void> {
  private Map<String, ActorRef> rooms = new HashMap();

  protected Void doRun() throws InterruptedException, SuspendExecution { //Exceptions

    while (receive(msg -> {
      try{
      switch (msg.getType()) {
        case ROOM_INFO:
          String roomName = (String) msg.getContent();
          if(rooms.containsKey(roomName)){ 
            // he already tested this in rest
            Msg reply = new Pigeon(rooms.get(roomName)).carry(MsgType.ROOM_INFO);
            msg.getFrom().send(reply);
          } else msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
          return true;
        case CREATE_ROOM:
          String roomName1 = (String) msg.getContent();
          if(rooms.containsKey(roomName1))
            msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
          else{
            rooms.put(roomName1, new Room(roomName1).spawn());
            msg.getFrom().send(new Msg(MsgType.OK, null, null));
          }
          return true;
        case DELETE_ROOM:
          return true;
        case CHANGE_ROOM:
          //Falta dizer as salas que o user mudou
          String[] roomAndUser = (String[]) msg.getContent();
          String roomName2 = roomAndUser[0];
          if(rooms.containsKey(roomName2)){ 
            Msg reply = new Pigeon(rooms.get(roomName2)).carry(MsgType.CHANGE_ROOM);
            msg.getFrom().send(reply);
          } else {
            msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
          }
          return true;
      }
      } catch (ExecutionException ee) { System.out.println("PIGEON: " + ee.getMessage()); }
      return false;  // stops the actor if some unexpected message is received
    }));
    return null;
  }
}