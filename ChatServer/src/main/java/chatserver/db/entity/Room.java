package chatserver.db.entity;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class Room extends AbstractEntity {

   private boolean active;
   
   public Room(ODocument room) {
      super(room.getIdentity().toString());
      this.active = room.field("active");
   }
   
   public boolean isActive(){
      return active;
   }
}
