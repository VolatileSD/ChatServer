package chatserver.db;

import java.util.ArrayList;
import java.util.List;

public class RoomDB {

   private final List<MessageDB> messages = new ArrayList();

   public void addMessage(String fromUsername, String message) {
      messages.add(new MessageDB(fromUsername, message));
   }

   // there will be shared state here
   // so we better clone the messages
   public List<MessageDB> getMessages() {
      List<MessageDB> list = new ArrayList();
      for (MessageDB m : messages) {
         list.add(m.clone());
      }

      return list;
   }
}
