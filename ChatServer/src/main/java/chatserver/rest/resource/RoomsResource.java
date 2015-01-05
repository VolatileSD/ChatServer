package chatserver.rest.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import common.representation.RoomsRepresentation;
import chatserver.rest.entity.Rooms;
import com.google.gson.Gson;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomsResource {

   private final Rooms rooms;
   private final Gson gson;
   
   public RoomsResource(Rooms rooms) {
      this.rooms = rooms;
      this.gson = new Gson();
   }

   @GET
   public Response getRooms() throws Exception {
      RoomsRepresentation rr = new RoomsRepresentation(rooms.getRooms());
      return Response.ok(gson.toJson(rr)).build();
   }
}
