package chatserver.rest.entities;

import java.util.Collection;
import java.util.ArrayList;

public class Rooms{
  private final Collection<String> rooms = new ArrayList();

  /**
   * Adds a room
   * @param roomName name of the room
   */
  public synchronized void addRoom(String roomName){
    rooms.add(roomName);
  }

  /**
     * Test if a room exists
     * @param roomName name of the room
     * @return if the room exists
   */
  public synchronized boolean has(String roomName){
  	return rooms.contains(roomName);
  }

  /**
   * 
   * @return list of all rooms
   */
  public synchronized Collection<String> getRooms() {
    // is this necessary?
    // or still dangerous?
    return new ArrayList(rooms);
  }
}