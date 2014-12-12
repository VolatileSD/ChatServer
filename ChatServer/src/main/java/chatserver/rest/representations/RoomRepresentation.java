package chatserver.rest.representations;

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

public class RoomRepresentation {
  public String name; 
  public Collection<String> users = new ArrayList<String>();

  @JsonCreator
  public RoomRepresentation(){
  }
 
  public void setName(String name){
  	this.name = name;
  }

  public void setUsers(Collection<String> users) {
    this.users = users;
  }
}