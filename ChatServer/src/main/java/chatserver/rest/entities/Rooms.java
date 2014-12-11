package chatserver.rest.entities;

import java.util.List;
import java.util.ArrayList;

public class Rooms{
  private List<String> rooms = new ArrayList();

  public synchronized void addRoom(String roomName){
    rooms.add(roomName);
  }

  public synchronized List<String> getRooms() {
    // is this necessary?
    // or still dangerous?
    return new ArrayList<String>(rooms);
  }
}