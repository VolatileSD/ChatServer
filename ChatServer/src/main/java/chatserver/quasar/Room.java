package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;
import java.util.Set;
import java.util.HashSet;

import chatserver.data.Msg;
import chatserver.data.MsgType;


public class Room extends BasicActor<Msg, Void> {
  private Set<ActorRef> users = new HashSet();

  protected Void doRun() throws InterruptedException, SuspendExecution {
    while (receive(msg -> {
      switch (msg.getType()) {
        case ENTER:
          users.add((ActorRef) msg.getO());
          return true;
        case LEAVE:
          users.remove((ActorRef) msg.getO());
          return true;
        case LINE:
          for (ActorRef u : users) u.send(msg); // danger!?!?
          // concurrent exception can be thrown here right?
          // clone might solve it
          return true;
      }
      return false;
    }));
    return null;
  }
}