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
               pub.sendMore("rooms");
               pub.send("Room " + ((String[]) msg.getContent())[0] + " was created!");
               return true;
            case DELETE_ROOM:
               pub.sendMore("rooms");
               pub.send("Room " + ((String[]) msg.getContent())[0] + " was deleted!");
               return true;
            case ENTER:
               pub.sendMore("room/" + msg.getContent());
               pub.send("User @" + msg.getFromUsername() + " entered the room " + msg.getContent());
               return true;
            case LEAVE:
               pub.sendMore("room/" + msg.getContent());
               pub.send("User @" + msg.getFromUsername() + " left the room " + msg.getContent());
               return true;
         }
         return false;
      }));
      return null;
   }

   static class Proxy extends Thread {

      @Override
      public void run() {
         ZMQ.Context context = ZMQ.context(1);
         ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
         ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
         xpub.bind("tcp://*:" + port);
         xsub.bind("tcp://*:" + internalPort);
         ZMQ.proxy(xpub, xsub, null);
      }
   }
}
