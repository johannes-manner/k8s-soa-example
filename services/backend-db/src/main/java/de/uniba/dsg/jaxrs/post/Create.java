package de.uniba.dsg.jaxrs.post;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import de.uniba.dsg.jaxrs.models.Cat;
import de.uniba.dsg.jaxrs.stubs.CatsService;

@Path("cats")
public class Create {

    @POST
    public Response createCat(Cat newCat, @Context UriInfo uriInfo) {
    	int id = CatsService.getInstance().getFamousCats().stream().mapToInt(c -> c.id).max().orElse(0);
    	newCat.id = id + 1;
    	Cat cat = CatsService.getInstance().addCat(newCat);

        if (cat == null) {
            throw new WebApplicationException("Invalid request body" , 400);
        }

        UriBuilder path = uriInfo.getAbsolutePathBuilder();
        path.path(Integer.toString(cat.getId()));
        return Response.created(path.build()).build();
    }
}
