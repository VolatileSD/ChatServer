package chatserver.util;

public class Util {
    /**
    * Filters commands for the simple client
    **/
    public CommandType getCommandType(String cmd){
        CommandType type = CommandType.UNKNOWN;

        if (cmd.equals(":cr")) type = CommandType.CHANGE_ROOM;
    	else if (cmd.equals(":h") || cmd.equals(":help")) type = CommandType.HELP;

        return type;
    }
}