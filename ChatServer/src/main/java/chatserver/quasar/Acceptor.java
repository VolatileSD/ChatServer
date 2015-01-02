package chatserver.quasar;

import co.paralleluniverse.actors.ActorRef;
import co.paralleluniverse.actors.BasicActor;
import java.util.logging.Logger;

public abstract class Acceptor extends BasicActor {

   protected final int port;
   protected final ActorRef mainRoom;
   protected final ActorRef roomManager;
   protected final ActorRef manager;
   protected static final Logger logger = Logger.getLogger(Acceptor.class.getName());

   public Acceptor(int port, ActorRef mainRoom, ActorRef roomManager, ActorRef manager) {
      this.port = port;
      this.mainRoom = mainRoom;
      this.roomManager = roomManager;
      this.manager = manager;
   }
}
