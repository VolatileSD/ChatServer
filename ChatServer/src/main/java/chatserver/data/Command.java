package chatserver.data;

import java.util.*;

public class Command {
	private final CommandType type;
    private final Object content;
    private final String command;
    // careful with mutable objects, such as the byte array
    public Command(String command) { this.command = command;}
    public Command(CommandType type, Object content) { this.type = type; this.content = content; }

    public CommandType getType(){
        /*
        * Filters commands for the simple client
        
        */
        String cmd= this.command;
        if (cmd ==":cr") {
            return CommandType.CHANGE_ROOM;
        }
    	else if (cmd==":h" || cmd==":help"){
    	    return CommandType.HELP;
    	}
    	else{
    	    return CommandType.UNKNOWN
    	}
    
    }

    public Object getContent(){
    	return content;
    }
}
