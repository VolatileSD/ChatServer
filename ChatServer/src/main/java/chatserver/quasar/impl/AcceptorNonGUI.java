package chatserver.quasar.impl;

import chatserver.quasar.Acceptor;
import chatserver.util.Msg;
import chatserver.util.MsgType;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.FiberServerSocketChannel;
import co.paralleluniverse.fibers.io.FiberSocketChannel;
import java.io.IOException;
import java.net.InetSocketAddress;

public class AcceptorNonGUI extends Acceptor {

   public AcceptorNonGUI(int port, ActorRef mainRoom, ActorRef roomManager, ActorRef manager) {
      super(port, mainRoom, roomManager, manager);
   }

   @Override
   protected Void doRun() throws InterruptedException, SuspendExecution {

      byte[] welcomeMessage = "------ Welcome to an awesome chat service! ------\n #Please login to chat. Type :h for help.\n".getBytes();
      try {
         FiberServerSocketChannel ss = FiberServerSocketChannel.open();
         ss.bind(new InetSocketAddress(port));
         while (true) {
            FiberSocketChannel socket = ss.accept();
            ActorRef user = new UserNonGUI(mainRoom, roomManager, manager, socket).spawn();
            user.send(new Msg(MsgType.LINE, null, null, welcomeMessage));
         }
      } catch (IOException e) {
         logger.severe(e.getMessage());
      }
      return null;
   }
}
