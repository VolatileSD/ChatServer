package chatserver.data;

import java.util.*;

public class Util {
    public Map<String,String> CommandMap = new HashMap;

 
    public CommandType getCommandType(String cmd){
        /*
        * Filters commands for the simple client
        */
       
        if (cmd ==":cr") {
            return CommandType.CHANGE_ROOM;
        }
    	else if (cmd==":h" || cmd==":help"){
    	    return CommandType.HELP;
    	}
    	else{
    	    return CommandType.UNKNOWN;
    	}
    
    }
    
 
}
