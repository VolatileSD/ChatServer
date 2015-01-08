package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.Room;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "admin", "admin");

   public Room create(String name) {
      Room room = findByName(name);
      if (room == null) {
         String command = "INSERT INTO Room set name = '" + name
                 + "', creationDate = sysdate(), active = true RETURN @this";
         try {
            ODocument document = (ODocument) db.execute(command);
            room = new Room(document);
         } finally {
            db.close();
         }
      } else if (!room.isActive()) {
         setActive(room.getRid(), true);
      }

      return room;

   }

   public Room findByName(String name) {
      Room room = null;
      String query = "SELECT FROM Room WHERE name = '" + name + "'";
      try {
         List<ODocument> resultList = db.executeSynchQuery(query);
         if (!resultList.isEmpty()) {
            room = new Room(resultList.get(0));
         }
      } finally {
         db.close();
      }

      return room;
   }

   public void setActive(String rid, boolean active) {
      String command = "UPDATE " + rid + " SET active = " + active;
      try {
         db.execute(command);
      } finally {
         db.close();
      }
   }

   public void addMessage(String roomRid, Message m) {
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
