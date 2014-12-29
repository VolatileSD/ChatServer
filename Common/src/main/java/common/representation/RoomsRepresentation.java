package common.representation;

import java.util.Collection;

public class RoomsRepresentation {

   private Collection<String> rooms;

   public RoomsRepresentation(Collection<String> rooms) {
      this.rooms = rooms;
   }

   public Collection<String> getRooms(){
      return rooms;
   }
   
   public void setRooms(Collection<String> rooms) {
      this.rooms = rooms;
   }
}