package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;

public class LoginManager extends BasicActor<Msg, Void> {
  private Map<String, String> users = new HashMap();

  protected Void doRun() throws InterruptedException, SuspendExecution { //Exceptions
    while (receive(msg -> {
      switch (msg.getType()) {
        case LOGIN:
          String[] parts = (String []) msg.getContent();
          if(!users.containsKey(parts[1])){
            users.put(parts[1], parts[2]);
            msg.getFrom().send(new Msg(MsgType.OK, null, null));
          } else{
            if(users.get(parts[1]).equals(parts[2])) msg.getFrom().send(new Msg(MsgType.OK, null, null));
            else msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
          }
          return true;
      }
      return false;  // stops the actor if some unexpected message is received
    }));
    return null;
  }
}