package chatserver;

import co.paralleluniverse.actors.ActorRef;

import chatserver.rest.ChatServerApplication;
import chatserver.quasar.Acceptor;
import chatserver.quasar.RoomManager;


public class ChatServer {
  public static void main(String[] args) throws Exception {
    int port = 1111; //Integer.parseInt(args[0]);
    ActorRef roomManager = new RoomManager().spawn();
    new ChatServerApplication(roomManager).run(args); // starts rest
    Acceptor acceptor = new Acceptor(port);
    acceptor.spawn();
    acceptor.join();
  }
}