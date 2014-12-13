package chatserver.quasar;

import chatserver.util.Msg;
import co.paralleluniverse.actors.BasicActor;
import co.paralleluniverse.fibers.SuspendExecution;

public class NotificationManager extends BasicActor<Msg, Void> {

    private final int port;

    public NotificationManager(int port) {
        this.port = port;
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
