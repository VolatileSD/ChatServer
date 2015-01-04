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
      return new StringBuilder("User ").append(username).append(", you are logged in.\n").toString();
   }

   public static String getLoginAdminOk(String username) {
      return new StringBuilder("Admin ").append(username).append(", you are logged in.\n").toString();
   }

   public static String getLoginInvalid() {
      return "Login invalid. Username or password incorrect.\n";
   }

   public static String getLogoutOk() {
      return "Successfully logged out.\n";
   }

   public static String getUnknownCommand() {
      return "Unknown Command.\n";
   }
}
