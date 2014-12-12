package chatserver.quasar;

import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;
import java.util.Map;
import java.util.HashMap;

import chatserver.util.Msg;
import chatserver.util.MsgType;


public class Room extends BasicActor<Msg, Void> {
  private Map<ActorRef, String> users = new HashMap();
  private String topic; // name?
  
  public Room(String topic){ 
    this.topic = topic; 
  }

  protected Void doRun() throws InterruptedException, SuspendExecution {

    while (receive(msg -> {
      switch (msg.getType()) {
        case ENTER:
        byte[] welcomeMessage = ("------ Welcome to Room " + topic + "! ------\n").getBytes();
          // in case of the main room everyone will have null as name s
          String username = (String) msg.getContent();
          users.put(msg.getFrom(), username);
          msg.getFrom().send(new Msg(MsgType.LINE, null, welcomeMessage));
          byte[] forAllUserEnter = ("#User " + username + " just got in!\n").getBytes(); 
          for (ActorRef u : users.keySet()) 
            u.send(new Msg(MsgType.LINE, null, forAllUserEnter));
          return true;
        case LEAVE:
          users.remove(msg.getFrom());
          byte[] forAllUserLeave  = ("#User " + msg.getContent() + " just left!\n").getBytes();
          for (ActorRef u : users.keySet()) 
            u.send(new Msg(MsgType.LINE, null, forAllUserLeave));
          return true;
        case LINE:
          for (ActorRef u : users.keySet()) u.send(msg); // danger!?!?
          // concurrent exception can be thrown here right?
          // clone might solve it
          return true;
        case ROOM_INFO:
          msg.getFrom().send(new Msg(MsgType.OK, null, users.values()));
          return true;
      }
      return false;
    }));
    return null;
  }
}