package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.User;
import com.orientechnologies.orient.core.record.impl.ODocument;
import common.representation.MessageRepresentation;
import common.representation.TalkRepresentation;
import common.representation.UsersRepresentation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "admin", "admin");

   public User findByRid(String rid) {
      User user = null;

      try {
         List<ODocument> resultList = db.executeSynchQuery("SELECT FROM " + rid);
         if (!resultList.isEmpty()) {
            user = new User(resultList.get(0));
         }
      } finally {
         db.close();
      }

      return user;
   }

   public User findByUsername(String username) {
      User user = null;
      String query = "SELECT FROM User WHERE username = '" + username + "'";

      try {
         List<ODocument> resultList = db.executeSynchQuery(query);
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

   public User create(String username, String password, boolean isAdmin) {
      User user = findByUsername(username);
      if (user == null) {
         String command = "INSERT INTO User SET username = '" + username
                 + "', password = '" + password
                 + "', registrationDate = sysdate(), loggedIn = false, isAdmin = "
                 + isAdmin + ", active = true RETURN @this";

         try {
            ODocument document = (ODocument) db.execute(command);
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
         if (!user.isActive() || user.isLoggedIn()) {
            user = null;
         } else {
            setActive(user.getRid(), false);
         }
      }

      return user;
   }

   public User login(String username, String password) {
      User user = findByUsernameAndPassword(username, password);
      if (user != null) {
         if (user.isLoggedIn() || !user.isActive()) {
            user = null; // maybe here we could say that the user is already logged in or inactive
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
      try {
         db.execute("UPDATE " + rid + " SET loggedIn = " + loggedIn);
      } finally {
         db.close();
      }
   }

   public void logoutEveryone() {
      try {
         db.execute("UPDATE User set loggedIn = false");
      } finally {
         db.close();
      }
   }

   public void setActive(String rid, boolean active) {
      StringBuilder sb = new StringBuilder("UPDATE ").append(rid).append(" SET active = ").append(active);
      if (!active) {
         sb.append(", loggedIn = false");
      }
      try {
         db.execute(sb.toString());
      } finally {
         db.close();
      }
   }

   public void addPrivateMessage(User userFrom, User userTo, Message message) {
      db.createEdge("PrivateMessages", userFrom.getRid(), message.getRid());
      db.createEdge("PrivateMessages", message.getRid(), userTo.getRid());
   }

   public List<Message> getInbox(String rid) {
      List<Message> inbox = new ArrayList();
      try {
         List<ODocument> resultList = db.executeSynchQuery("SELECT expand(in('PrivateMessages')) FROM " + rid);
         for (ODocument d : resultList) {
            inbox.add(new Message(d));
         }
      } finally {
         db.close();
      }

      return inbox;
   }

   public UsersRepresentation getInboxUsers(String rid) {
      String query1 = "SELECT distinct(from) AS username FROM "
              + "(SELECT expand(in('PrivateMessages')) FROM " + rid + ")";

      String query2 = "SELECT distinct(to) AS username FROM "
              + "(SELECT expand(out('PrivateMessages')) FROM " + rid + ")";
      // try to order by last message sent and in one database call
      UsersRepresentation users = new UsersRepresentation();

      try {
         List<ODocument> resultList = db.executeSynchQuery(query1);
         Set<String> usernames = new HashSet();
         for (ODocument d : resultList) {
            usernames.add(d.field("username"));
         }

         resultList = db.executeSynchQuery(query2);
         for (ODocument d : resultList) {
            usernames.add(d.field("username"));
         }

         users = new UsersRepresentation(usernames);
      } finally {
         db.close();
      }

      return users;
   }

   public TalkRepresentation getTalk(String rid, String username, String withUsername) {
      String query = "SELECT FROM (SELECT expand(both('PrivateMessages')) FROM "
              + rid + ") WHERE (from = '" + username + "' AND to = '" + withUsername
              + "') OR (from = '" + withUsername + "' AND to = '" + username 
              + "') ORDER BY date ASC";
      
      TalkRepresentation talk = new TalkRepresentation();

      try {
         List<ODocument> resuList = db.executeSynchQuery(query);
         ArrayList<MessageRepresentation> messages = new ArrayList();
         for (ODocument d : resuList) {
            messages.add(new MessageRepresentation(d.field("from"), d.field("text"), d.field("date")));
         }
         talk = new TalkRepresentation(messages);
      } finally {
         db.close();
      }

      return talk;
   }

   public boolean isAdmin(String username, String password) {
      User user = findByUsernameAndPassword(username, password);
      return user != null && user.isAdmin();
   }

   public User makeAdmin(String username) {
      User user = findByUsername(username);
      if (user != null) {
         setIsAdmin(user.getRid(), true);
      }

      return user;
   }

   public User removeAdmin(String username) {
      User user = findByUsername(username);
      if (user != null && user.isAdmin()) {
         setIsAdmin(user.getRid(), false);
      } else{
         user = null;
      }

      return user;
   }

   public void setIsAdmin(String rid, boolean isAdmin) {
      try {
         db.execute("UPDATE " + rid + " SET isAdmin = " + isAdmin);
      } finally {
         db.close();
      }
   }

}
