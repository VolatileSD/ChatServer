package chatserver.db;

import co.paralleluniverse.actors.*;

public class User {

   private ActorRef ref;
   private final String name;
   private final String pass;
   private boolean loggedIn;

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

}
