package chatserver.db;

import chatserver.db.entity.Message;
import chatserver.db.entity.User;
import com.orientechnologies.orient.core.record.impl.ODocument;
import common.representations.MessageRepresentation;
import common.representations.TalkRepresentation;
import common.representations.UsersRepresentation;
import java.util.ArrayList;
import java.util.List;

public class UserODB {

   private final OrientDatabase db = new OrientDatabase("remote:localhost/ChatServer", "root", "root");

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
         sb.append(password).append("', registrationDate = sysdate(), loggedIn = false,");
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
      StringBuilder sb = new StringBuilder("UPDATE ");
      sb.append(rid).append(" SET loggedIn = ").append(loggedIn);
      try {
         db.execute(sb.toString());
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
      StringBuilder sb = new StringBuilder("UPDATE ");
      sb.append(rid).append(" SET active = ").append(active);
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
      StringBuilder sb = new StringBuilder("SELECT distinct(from) AS username FROM (SELECT expand(in('PrivateMessages')) FROM ");
      sb.append(rid).append(")");
      // try to order by last message sent
      UsersRepresentation users = new UsersRepresentation();

      try {
         List<ODocument> resultList = db.executeSynchQuery(sb.toString());
         ArrayList<String> usernames = new ArrayList();
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
      StringBuilder sb = new StringBuilder("SELECT FROM (SELECT expand(both('PrivateMessages')) FROM ");
      sb.append(rid).append(") WHERE (from = '").append(username);
      sb.append("' AND to = '").append(withUsername).append("') OR (from = '").append(withUsername);
      sb.append("' AND to = '").append(username).append("') ORDER BY date ASC");
      TalkRepresentation talk = new TalkRepresentation();

      try {
         List<ODocument> resuList = db.executeSynchQuery(sb.toString());
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

}
