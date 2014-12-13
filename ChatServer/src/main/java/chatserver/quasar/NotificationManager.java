package chatserver.quasar;

import chatserver.util.Msg;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;

public class NotificationManager extends BasicActor<Msg, Void> {
    private final int port;
    
    public NotificationManager(int port){
        this.port = port;
    }
    
    @Override
    protected Void doRun() throws InterruptedException, SuspendExecution {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }   
}
