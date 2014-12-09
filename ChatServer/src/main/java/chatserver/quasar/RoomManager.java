package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.util.Msg;
import chatserver.util.MsgType;

public class RoomManager extends BasicActor<Msg, Void> {

  protected Void doRun() throws InterruptedException, SuspendExecution { //Exceptions
    while (receive(msg -> {
      switch (msg.getType()) {
        case DATA:
          return true;
        case EOF:
        case IOE:
          return false;
        case LINE:
          return true;
      }
      return false;  // stops the actor if some unexpected message is received
    }));
    return null;
  }
}