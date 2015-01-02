package chatserver.quasar.impl;

import chatserver.quasar.User;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.io.FiberSocketChannel;

public class UserGUI extends User {

   public UserGUI(ActorRef mainRoom, ActorRef roomManager, ActorRef manager, FiberSocketChannel socket) {
      super(mainRoom, roomManager, manager, socket);
   }

}
