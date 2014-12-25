package chatserver.db;

import chatserver.db.entity.Message;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class MessageODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "root", "root");

   public Message create(String from, String text) {
      Message m = null;
      StringBuilder sb = new StringBuilder("INSERT INTO Message set from = '");
      sb.append(from).append("', text = '");
      sb.append(text).append("', date = sysdate() RETURN @this");

      try {
         ODocument document = (ODocument) db.execute(sb.toString());
         m = new Message(document);
      } finally {
         db.close();
      }

      return m;
   }

}
