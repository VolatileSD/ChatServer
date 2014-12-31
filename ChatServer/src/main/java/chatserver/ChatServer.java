package chatserver;

import co.paralleluniverse.actors.ActorRef;

import chatserver.rest.ChatServerApplication;
import chatserver.quasar.Acceptor;
import chatserver.quasar.Manager;
import chatserver.quasar.NotificationManager;
import chatserver.quasar.Room;
import chatserver.quasar.RoomManager;
import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ChatServer {

    public static void main(String[] args) throws Exception {
        int chatPort = 1111; //Integer.parseInt(args[0]);
        int notificationPort = 2222;
        ActorRef notificationManager = new NotificationManager(notificationPort).spawnThread(); // starts notifications
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
        Collection<String> roomsName = roomsRestored.keySet();
        new ChatServerApplication(roomManager, roomsName).run(args); // starts rest
        
        Acceptor acceptor = new Acceptor(chatPort, roomsRestored.get("Main"), roomManager);
        acceptor.spawn();
        acceptor.join();
    }
}
