package chatserver.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import co.paralleluniverse.actors.ActorRef;
import java.util.Collection;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;
import common.representation.RoomRepresentation;
import chatserver.rest.entity.Rooms;
import com.google.gson.Gson;
import common.representation.UserRepresentation;
import java.util.Base64;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

@Path("/room/{name}")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

   private final ActorRef roomManager;
   private final Rooms rooms;
   private final Gson gson;
   private final Base64.Decoder decoder;

   public RoomResource(ActorRef roomManager, Rooms rooms) {
      this.rooms = rooms;
      this.roomManager = roomManager;
      this.gson = new Gson();
      this.decoder = Base64.getDecoder();
   }

   @GET
   public Response getRoomInfo(@PathParam("name") String roomName) throws Exception {
      ResponseBuilder response;
      if (rooms.has(roomName)) {
         Msg msg = new Pigeon(roomManager).carry(MsgType.ROOM_INFO, null, roomName);
         switch (msg.getType()) {
            case OK:
               RoomRepresentation rr = new RoomRepresentation(roomName, (Collection<String>) msg.getContent());
               response = Response.ok(gson.toJson(rr));
               break;
            default:
               response = Response.status(Status.INTERNAL_SERVER_ERROR);
               break;
         }
      } else {
         response = Response.status(Status.NOT_FOUND);
      }

      return response.build();
   }

   @PUT
   public Response createRoom(@HeaderParam("Volatile-ChatServer-Auth") String auth, @PathParam("name") String roomName) throws Exception {
      if (auth == null) {
         throw new WebApplicationException(Status.UNAUTHORIZED);
      }
      
      String authJson = new String(decoder.decode(auth));
      UserRepresentation user = gson.fromJson(authJson, UserRepresentation.class);
      ResponseBuilder response;
      if (!rooms.has(roomName)) {
         Msg msg = new Pigeon(roomManager).carry(MsgType.CREATE_ROOM, null, new String[]{roomName, user.getUsername(), user.getPassword()});
         switch (msg.getType()) {
            case OK:
               rooms.addRoom(roomName);
               response = Response.status(Status.CREATED);
               break;
            case UNAUTHORIZED:
               throw new WebApplicationException(Status.UNAUTHORIZED);
            default:
               response = Response.status(Status.INTERNAL_SERVER_ERROR);
               break;
         }
      } else {
         response = Response.status(Status.CONFLICT);
      }
      return response.build();
   }

   @DELETE
   public Response deleteRoom(@HeaderParam("Volatile-ChatServer-Auth") String auth, @PathParam("name") String roomName) throws Exception {
      if (auth == null) {
         throw new WebApplicationException(Status.UNAUTHORIZED);
      }
      
      String authJson = new String(decoder.decode(auth));
      UserRepresentation user = gson.fromJson(authJson, UserRepresentation.class);
      ResponseBuilder response;
      if (roomName.equals("Main")) { // Main cannot be removed
         response = Response.status(Status.PRECONDITION_FAILED);
      } else if (rooms.has(roomName)) {
         Msg msg = new Pigeon(roomManager).carry(MsgType.DELETE_ROOM, null, new String[]{roomName, user.getUsername(), user.getPassword()});
         switch (msg.getType()) {
            case OK:
               rooms.removeRoom(roomName);
               // should we return room representation here?
               response = Response.ok();
               break;
            case INVALID:
               response = Response.status(Status.PRECONDITION_FAILED);
               break;
            case UNAUTHORIZED:
               throw new WebApplicationException(Status.UNAUTHORIZED);
            default:
               response = Response.status(Status.INTERNAL_SERVER_ERROR);
               break;
         }
      } else {
         response = Response.status(Status.CONFLICT);
      }

      return response.build();
   }

}
