package common.representation;

import java.util.ArrayList;
import java.util.Collection;

public class UsersRepresentation {

   private Collection<String> users;
   
   public UsersRepresentation(){
      this.users = new ArrayList();
   }

   public UsersRepresentation(Collection<String> users) {
      this.users = users;
   }

   public Collection<String> getUsers(){
      return users;
   }
   
   public void setUsers(Collection<String> users) {
      this.users = users;
   }
}
