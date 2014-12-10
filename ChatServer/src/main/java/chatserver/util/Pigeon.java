package chatserver.util;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

public class Pigeon{
  final ActorRef to;

  public Pigeon(ActorRef to){
    this.to = to;
  }

  public Msg carry(MsgType type){
    return carry(type, null);
  }

  public Msg carry(MsgType type, Object content){
    Msg res = null;
    try{
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
    } catch (Exception e){ System.out.println(e.getMessage()); }

    return res;      
  }
}