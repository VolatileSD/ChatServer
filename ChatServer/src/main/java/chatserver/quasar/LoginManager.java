package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;

public class LoginManager extends BasicActor<Msg, Void> {

   private final Map<String, String> users = new HashMap();

   @Override
   @SuppressWarnings("empty-statement")
   protected Void doRun() throws InterruptedException, SuspendExecution {
      while (receive(msg -> {
         String[] parts = (String[]) msg.getContent();
         switch (msg.getType()) {
            case CREATE:
               if (!users.containsKey(parts[1])) {
                  users.put(parts[1], parts[2]);
                  msg.getFrom().send(new Msg(MsgType.OK, null, null));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
               return true;
            case LOGIN:
               if (users.containsKey(parts[1]) && users.get(parts[1]).equals(parts[2])) {
                  msg.getFrom().send(new Msg(MsgType.OK, null, null));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
               return true;
            case REMOVE:
          // in this case, parts has only 2 elements
               // in this case, could be a only a String instead of String[]
               if (users.containsKey(parts[1])) {
                  users.remove(parts[1]);
                  msg.getFrom().send(new Msg(MsgType.OK, null, null));
               } else {
                  msg.getFrom().send(new Msg(MsgType.INVALID, null, null));
               }
               return true;
         }
         return false;
      }));
      return null;
   }
}
