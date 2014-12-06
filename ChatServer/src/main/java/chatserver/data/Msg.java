package chatserver.data;

public class Msg {
	private final MsgType type;
    private final Object o;  // careful with mutable objects, such as the byte array
    public Msg(MsgType type, Object o) { this.type = type; this.o = o; }

    public MsgType getType(){
    	return type;
    }

    public Object getO(){
    	return o;
    }
}