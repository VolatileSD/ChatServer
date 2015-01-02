package chatserver.quasar.impl;

import chatserver.quasar.Acceptor;
import chatserver.quasar.User;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.FiberServerSocketChannel;
import co.paralleluniverse.fibers.io.FiberSocketChannel;
import java.io.IOException;
import java.net.InetSocketAddress;

public class AcceptorGUI extends Acceptor {

   public AcceptorGUI(int port, ActorRef mainRoom, ActorRef roomManager, ActorRef manager) {
      super(port, mainRoom, roomManager, manager);
   }

   @Override
   protected Void doRun() throws InterruptedException, SuspendExecution {
      try {
         FiberServerSocketChannel ss = FiberServerSocketChannel.open();
         ss.bind(new InetSocketAddress(port));
         while (true) {
            FiberSocketChannel socket = ss.accept();
            new User(mainRoom, roomManager, manager, socket, true).spawn();
         }
      } catch (IOException e) {
         logger.severe(e.getMessage());
      }
      return null;
   }
}
