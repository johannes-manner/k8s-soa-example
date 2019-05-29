package de.uniba.dsg.jaxrs.delete;

import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

import de.uniba.dsg.jaxrs.stubs.CatsService;

@Path("cats")
public class Delete {

    @DELETE
    @Path("{cat-id}")
    public void deleteCat(@PathParam("cat-id") int id) {
        boolean deleted = CatsService.getInstance().deleteCat(id);

        if (!deleted) {
            throw new WebApplicationException("No cat found with id: " + id, 404);
        }
    }
}
