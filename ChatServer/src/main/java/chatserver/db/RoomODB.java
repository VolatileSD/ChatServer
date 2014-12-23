/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.Room;
import com.orientechnologies.orient.core.record.impl.ODocument;

/**
 *
 * @author pc-user
 */
public class RoomODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "root", "root");

   public Room create(String name) {
      Room room = null;
      StringBuilder sb = new StringBuilder().append("INSERT INTO Room set name = '");
      sb.append(name).append("', creationDate = sysdate() RETURN @this");

      try {
         ODocument document = (ODocument) db.execute(sb.toString());
         room = new Room(document);
      } finally {
         db.close();
      }
      
      return room;
   }

   public void addMessage(String roomRid, Message m) {
      if (roomRid == null) { // because of main
         // main room is very problematic
         return;
      }
      try {
         db.createEdge("Messages", roomRid, m.getRid());
      } finally {
         db.close();
      }
   }

}
