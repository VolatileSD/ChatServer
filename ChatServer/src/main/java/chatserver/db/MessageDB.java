package chatserver.db;

import java.util.Date;

public class MessageDB {

   private final String fromUsername;
   private final String message;
   private boolean seen;
   private final Date date;

   public MessageDB(String fromUsername, String message) {
      this.fromUsername = fromUsername;
      this.message = message;
      this.seen = false;
      this.date = new Date();
   }

   public String getFromUsername() {
      return fromUsername;
   }

   public String getMessage() {
      return message;
   }

   public void see() {
      this.seen = true;
   }

   @Override
   public String toString() {
StringBuilder sb = new StringBuilder();
      sb.append("From: ").append(fromUsername).append("\n");
      sb.append("When: ").append(date.toString()).append("\n");
      sb.append("Message: ").append(message).append("\n");

      return sb.toString();
   }
}
