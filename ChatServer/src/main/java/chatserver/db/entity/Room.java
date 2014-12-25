package chatserver.db.entity;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class Room extends AbstractEntity {

   private String name;
   private boolean active;
   
   public Room(ODocument room) {
      super(room.getIdentity().toString());
      this.name = room.field("name");
      this.active = room.field("active");
   }
   
   public String getName(){
      return name;
   }
   
   public boolean isActive(){
      return active;
   }
}
