package chatserver.util;

import co.paralleluniverse.actors.ActorRef;

public class Msg {

   private final MsgType type;
   private final ActorRef from;
   private final Object content;  // careful with mutable objects, such as the byte array
   private final String fromUsername;

   /**
    *
    * @param type Type of the message
    * @param from From whom the message is
    * @param content Content of the message
    */
   public Msg(MsgType type, ActorRef from, Object content) {
      this.type = type;
      this.from = from;
      this.content = content;
      this.fromUsername = null; // is new String() better?
   }

   public Msg(MsgType type, ActorRef from, Object content, String fromUsername) {
      this.type = type;
      this.from = from;
      this.content = content;
      this.fromUsername = fromUsername;
   }

   public MsgType getType() {
      return type;
   }

   public ActorRef getFrom() {
      return from;
   }

   public Object getContent() {
      return content;
   }

   public String getFromUsername() {
      return fromUsername;
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("Type: ");
      sb.append(type);
      if (from != null) {
         sb.append("\nFrom: ");
         sb.append(from);
      }
      if (content != null) {
         sb.append("\nContent: ");
         sb.append(content.toString());
      }
      if (fromUsername != null) {
         sb.append("\nFromUsername: ");
         sb.append(fromUsername);
      }
      return sb.toString();
   }
}
