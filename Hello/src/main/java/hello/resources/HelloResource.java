package hello.resources;

import hello.representations.Saying;

import com.google.common.base.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class HelloResource {
    private final String template;
    private volatile String defaultName;
    private long counter;

    public HelloResource(String template, String defaultName) {
        this.template = template;
        this.defaultName = defaultName;
    }

    @GET
    public Saying sayHello(@QueryParam("name") Optional<String> name) {
        final String content = String.format(template, name.or(defaultName));
        long i;
        synchronized (this) { counter++; i = counter; }
        return new Saying(i, content);
    }

    @PUT
    @Path("/default/{name}")
    public Response put(@PathParam("name") String name) {
        defaultName = name;
        return Response.ok().build();
    }
}

