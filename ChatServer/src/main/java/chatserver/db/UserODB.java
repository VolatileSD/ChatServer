package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.User;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.ArrayList;
import java.util.List;

public class UserODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "root", "root");

   public User findByUsername(String username) {
      User user = null;
      StringBuilder sb = new StringBuilder("SELECT FROM User WHERE username = '");
      sb.append(username).append("'");

      try {
         List<ODocument> resultList = db.executeSynchQuery(sb.toString());
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

   public User create(String username, String password) {
      User user = findByUsername(username);
      if (user == null) {
         StringBuilder sb = new StringBuilder("INSERT INTO User SET username = '");
         sb.append(username).append("', password = '");
         sb.append(password).append("', registrationDate = sysdate(), loggedIn = false");
         sb.append(" active = true RETURN @this");

         try {
            ODocument document = (ODocument) db.execute(sb.toString());
            user = new User(document);
         } finally {
            db.close();
         }
      } else {
         user = null;
      }

      return user;
   }

   public User remove(String username, String password) {
      // the fact that we dont actually remove the user it allows us to implement activate account in the future
      User user = findByUsernameAndPassword(username, password);
      if (user != null) {
         if(!user.isActive()){
            user = null; // maybe here we could say the user does not exists
         } else{
            setActive(user.getRid(), false);
         }
      }

      return user;
   }

   public User login(String username, String password) {
      User user = findByUsernameAndPassword(username, password);
      if (user != null) {
         if (user.isLoggedIn()) {
            user = null; // maybe here we could say that the user is already logged in
         } else {
            setLoggedIn(user.getRid(), true);
         }
      }
      return user;
   }

   public void logout(String rid) {
      setLoggedIn(rid, false);
   }

   public void setLoggedIn(String rid, boolean loggedIn) {
      StringBuilder sb = new StringBuilder("UPDATE ");
      sb.append(rid).append(" SET loggedIn = ").append(loggedIn);
      try {
         db.execute(sb.toString());
      } finally {
         db.close();
      }
   }

   public void setActive(String rid, boolean active) {
      StringBuilder sb = new StringBuilder("UPDATE ");
      sb.append(rid).append(" SET active = ").append(active);
      try {
         db.execute(sb.toString());
      } finally {
         db.close();
      }
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

   public void logoutEveryone() {
      try {
         db.execute("UPDATE User set loggedIn = false");
      } finally {
         db.close();
      }
   }

}
