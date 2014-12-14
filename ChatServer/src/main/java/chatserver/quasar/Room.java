package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;

public class Room extends BasicActor<Msg, Void> {

   private final Map<ActorRef, String> users = new HashMap();
   private final String topic; // name?
   private boolean cannotDelete = false;

   public Room(String topic) {
      this.topic = topic;
   }

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      byte[] welcomeMessage = ("------ Welcome to Room " + topic + "! ------\n").getBytes();
      while (receive(msg -> {
         switch (msg.getType()) {
            case ENTER:
               // in case of the main room everyone will have null as name s
               String username = (String) msg.getContent();
               users.put(msg.getFrom(), username); // we should do this after the message is send for all users
               msg.getFrom().send(new Msg(MsgType.LINE, null, welcomeMessage));
               byte[] forAllUserEnter = ("#User " + username + " just got in!\n").getBytes();
               for (ActorRef u : users.keySet()) {
                  u.send(new Msg(MsgType.LINE, null, forAllUserEnter));
               }
               return true;
            case LEAVE:
               users.remove(msg.getFrom());
               byte[] forAllUserLeave = ("#User " + msg.getContent() + " just left!\n").getBytes();
               for (ActorRef u : users.keySet()) {
                  u.send(new Msg(MsgType.LINE, null, forAllUserLeave));
               }
               return true;
            case LINE:
               for (ActorRef u : users.keySet()) {
                  u.send(msg); // danger!?!?
               }          // concurrent exception can be thrown here right?
               // clone might solve it
               return true;
            case ROOM_INFO:
               msg.getFrom().send(new Msg(MsgType.OK, null, users.values()));
               return true;
            case DELETE_ROOM:
               return true;
            case CHANGE_ROOM:
               msg.getFrom().send(new Msg(MsgType.OK, self(), null));
               if (users.isEmpty()) {
                  setCannotDelete(true); // IMPORTANT
               }
               return true;
         }
         return false;
      }));
      return null;
   }

   private void setCannotDelete(boolean cannotDelete) {
      this.cannotDelete = cannotDelete;
   }
}
