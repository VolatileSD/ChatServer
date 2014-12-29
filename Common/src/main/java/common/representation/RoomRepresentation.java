package common.representation;

import java.util.Collection;

public class RoomRepresentation {

   public String name;
   public Collection<String> users;

   public RoomRepresentation(String name, Collection<String> users) {
      this.name = name;
      this.users = users;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public Collection<String> getUsers(){
      return users;
   }
   
   public void setUsers(Collection<String> users) {
      this.users = users;
   }
}
