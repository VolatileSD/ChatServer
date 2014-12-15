package chatserver.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import co.paralleluniverse.actors.ActorRef;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collection;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;
import chatserver.rest.representations.RoomRepresentation;
import chatserver.rest.entities.Rooms;

@Path("/room/{name}")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

   private final ActorRef roomManager;
   private final Rooms rooms;

   public RoomResource(Rooms rooms, ActorRef roomManager) {
      this.rooms = rooms;
      this.roomManager = roomManager;
   }

   @GET
   public Response getRoomInfo(@PathParam("name") String roomName) throws Exception {
      if (rooms.has(roomName)) {
         Msg msg = new Pigeon(roomManager).carry(MsgType.ROOM_INFO, null, roomName);
         switch (msg.getType()) {
            case OK:
               RoomRepresentation rr = new RoomRepresentation();
               rr.setName(roomName);
               rr.setUsers((Collection<String>) msg.getContent());
               return Response.ok(new ObjectMapper().writeValueAsString(rr)).build();
            default:
               return Response.status(500).build();
         }
      } else {
         return Response.status(409).build();
      }
   }

   @PUT
   public Response createRoom(@PathParam("name") String roomName) throws Exception {
      Msg msg = new Pigeon(roomManager).carry(MsgType.CREATE_ROOM, null, roomName);
      switch (msg.getType()) {
         case OK:
            rooms.addRoom(roomName);
            return Response.status(201).build();
         case INVALID:
            return Response.status(409).build();
      }

      return Response.status(500).build();
   }

   @DELETE
   public Response deleteRoom(@PathParam("name") String roomName) {
      return Response.status(500).build();
   }
}
