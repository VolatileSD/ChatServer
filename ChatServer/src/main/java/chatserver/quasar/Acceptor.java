package chatserver.quasar;


import java.io.IOException;
import java.net.InetSocketAddress;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.util.Msg;
import chatserver.util.MsgType;

import java.util.Map;
import java.util.HashMap;


public class Acceptor extends BasicActor {
  public Map<String, ActorRef> map;
  final int port;

  public Acceptor(int port) { 
    this.port = port; 
    this.map = new HashMap();
  }

  protected Void doRun() throws InterruptedException, SuspendExecution {
    ActorRef mainRoom = new Room("Main").spawn();
    ActorRef roomA = new Room("Expa").spawn();
    ActorRef roomB = new Room("Expb").spawn();
    map.put("Main", mainRoom);
    map.put("Expa", roomA);
    map.put("Expb", roomB);
    try {
      FiberServerSocketChannel ss = FiberServerSocketChannel.open();
      ss.bind(new InetSocketAddress(port));
      while (true) {
        FiberSocketChannel socket = ss.accept();
        ActorRef user = new User(mainRoom, socket,"userx").spawn();
      }
    } catch (IOException e) { System.out.println(e.getMessage()); }
    return null;
  }
}