package chatserver.rest.representations;

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.List;

public class RoomRepresentation {
  public List<String> users = new ArrayList<String>();

  @JsonCreator
  public RoomRepresentation(){
  }
 
  public void setUsers(List<String> users) {
    this.users = users;
  }
}