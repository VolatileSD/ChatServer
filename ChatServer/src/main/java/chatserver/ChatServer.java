package chatserver;

import co.paralleluniverse.actors.ActorRef;

import chatserver.rest.ChatServerApplication;
import chatserver.quasar.Acceptor;
import chatserver.quasar.Manager;
import chatserver.quasar.NotificationManager;
import chatserver.quasar.RoomManager;
import chatserver.quasar.impl.AcceptorGUI;
import chatserver.quasar.impl.AcceptorNonGUI;
import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {

   private static final int CHAT_PORT = 1111;
   private static final int CHAT_PORT_GUI = 1112;
   private static final int NOTIFICATION_PORT = 2222;

   public static void main(String[] args) throws Exception {
      ActorRef notificationManager = new NotificationManager(NOTIFICATION_PORT).spawnThread(); // starts notifications
      ActorRef manager = new Manager().spawnThread();
      ActorRef roomManager = new RoomManager(manager, notificationManager).spawn();

      Msg msg;
      try {
         Msg reply = new Pigeon(manager).carry(MsgType.RESTORE);
         msg = new Pigeon(roomManager).carry(MsgType.RESTORE, null, reply.getContent());
      } catch (InterruptedException | SuspendExecution | ExecutionException e) {
         Logger.getLogger(ChatServerApplication.class.getName()).log(Level.SEVERE, "Error trying to restore - {0}", e.getMessage());
         return;
      }

      Map<String, ActorRef> roomsRestored = (Map) msg.getContent();
      Collection<String> roomsNames = roomsRestored.keySet();
      new ChatServerApplication(manager, roomManager, roomsNames).run(args); // starts rest

      ActorRef mainRoom = roomsRestored.get("Main");

      new AcceptorGUI(CHAT_PORT_GUI, mainRoom, roomManager, manager).spawn();
      Acceptor acceptor = new AcceptorNonGUI(CHAT_PORT, mainRoom, roomManager, manager);
      acceptor.spawn();
      acceptor.join();
   }
}
