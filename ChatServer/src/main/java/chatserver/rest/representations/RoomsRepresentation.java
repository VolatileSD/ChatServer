package chatserver.rest.representations;

import com.fasterxml.jackson.annotation.*;
import java.util.ArrayList;
import java.util.Collection;

public class RoomsRepresentation {
  public Collection<String> rooms = new ArrayList();

  @JsonCreator
  public RoomsRepresentation(){
  }
 
  public void setRooms(Collection<String> rooms) {
    this.rooms = rooms;
  }
}