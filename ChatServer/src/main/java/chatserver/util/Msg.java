package chatserver.util;

import co.paralleluniverse.actors.ActorRef;

public class Msg {

   private final MsgType type;
   private final ActorRef from;
   private final String fromUsername;
   private final Object content;  // careful with mutable objects, such as the byte array

   /**
    *
    * @param type Type of the message
    * @param from ActorRef from whom the message is
    * @param fromUsername Username from whom the message is
    * @param content Content of the message
    */
   public Msg(MsgType type, ActorRef from, String fromUsername, Object content) {
      this.type = type;
      this.from = from;
      this.content = content;
      this.fromUsername = fromUsername;
   }

   public Msg(MsgType type) {
      this.type = type;
      this.from = null;
      this.content = null;
      this.fromUsername = null;
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
      StringBuilder sb = new StringBuilder("Type: ");
      sb.append(type);
      if (from != null) {
         sb.append("\nFrom: ");
         sb.append(from);
      }
      if (fromUsername != null) {
         sb.append("\nFromUsername: ");
         sb.append(fromUsername);
      }
      if (content != null) {
         sb.append("\nContent: ");
         sb.append(content.toString());
      }

      return sb.toString();
   }

}
