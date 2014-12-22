package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.User;
import com.orientechnologies.orient.core.id.ORecordId;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "root", "root");

   public UserODB() {
   }

   public void create(String username, String password) {
      try {
         db.open();
         StringBuilder sb = new StringBuilder().append("INSERT INTO User set username = '");
         sb.append(username).append("', password = '");
         sb.append(password).append("', registrationDate = sysdate()");
         /*
          ODocument document = new ODocument("User");
          document.field("username", username);
          document.field("password", password);
          document.field("registrationDate", new Date());

          document.save();
          */
         db.execute(sb.toString());
      } finally {
         db.close();
      }
   }

   // unused
   public User findByRid(String rid) {
      User user = null;

      try {
         String[] cap = rid.split(":");
         ORecordId orid = new ORecordId(Integer.parseInt(cap[0]), Long.parseLong(cap[1]));
         ODocument document = new ODocument(orid);
         user = new User(document);
      } finally {
         db.close();
      }

      return user;
   }

   public User findByUsername(String username) {
      User user = null;
      Map<String, Object> params = new HashMap();
      params.put("username", username);

      try {
         List<ODocument> resultList = db.executeSynchQuery("SELECT FROM User WHERE username = :username", params);
         if (!resultList.isEmpty()) {
            user = new User(resultList.get(0));
         }
      } finally {
         db.close();
      }

      return user;
   }

   public User findByUsernameAndPassword(String username, String password) {
      User user = findByUsername(username);
      return user != null && user.getPassword().equals(password) ? user : null;
   }

   public void addPrivateMessage(User user, Message message) {
      db.createEdge("PrivateMessages", user.getRid(), message.getRid());
   }

   public List<Message> getInbox(String rid) {
      List<Message> inbox = new ArrayList();
      try {
         List<ODocument> resultList = db.executeSynchQuery("SELECT expand(out('PrivateMessages')) FROM " + rid);
         for (ODocument d : resultList) {
            inbox.add(new Message(d));
         }
      } finally {
         db.close();
      }

      return inbox;
   }

}
