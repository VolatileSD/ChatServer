package chatserver.quasar;

import chatserver.util.Msg;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;

public class HistoryWorker extends BasicActor<Msg, Void>{
   
   public HistoryWorker(Msg msg){
      
   }

   @Override
   protected Void doRun() throws InterruptedException, SuspendExecution {
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }
}
