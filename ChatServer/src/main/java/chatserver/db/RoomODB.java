/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.Room;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author pc-user
 */
public class RoomODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "admin", "admin");

   public Room create(String name) {
      Room room = findByName(name);
      if (room == null) {
         StringBuilder sb = new StringBuilder("INSERT INTO Room set name = '");
         sb.append(name).append("', creationDate = sysdate(), active = true RETURN @this");
         try {
            ODocument document = (ODocument) db.execute(sb.toString());
            room = new Room(document);
         } finally {
            db.close();
         }
      } else if (!room.isActive()) {
         setActive(room.getRid(), true);
      }

      return room;

   }

   private void delete(String name) {
      Room room = findByName(name);
      setActive(room.getRid(), false);

   }

   public Room findByName(String name) {
      Room room = null;
      StringBuilder sb = new StringBuilder("SELECT FROM Room WHERE name = '");
      sb.append(name).append("'");

      try {
         List<ODocument> resultList = db.executeSynchQuery(sb.toString());
         if (!resultList.isEmpty()) {
            room = new Room(resultList.get(0));
         }
      } finally {
         db.close();
      }

      return room;
   }

   public void setActive(String rid, boolean active) {
      StringBuilder sb = new StringBuilder("UPDATE ");
      sb.append(rid).append(" SET active = ").append(active);
      try {
         db.execute(sb.toString());
      } finally {
         db.close();
      }
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

   public Map<String, String> getActiveRooms() {
      Map<String, String> rooms = new HashMap();
      try {
         List<ODocument> resultList = db.executeSynchQuery("SELECT FROM Room WHERE active = true");
         for (ODocument d : resultList) {
            Room r = new Room(d);
            rooms.put(r.getName(), r.getRid());
         }
      } finally {
         db.close();
      }

      return rooms;
   }

}
