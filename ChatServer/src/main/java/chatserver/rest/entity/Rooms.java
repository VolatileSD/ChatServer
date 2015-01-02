package chatserver.rest.entity;

import java.util.Collection;
import java.util.ArrayList;

public class Rooms {

   private final Collection<String> rooms;

   public Rooms() {
      rooms = new ArrayList();
   }

   public Rooms(Collection<String> roomsRestored) {
      this.rooms = new ArrayList();
      for (String s : roomsRestored) {
         this.rooms.add(s);
      }
   }

   /**
    * Test if a room exists
    *
    * @param roomName name of the room
    * @return if the room exists
    */
   public synchronized boolean has(String roomName) {
      return rooms.contains(roomName);
   }

   /**
    * Adds a room
    *
    * @param roomName name of the room
    */
   public synchronized void addRoom(String roomName) {
      rooms.add(roomName);
   }

   /**
    * Removes a room
    * @param roomName name of the room
    */ 
   public synchronized void removeRoom(String roomName) {
      rooms.remove(roomName);
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
