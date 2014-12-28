package chatserver.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import common.representations.RoomsRepresentation;
import chatserver.rest.entities.Rooms;
import com.google.gson.Gson;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomsResource {

   private final Rooms rooms;

   public RoomsResource(Rooms rooms) {
      this.rooms = rooms;
   }

   @GET
   public Response getRooms() throws Exception {
      RoomsRepresentation rr = new RoomsRepresentation(rooms.getRooms());
      return Response.ok(new Gson().toJson(rr)).build();
   }
}
