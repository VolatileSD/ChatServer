package chatserver.rest.entities;

import java.util.Collection;
import java.util.ArrayList;

public class Rooms{
  private Collection<String> rooms = new ArrayList();

  public synchronized void addRoom(String roomName){
    rooms.add(roomName);
  }

  /*
	 * Tests if there is a room with this name;
  */
  public synchronized boolean has(String roomName){
  	return rooms.contains(roomName);
  }

  public synchronized Collection<String> getRooms() {
    // is this necessary?
    // or still dangerous?
    return new ArrayList<String>(rooms);
  }
}