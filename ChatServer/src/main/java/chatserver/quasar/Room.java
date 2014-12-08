package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;
import java.util.Set;
import java.util.HashSet;

import chatserver.data.Msg;
import chatserver.data.MsgType;


public class Room extends BasicActor<Msg, Void> {
  private Set<ActorRef> users = new HashSet();
  private String topic;
  public Room(String topic){this.topic=topic;}

  protected Void doRun() throws InterruptedException, SuspendExecution {
    while (receive(msg -> {
      switch (msg.getType()) {
        case ENTER:
          byte[] WELCOME_MESSAGE= ("------ Welcome to Room "+topic+"! ------\n").getBytes();
          users.add(msg.getFrom());
          msg.getFrom().send(new Msg(MsgType.LINE,null,WELCOME_MESSAGE));
          for (ActorRef u : users) u.send(new Msg(MsgType.LINE,null,("#User "+msg.getContent()+" just got in!\n").getBytes()));
          return true;
        case LEAVE:
          users.remove(msg.getFrom());
          for (ActorRef u : users) u.send(new Msg(MsgType.LINE,null,("#User "+msg.getContent()+" just left!\n").getBytes()));
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