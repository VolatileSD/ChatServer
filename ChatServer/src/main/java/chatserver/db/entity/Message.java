package chatserver.db.entity;

import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Date;

public class Message extends AbstractEntity {

   private final String from;
   private final String text;
   private final Date date;

   public Message(ODocument message) {
      super(message.getIdentity().toString());
      this.from = message.field("from");
      this.text = message.field("text");
      this.date = message.field("date");
   }

   public String getFrom() {
      return from;
   }

   public String getText() {
      return text;
   }

   public Date getDate() {
      return date;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(date.toString());
      sb.append("\nFrom: ").append(from);
      sb.append("\nMessage: ").append(text).append("\n");

      return sb.toString();
   }
}
