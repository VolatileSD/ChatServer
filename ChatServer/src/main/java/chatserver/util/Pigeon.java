package chatserver.util;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.io.*;
import java.util.concurrent.ExecutionException;

public class Pigeon{
  final ActorRef to;

  /**
   * Pigeon constructor. 
  **/
  public Pigeon(ActorRef to){
    this.to = to;
  }

  public Msg carry(MsgType type) throws InterruptedException, SuspendExecution, ExecutionException{
    return carry(type, null);
  }

  public Msg carry(MsgType type, Object content) throws InterruptedException, SuspendExecution, ExecutionException{
    Msg res = null;
    Actor<Msg, Msg> pigeon = new BasicActor<Msg, Msg>() {
      @Override
      protected Msg doRun() throws InterruptedException, SuspendExecution {
        to.send(new Msg(type, self(), content));
        Msg reply = receive();
        return reply;
      }
    };
    pigeon.spawn();
    res = pigeon.get(); // study timeout

    return res;      
  }
}