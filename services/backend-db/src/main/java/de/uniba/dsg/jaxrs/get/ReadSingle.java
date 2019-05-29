package de.uniba.dsg.jaxrs.get;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import de.uniba.dsg.jaxrs.models.Cat;
import de.uniba.dsg.jaxrs.stubs.CatsService;

@Path("cats")
public class ReadSingle {

    @GET
    @Path("{cat-id}")
    public Cat getCat(@PathParam("cat-id") int id) {
    	Cat cat = CatsService.getInstance().getFamousCat(id);

        if (cat == null) {
            throw new WebApplicationException("No cat found with id: " + id, 404);
        }
        return cat;
    }
}
