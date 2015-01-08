package chatserver.quasar;

import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;

public class Room extends BasicActor<Msg, Void> {

   private final Map<String, ActorRef> users = new HashMap();
   private final String topic; // name?
   private final String rid;
   private final ActorRef manager;
   private final ActorRef notificationManager;
   private int usersWillEnterSoon = 0;

   public Room(String topic, String rid, ActorRef manager, ActorRef notificationManager) {
      this.topic = topic;
      this.rid = rid;
      this.manager = manager;
      this.notificationManager = notificationManager;
   }

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      byte[] welcomeMessage = ("------ Welcome to Room " + topic + "! ------\n").getBytes();
      while (receive(msg -> {
         String fromUsername;
         switch (msg.getType()) {
            case ENTER:
               usersWillEnterSoon--; // this is not intuite xD
               // in case of the main room everyone will have null as name
               fromUsername = (String) msg.getFromUsername();
               ActorRef newUser = msg.getFrom();
               newUser.send(new Msg(MsgType.LINE, null, null, welcomeMessage));

               byte[] forAllUserEnter = ("#User @" + fromUsername + " just got in!\n").getBytes();
               for (ActorRef u : users.values()) {
                  u.send(new Msg(MsgType.LINE, null, null, forAllUserEnter));
               }

               users.put(fromUsername, newUser);
               notificationManager.send(new Msg(MsgType.ENTER, null, fromUsername, topic));
               return true;
            case LEAVE:
               fromUsername = msg.getFromUsername();
               users.remove(fromUsername);

               byte[] forAllUserLeave = ("#User @" + fromUsername + " just left!\n").getBytes();
               for (ActorRef u : users.values()) {
                  u.send(new Msg(MsgType.LINE, null, null, forAllUserLeave));
               }
               notificationManager.send(new Msg(MsgType.LEAVE, null, fromUsername, topic));
               return true;
            case LINE:
               fromUsername = msg.getFromUsername();
               byte[] message = ("@" + fromUsername + ": " + msg.getContent()).getBytes();
               Msg line = new Msg(MsgType.LINE, null, null, message);
               for (ActorRef u : users.values()) {
                  u.send(line);
               }
               manager.send(new Msg(MsgType.LINE, null, fromUsername, new String[]{rid, (String) msg.getContent()}));
               return true;
            case ROOM_INFO:
               msg.getFrom().send(new Msg(MsgType.OK, null, null, users.keySet()));
               return true;
            case DELETE_ROOM:
               if (users.size() + usersWillEnterSoon > 0) {
                  msg.getFrom().send(new Msg(MsgType.INVALID));
                  return true;
               } else {
                  msg.getFrom().send(new Msg(MsgType.OK, null, null, new String[]{rid}));
                  return false;
               }
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
