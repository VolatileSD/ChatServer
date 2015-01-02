package chatserver.util;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
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

   /**
    * The pigeon carries a message to its destination and comes back with the
    * reply
    *
    * @param type Type of the message
    * @param fromUsername Username from whom the message is
    * @param content Content of the message
    * @return Reply from the destination of the message
    * @throws java.lang.InterruptedException
    * @throws co.paralleluniverse.fibers.SuspendExecution
    * @throws java.util.concurrent.ExecutionException
    */
   public Msg carry(MsgType type, String fromUsername, Object content) throws InterruptedException, SuspendExecution, ExecutionException {
      Actor<Msg, Msg> pigeon = new BasicActor<Msg, Msg>() {
         @Override
         protected Msg doRun() throws InterruptedException, SuspendExecution {
            to.send(new Msg(type, self(), fromUsername, content));
            Msg reply = receive();
            return reply; // study timeout
         }
      };
      return pigeon.run();
   }

   public Msg carry(MsgType type) throws InterruptedException, SuspendExecution, ExecutionException {
      return carry(type, null, null);
   }
}
