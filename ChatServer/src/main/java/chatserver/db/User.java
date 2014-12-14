package chatserver.db;

import co.paralleluniverse.actors.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class User {

   private ActorRef ref;
   private final String name;
   private final String pass;
   private boolean loggedIn;
   private final Map<String, String> inbox = new HashMap();

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

   public void addMessage(String u, String m) {
      inbox.put(u, m);
   }

   public String showInbox() {
      Iterator it = inbox.entrySet().iterator();
      String res;
      res = "";
      while (it.hasNext()) {
         Map.Entry pairs = (Map.Entry) it.next();
         res = res + (pairs.getKey() + " said '" + pairs.getValue() + "'\n -------------------\n");
         it.remove(); // avoids a ConcurrentModificationException
      }
      return res;
   }
}
