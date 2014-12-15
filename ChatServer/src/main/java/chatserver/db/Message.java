package chatserver.db;

import java.util.Date;

public class Message {

   private final String fromUsername;
   private final String message;
   private boolean seen;
   private final Date date;

   public Message(String fromUsername, String message) {
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
      return "Message{" + "fromUsername=" + fromUsername + ", message=" + message + ", seen=" + seen + ", date=" + date + '}';
   }
}
