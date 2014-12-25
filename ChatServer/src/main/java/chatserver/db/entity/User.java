package chatserver.db.entity;

import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Date;

public class User extends AbstractEntity {

   private final String username;
   private final String password;
   private final Date registrationDate;
   private final boolean loggedIn;
   private final boolean active;

   public User(ODocument user) {
      super(user.getIdentity().toString());
      this.username = user.field("username");
      this.password = user.field("password");
      this.registrationDate = user.field("registrationDate");
      this.loggedIn = user.field("loggedIn");
      this.active = user.field("active");
   }

   public String getUsername() {
      return username;
   }

   public String getPassword() {
      return password;
   }

   public Date getRegistrationDate() {
      return registrationDate;
   }

   public boolean isLoggedIn() {
      return loggedIn;
   }
   
   public boolean isActive(){
      return active;
   }
}
