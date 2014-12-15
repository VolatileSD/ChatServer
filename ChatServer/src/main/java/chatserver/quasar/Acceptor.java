package chatserver.quasar;

import java.io.IOException;
import java.net.InetSocketAddress;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.util.Msg;
import chatserver.util.MsgType;

public class Acceptor extends BasicActor {

   private final int port;
   private final ActorRef mainRoom;
   private final ActorRef roomManager;

   public Acceptor(int port, ActorRef mainRoom, ActorRef roomManager) {
      this.port = port;
      this.mainRoom = mainRoom;
      this.roomManager = roomManager;
   }

   @Override
   protected Void doRun() throws InterruptedException, SuspendExecution {
      ActorRef manager = new Manager().spawn();

      byte[] welcomeMessage = "------ Welcome to an awesome chat service! ------\n #Please login to chat. Type :h for help.\n".getBytes();

      try {
         FiberServerSocketChannel ss = FiberServerSocketChannel.open();
         ss.bind(new InetSocketAddress(port));
         while (true) {
            FiberSocketChannel socket = ss.accept();
            ActorRef user = new User(mainRoom, roomManager, manager, socket).spawn();
            user.send(new Msg(MsgType.LINE, null, null, welcomeMessage));
         }
      } catch (IOException e) {
         System.out.println(e.getMessage());
      }
      return null;
   }
}
