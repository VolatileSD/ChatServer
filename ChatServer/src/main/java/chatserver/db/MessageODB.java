package chatserver.db;

import chatserver.db.entity.Message;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class MessageODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "admin", "admin");

   public Message create(String from, String to, String text) {
      Message m = null;
      String command = "INSERT INTO Message set from = '" + from
              + "', to = '" + to + "', text = '" + text
              + "', date = sysdate() RETURN @this";
              
      try {
         ODocument document = (ODocument) db.execute(command);
         m = new Message(document);
      } finally {
         db.close();
      }

      return m;
   }

}
