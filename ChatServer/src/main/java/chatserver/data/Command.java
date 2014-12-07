package chatserver.data;

public class Command {
	private final CommandType type;
    private final Object content;  // careful with mutable objects, such as the byte array
    public Command(CommandType type, Object content) { this.type = type; this.content = content; }

    public CommandType getType(String cmd){
        if (cmd ==":cr") {
            return CommandType.CHANGE_ROOM
        }
    	else if (){}
    	else if (){}
    	else{
    	    return CommandType.UNKNOWN
    	}
    }

    public Object getContent(){
    	return content;
    }
}
