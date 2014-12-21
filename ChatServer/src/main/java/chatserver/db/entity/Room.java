package chatserver.db.entity;

import com.orientechnologies.orient.core.record.impl.ODocument;

public class Room extends AbstractEntity {

   public Room(ODocument room) {
      super(room.getIdentity().toString());
   }
}
