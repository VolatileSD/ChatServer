package chatserver.db;

import co.paralleluniverse.actors.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class User {

   private ActorRef ref;
   private final String name;
   private final String pass;
   private boolean loggedIn;
   private final Set<Message> inbox = new HashSet();

   public User(String pass, String name) {
      this.name = name;
      this.pass = pass;
   }

   public void setActorRef(ActorRef newref) {
      this.ref = newref;
   }

   public ActorRef getActorRef() {
      return ref;
   }

   public String getPassword() {
      return pass;
   }

   public boolean isLoggedIn() {
      return loggedIn;
   }

   public void setLoggedIn(boolean loggedIn) {
      this.loggedIn = loggedIn;
   }

   public void addMessage(String fromUsername, String message) {
      inbox.add(new Message(fromUsername, message));
   }

   public String showInbox() {
      Iterator it = inbox.iterator();
      String res;
      res = "";
      while (it.hasNext()) {
         Message m = (Message) it.next();
         res = res + (m.getFromUsername() + " said '" + m.getMessage() + "'\n -------------------\n");
         it.remove(); // avoids a ConcurrentModificationException
      }
      return res;
   }
}
