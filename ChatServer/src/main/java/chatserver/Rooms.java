public class Rooms{
    
    static class Acceptor extends BasicActor {
    final int port;
    final ActorRef room;
    Acceptor(int port, ActorRef room) { this.port = port; this.room = room; }

    protected Void doRun() throws InterruptedException, SuspendExecution {
      try {
        FiberServerSocketChannel ss = FiberServerSocketChannel.open();
        ss.bind(new InetSocketAddress(port));
        while (true) {
          FiberSocketChannel socket = ss.accept();
          ActorRef user = new User(room, socket).spawn();
          room.send(new Msg(MsgType.ENTER, user)); 
        }
      } catch (IOException e) { }
      return null;
    }
  }
    
  static class Room extends BasicActor<Msg, Void> {
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

    
    
}