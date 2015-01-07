package chatserver.rest.resource;

import chatserver.util.Msg;
import chatserver.util.MsgType;
import chatserver.util.Pigeon;
import co.paralleluniverse.actors.ActorRef;
import com.google.gson.Gson;
import common.representation.UserRepresentation;
import java.util.Base64;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/admin/{username}")
@Produces(MediaType.APPLICATION_JSON)
public class AdminsResource {

   private final ActorRef manager;
   private final Gson gson;
   private final Base64.Decoder decoder;

   public AdminsResource(ActorRef manager) {
      this.manager = manager;
      this.gson = new Gson();
      this.decoder = Base64.getDecoder();
   }

   @PUT
   public Response addAdmin(@HeaderParam("Volatile-ChatServer-Auth") String auth, @PathParam("username") String username) throws Exception {
      if (auth == null) {
         throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      String authJson = new String(decoder.decode(auth));
      UserRepresentation user = gson.fromJson(authJson, UserRepresentation.class);
      Response.ResponseBuilder response;
      Msg msg = new Pigeon(manager).carry(MsgType.MAKE_ADMIN, null, new String[]{username, user.getUsername(), user.getPassword()});
      switch (msg.getType()) {
         case OK:
            response = Response.status(Response.Status.CREATED);
            break;
         case INVALID:
            response = Response.status(Response.Status.NOT_FOUND);
            break;
         case UNAUTHORIZED:
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
         default:
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            break;
      }
      return response.build();
   }
   
   @DELETE
   public Response removeAdmin(@HeaderParam("Volatile-ChatServer-Auth") String auth, @PathParam("username") String username) throws Exception {
      if (auth == null) {
         throw new WebApplicationException(Response.Status.UNAUTHORIZED);
      }

      String authJson = new String(decoder.decode(auth));
      UserRepresentation user = gson.fromJson(authJson, UserRepresentation.class);
      Response.ResponseBuilder response;
      Msg msg = new Pigeon(manager).carry(MsgType.REMOVE_ADMIN, null, new String[]{username, user.getUsername(), user.getPassword()});
      switch (msg.getType()) {
         case OK:
            response = Response.ok();
            break;
         case INVALID:
            response = Response.status(Response.Status.NOT_FOUND);
            break;
         case UNAUTHORIZED:
            throw new WebApplicationException(Response.Status.UNAUTHORIZED);
         default:
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR);
            break;
      }
      return response.build();
   }

}
