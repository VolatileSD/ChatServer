package chatserver.util;

import co.paralleluniverse.actors.ActorRef;


public class Msg {
  private final MsgType type;
  private final ActorRef from;
  private final Object content;  // careful with mutable objects, such as the byte array
  
  public Msg(MsgType type, ActorRef from, Object content) { 
    this.type = type; 
    this.from = from; 
    this.content = content; 
  }

  public MsgType getType(){
    return type;
  } 

  public ActorRef getFrom(){
    return from;
  }

  public Object getContent(){
    return content;
  }

  public String toString(){
    StringBuilder sb = new StringBuilder();
    sb.append("Type: ");
    sb.append(type);
    if(from != null){
      sb.append("\nFrom: ");
      sb.append(from); 
    }
    if(content != null){
      sb.append("\nContent: ");
      sb.append(content.toString());
    }
    return sb.toString();
  }
}
