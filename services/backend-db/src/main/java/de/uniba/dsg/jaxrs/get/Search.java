package de.uniba.dsg.jaxrs.get;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import de.uniba.dsg.jaxrs.models.Cat;
import de.uniba.dsg.jaxrs.stubs.CatsService;

@Path("search")
public class Search {

    @GET
    public List<Cat> getCats(@QueryParam("movie") String movie) {
        return CatsService.getInstance().getFamousCats(movie);
    }
}
