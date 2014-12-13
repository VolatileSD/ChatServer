package chatserver.quasar;

import chatserver.util.Msg;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import org.zeromq.ZMQ;

public class NotificationManager extends BasicActor<Msg, Void> {

   private final int internalPort = 3333;
   private final ZMQ.Socket pub;

   /**
    * Constructor of NotificationManager
    *
    * @param port Port where Notification Client will connect
    */
   public NotificationManager(int port) {
      ZMQ.Context context = ZMQ.context(1);
      ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
      ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
      xpub.bind("tcp://*:" + port);
      xsub.bind("tcp://*:" + internalPort);
      ZMQ.proxy(xpub, xsub, null);

      this.pub = context.socket(ZMQ.PUB);
      this.pub.connect("tcp://localhost:" + xsub);
   }

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
         switch (msg.getType()) {
            case CREATE_ROOM:
               pub.send("rooms");
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
}
