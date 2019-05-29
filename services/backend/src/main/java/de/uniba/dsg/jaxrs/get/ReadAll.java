package de.uniba.dsg.jaxrs.get;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

import de.uniba.dsg.jaxrs.models.Cat;
import de.uniba.dsg.jaxrs.stubs.CatsService;

@Path("cats")
public class ReadAll {

    @GET
    public List<Cat> getCats() {
        return CatsService.getFamousCats();
    }
}
