package common.saying;

public class Saying {

   public static String getCreateOk(String username) {
      return new StringBuilder("New user @").append(username).append(" created successfully.\n").toString();
   }

   public static String getCreateInvalid() {
      return "Username already exists.\n";
   }

   public static String getRemoveOk() {
      return "User removed successfully.\n";
   }

   public static String getRemoveInvalid() {
      return "Invalid. Maybe the password is wrong.\n";
   }

   public static String getLoginOk(String username) {
      return username + ", you are logged in.\n";
   }

   public static String getLoginInvalid() {
      return "Login invalid. Username or password incorrect.\n";
   }
}
