package chatserver.quasar;

import chatserver.db.entity.Message;
import chatserver.util.Msg;
import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import java.util.List;

public class HistoryWorker extends BasicActor<Msg, Void> {

   private ActorRef from;
   private List<Integer> roomLog;
   private List<Message> messages;

   public HistoryWorker(ActorRef from, List<Integer> roomLog, List<Message> messages) {
      this.from = from;
      this.roomLog = roomLog;
      this.messages = messages;
   }

   @Override
   protected Void doRun() throws InterruptedException, SuspendExecution {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
}
