package chatserver.db.entity;

import com.orientechnologies.orient.core.record.impl.ODocument;
import java.util.Date;

public class User extends AbstractEntity {

   private String username;
   private String password;
   private Date registrationDate;

   public User(ODocument user) {
      super(user.getIdentity().toString());
      this.username = user.field("username");
      this.password = user.field("password");
      this.registrationDate = user.field("registrationDate");
   }

   public String getPassword() {
      return password;
   }
}
