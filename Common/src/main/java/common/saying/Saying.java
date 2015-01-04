package common.saying;

public class Saying {

   public static String getAllCommands(){
      StringBuilder sb = new StringBuilder(":create username password\n");
      sb.append(":remove username password\n");
      sb.append(":login username password\n");
      sb.append(":logout\n");
      sb.append(":changeroom|:cr roomName\n");
      sb.append(":private username message\n");
      sb.append(":inbox\n");
      
      return sb.toString();
   }
   
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
   
   public static String getPrivateOk(String username){
      return new StringBuilder("Message successfully sent to @").append(username).append(".\n").toString();
   }
   
   public static String getPrivateInvalid(String username){
      return new StringBuilder("Unknown user @").append(username).append(".\n").toString();
   }
}
