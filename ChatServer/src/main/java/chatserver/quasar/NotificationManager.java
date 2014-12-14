package chatserver.quasar;

import chatserver.util.Msg;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import org.zeromq.ZMQ;

public class NotificationManager extends BasicActor<Msg, Void> {

   private static final int internalPort = 3333;
   private static int port;
   private ZMQ.Socket pub;
   private static ZMQ.Context context;

   /**
    * Constructor of NotificationManager
    *
    * @param port Port where Notification Client will connect
    */
   public NotificationManager(int port) {
      NotificationManager.context = ZMQ.context(1);
      NotificationManager.port = port;
   }

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      new Proxy().start();
      this.pub = context.socket(ZMQ.PUB);
      this.pub.connect("tcp://localhost:" + internalPort); // sure?

      while (receive(msg -> {
         switch (msg.getType()) {
            case CREATE_ROOM:
               System.out.println("I will send a notification!");
               pub.sendMore("rooms");
               pub.send("Room " + msg.getContent() + " was created!");
               return true;
            case DELETE_ROOM:
               return true;
            case ENTER:
               return true;
            case LEAVE:
               return true;
            // these two would be easier to implement if instead of change_room we had connect and disconnect
         }
         return false;
      }));
      return null;
   }

   static class Proxy extends Thread {

      @Override
      public void run() {
         ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
         ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
         xpub.bind("tcp://*:" + port);
         xsub.bind("tcp://*:" + internalPort);
         ZMQ.proxy(xpub, xsub, null);
      }
   }
}
