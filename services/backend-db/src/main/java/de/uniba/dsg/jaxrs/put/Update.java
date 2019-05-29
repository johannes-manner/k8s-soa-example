package de.uniba.dsg.jaxrs.put;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import de.uniba.dsg.jaxrs.models.Cat;
import de.uniba.dsg.jaxrs.stubs.CatsService;

@Path("cats")
public class Update {

    @PUT
    @Path("{cat-id}")
    public Response updateCat(@PathParam("cat-id") int id, Cat updatedCat) {
        Cat cat = CatsService.getInstance().getFamousCat(id);
        if (cat == null) {
            throw new WebApplicationException("No cat found with id: " + id, 404);
        }

        if (updatedCat == null) {
            throw new WebApplicationException("Invalid request body", 400);
        }

        Cat resultCat = CatsService.getInstance().updateCat(id, updatedCat);
        System.out.println(resultCat);
        return Response.ok().entity(resultCat).build();
    }
}
