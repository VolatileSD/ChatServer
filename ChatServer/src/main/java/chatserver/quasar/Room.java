package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;

public class Room extends BasicActor<Msg, Void> {

   private final Map<String, ActorRef> users = new HashMap();
   private final String topic; // name?
   private final ActorRef manager;
   private final ActorRef notificationManager;
   private int usersWillEnterSoon = 0;
   private int currentMessageNumber = 0;

   public Room(String topic, ActorRef manager, ActorRef notificationManager) {
      this.topic = topic;
      this.manager = manager;
      this.notificationManager = notificationManager;
   }

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      byte[] welcomeMessage = ("------ Welcome to Room " + topic + "! ------\n").getBytes();
      while (receive(msg -> {
         switch (msg.getType()) {
            case ENTER:
               usersWillEnterSoon--; // this is not intuite xD
               // in case of the main room everyone will have null as name
               String username = (String) msg.getFromUsername();
               ActorRef newUser = msg.getFrom();
               newUser.send(new Msg(MsgType.LINE, null, null, welcomeMessage));

               byte[] forAllUserEnter = ("#User @" + username + " just got in!\n").getBytes();
               for (ActorRef u : users.values()) {
                  u.send(new Msg(MsgType.LINE, null, null, forAllUserEnter));
               }

               users.put(username, newUser);

               notificationManager.send(new Msg(MsgType.ENTER, null, username, topic));
               return true;
            case LEAVE:
               users.remove(msg.getFrom());
               byte[] forAllUserLeave = ("#User @" + msg.getFromUsername() + " just left!\n").getBytes();
               for (ActorRef u : users.values()) {
                  u.send(new Msg(MsgType.LINE, null, null, forAllUserLeave));
               }

               notificationManager.send(new Msg(MsgType.LEAVE, null, msg.getFromUsername(), topic));
               manager.send(new Msg(MsgType.LEAVE));
               return true;
            case LINE:
               for (ActorRef u : users.values()) {
                  u.send(msg);
               }
               currentMessageNumber++;
               return true;
            case ROOM_INFO:
               msg.getFrom().send(new Msg(MsgType.OK, null, null, users.values()));
               return true;
            case DELETE_ROOM:
               if (users.size() > 0 || usersWillEnterSoon == 0) {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
               } else {
                  msg.getFrom().send(new Msg(MsgType.OK));
               }
               return true;
            case CHANGE_ROOM:
               msg.getFrom().send(new Msg(MsgType.OK, self(), null, null));
               usersWillEnterSoon++;
               return true;
         }
         return false;
      }));
      return null;
   }
}
