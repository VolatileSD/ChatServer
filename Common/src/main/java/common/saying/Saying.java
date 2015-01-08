package common.saying;

public class Saying {

   public static String getAllCommands() {
      String allCommands = ":create username password\n"
              + ":remove username password\n"
              + ":login username password\n"
              + ":logout\n"
              + ":changeroom|:cr roomName\n"
              + ":private username message\n"
              + ":inbox\n";

      return allCommands;
   }

   public static String getCreateOk(String username) {
      return "New user @" + username + " created successfully.\n";
   }

   public static String getCreateInvalid() {
      return "Username already exists.\n";
   }

   public static String getRemoveOk(String username) {
      return "User " + username + " removed successfully.\n";
   }

   public static String getRemoveInvalid() {
      return "Invalid. Maybe the password is wrong.\n";
   }

   public static String getLoginOk(String username) {
      return "User " + username + ", you are logged in.\n";
   }

   public static String getLoginAdminOk(String username) {
      return "Admin " + username + ", you are logged in.\n";
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

   public static String getPrivateOk(String username) {
      return "Message successfully sent to @" + username + "\n";
   }

   public static String getPrivateInvalid(String username) {
      return "Unknown user @" + username + "\n";
   }
}
