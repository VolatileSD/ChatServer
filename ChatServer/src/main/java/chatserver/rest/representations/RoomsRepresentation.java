package chatserver.rest.representations;

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.List;

public class RoomsRepresentation {
  public List<String> rooms = new ArrayList<String>();

  @JsonCreator
  public RoomsRepresentation(){
  }
 
  public void setRooms(List<String> rooms) {
    this.rooms = rooms;
  }
}