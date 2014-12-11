package chatserver.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import chatserver.rest.representations.RoomsRepresentation;
import chatserver.rest.entities.Rooms;


@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomsResource {
  private Rooms rooms;

  public RoomsResource(Rooms rooms) {
    this.rooms = rooms;
  }

  @GET
  public Response getRooms() throws Exception {
    RoomsRepresentation rr = new RoomsRepresentation();
    rr.setRooms(rooms.getRooms());
    return Response.ok(new ObjectMapper().writeValueAsString(rr)).build();
  }
}