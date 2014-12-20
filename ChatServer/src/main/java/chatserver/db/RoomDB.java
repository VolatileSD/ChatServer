package chatserver.db;

import java.util.ArrayList;
import java.util.List;

public class RoomDB {
   private final List<MessageDB> messages = new ArrayList();
   
   public void addMessage(String fromUsername, String message){
      messages.add(new MessageDB(fromUsername, message));
   }
}
