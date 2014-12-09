package chatserver.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import co.paralleluniverse.actors.*;
import co.paralleluniverse.fibers.SuspendExecution;
import co.paralleluniverse.fibers.io.*;

import chatserver.util.Msg;
import chatserver.util.MsgType;


@Path("/room")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {
    //private final ActorRef room;

    public RoomResource() {
        //this.room = room;
    }

    @GET
    @Path("/users")
    public Response getUsers() {
        //room.send(new Msg(MsgType.USERS, self()));
        return Response.ok().build();
    }

    @GET
    @Path("/test")
    public Response test() {
        return Response.status(403).build();
    }
}

