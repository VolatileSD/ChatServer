package common.representation;

import java.util.ArrayList;
import java.util.Collection;

public class TalkRepresentation {

   private Collection<MessageRepresentation> messages;

   public TalkRepresentation() {
      this.messages = new ArrayList();
   }

   public TalkRepresentation(Collection<MessageRepresentation> messages) {
      this.messages = messages;
   }

   public Collection<MessageRepresentation> getMessages() {
      return messages;
   }

   public void setMessages(Collection<MessageRepresentation> messages) {
      this.messages = messages;
   }

}
