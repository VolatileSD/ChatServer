package chatserver.quasar;

import chatserver.util.Msg;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;
import org.zeromq.ZMQ;

public class NotificationManager extends BasicActor<Msg, Void> {
    private final int internalPort = 3333;
    
    public NotificationManager(int port) {
        ZMQ.Context context = ZMQ.context(1);
        ZMQ.Socket xpub = context.socket(ZMQ.XPUB);
        ZMQ.Socket xsub = context.socket(ZMQ.XSUB);
        xpub.bind("tcp://*:" + port);
        xsub.bind("tcp://*:" + internalPort);
        ZMQ.proxy(xpub, xsub, null);
    }

    @Override
    @SuppressWarnings("empty-statement")
    protected Void doRun() throws InterruptedException, SuspendExecution {
        while (receive(msg -> {
            switch (msg.getType()) {
                case CREATE:
                    return true;
                case LOGIN:
                    return true;
                case REMOVE:
                    return true;
            }
            return false;
        }));
        return null;
    }
}
