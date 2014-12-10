package chatserver.util;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.Suspendable;
import co.paralleluniverse.fibers.io.*;
import java.util.concurrent.ExecutionException;

public class Pigeon{
  final ActorRef to;

  public Pigeon(ActorRef to){
    this.to = to;
  }

  public Msg carry(MsgType type) throws InterruptedException, SuspendExecution, ExecutionException{
    return carry(type, null);
  }

  //@Suspendable
  public Msg carry(MsgType type, Object content) throws InterruptedException, SuspendExecution, ExecutionException{
    Msg res = null;
    //try{
      Actor<Msg, Msg> pigeon = new BasicActor<Msg, Msg>() {
        @Override
        protected Msg doRun() throws InterruptedException, SuspendExecution {
          to.send(new Msg(type, self(), content));
          Msg reply = receive();
          return reply;
        }
      };
      pigeon.spawn();
      res = pigeon.get();
    //} catch (Exception e){ System.out.println("_EX_: " + e.getMessage()); }

    return res;      
  }
}