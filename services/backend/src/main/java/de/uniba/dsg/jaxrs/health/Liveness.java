package de.uniba.dsg.jaxrs.health;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("healthy")
public class Liveness {

    @GET
    public Response checkLiveness() {
        System.out.println("Liveness probe . . . ");

        return Response.ok().build();
    }
}
