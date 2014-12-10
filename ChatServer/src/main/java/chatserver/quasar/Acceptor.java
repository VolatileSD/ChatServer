package chatserver.quasar;


import java.io.IOException;
import java.net.InetSocketAddress;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.util.Msg;
import chatserver.util.MsgType;


public class Acceptor extends BasicActor {
  final int port;

  public Acceptor(int port) { 
    this.port = port; 
  }

  protected Void doRun() throws InterruptedException, SuspendExecution {
    ActorRef mainRoom = new Room("Main").spawn();
    // the mainRoom shall be returned when someone asks for the list of rooms?
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