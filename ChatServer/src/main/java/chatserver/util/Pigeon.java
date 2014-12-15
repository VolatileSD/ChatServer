package chatserver.util;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import static java.lang.Thread.sleep;
import java.util.concurrent.ExecutionException;

public class Pigeon {

   private final ActorRef to;

   /**
    *
    * @param to To whom the message goes
    */
   public Pigeon(ActorRef to) {
      this.to = to;
   }

   public Msg carry(MsgType type) throws InterruptedException, SuspendExecution, ExecutionException {
      System.out.println("CARRY1");
      return carry(type, null, null);
   }

   public Msg carry(MsgType type, Object content) throws InterruptedException, SuspendExecution, ExecutionException {
      return carry(type, content, null);
   }

   /**
    * The pigeon carries a message to its destination and comes back with the
    * reply
    *
    * @param type Type of the message
    * @param content Content of the message
    * @return Reply from the destination of the message
    * @throws java.lang.InterruptedException
    * @throws co.paralleluniverse.fibers.SuspendExecution
    * @throws java.util.concurrent.ExecutionException
    */
   public Msg carry(MsgType type, Object content, String from) throws InterruptedException, SuspendExecution, ExecutionException {
      System.out.println("CARRY3");
      Msg res;
      Actor<Msg, Msg> pigeon = new BasicActor<Msg, Msg>() {
         @Override
         protected Msg doRun() throws InterruptedException, SuspendExecution {
            System.out.println("SENDING");
            to.send(new Msg(type, self(), content, from));
            Msg reply = receive();
            return reply; // study timeout
         }
      };
      //pigeon.spawn();
      //res = pigeon.get();
      return pigeon.run();
   }
}
