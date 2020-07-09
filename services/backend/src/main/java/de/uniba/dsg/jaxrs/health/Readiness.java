package de.uniba.dsg.jaxrs.health;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("ready")
public class Readiness {

    @GET
    public Response checkReadiness(){
        // do some business logic readiness check
        // check if the service is really ready to serve requests

        // simulate Server error
//        if(Math.random() > 0.3) {
//            System.out.println("Failed readiness probe . . .");
//            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
//        }

        System.out.println("Succeeded readiness probe . . .");
        return Response.ok().build();
    }
}
