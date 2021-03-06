package chatserver.util;

public class Util {

   /**
    * Filters commands for the simple client
    *
    * @param cmd command to be parsed
    * @return the type of the command
    */
   public static CommandType getCommandType(String cmd) {
      CommandType type = CommandType.UNKNOWN;

      switch (cmd) {
         case ":create":
            type = CommandType.CREATE;
            break;
         case ":remove":
            type = CommandType.REMOVE;
            break;
         case ":login":
            type = CommandType.LOGIN;
            break;
         case ":logout":
            type = CommandType.LOGOUT;
            break;
         case ":cr":
         case ":changeroom":
            type = CommandType.CHANGE_ROOM;
            break;
         case ":private":
            type = CommandType.PRIVATE;
            break;
         case ":inbox":
            type = CommandType.INBOX;
            break;
         case ":iu":
            type = CommandType.INBOX_USERS;
            break;
         case ":talk":
            type = CommandType.TALK;
            break;
         case ":h":
         case ":help":
            type = CommandType.HELP;
            break;
      }

      return type;
   }
}
