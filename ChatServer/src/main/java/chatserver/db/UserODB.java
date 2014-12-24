package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.User;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "root", "root");

   public void create(String username, String password) {
      StringBuilder sb = new StringBuilder().append("INSERT INTO User SET username = '");
      sb.append(username).append("', password = '");
      sb.append(password).append("', registrationDate = sysdate(), loggedIn = false");

      try {
         db.execute(sb.toString());
      } finally {
         db.close();
      }
   }

   public User findByUsername(String username) {
      User user = null;
      StringBuilder sb = new StringBuilder().append("SELECT FROM User WHERE username = '");
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

   public User login(String username, String password) {
      User user = findByUsernameAndPassword(username, password);
      if (user.isLoggedIn()) {
         user = null; // maybe here we could say that the user is already logged in
      } else {
         setLoggedIn(user.getRid(), true);
      }
      return user;
   }

   public void logout(String rid) {
      setLoggedIn(rid, false);
   }

   public void setLoggedIn(String rid, boolean loggedIn) {
      StringBuilder sb = new StringBuilder().append("UPDATE ");
      sb.append(rid).append(" SET loggedIn = ").append(loggedIn);
      try {
         db.execute(sb.toString());
      } finally {
         db.close();
      }
   }

   public boolean remove(String username, String password) {
      /*
       User user = findByUsernameAndPassword(username, password);
       if (user != null) {
       try {
            
       } finally {
       db.close();
       }
       return true;
       } else {
       return false;
       }
       */
      return true;
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
