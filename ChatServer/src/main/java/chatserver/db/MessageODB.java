/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver.db;

import chatserver.db.entity.Message;
import com.orientechnologies.orient.core.record.impl.ODocument;

public class MessageODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "root", "root");

   public MessageODB() {
   }

   public Message create(String from, String text) {
      Message m = null;
      try {
         db.open();
         StringBuilder sb = new StringBuilder().append("INSERT INTO Message set from = '");
         sb.append(from).append("', text = '");
         sb.append(text).append("', date = sysdate() RETURN @this");

         ODocument document = (ODocument) db.execute(sb.toString());

         m = new Message(document);
      } finally {
         db.close();
      }

      return m;
   }
}
