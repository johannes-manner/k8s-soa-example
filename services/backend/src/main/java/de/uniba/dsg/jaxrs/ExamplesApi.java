package de.uniba.dsg.jaxrs;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import de.uniba.dsg.jaxrs.delete.Delete;
import de.uniba.dsg.jaxrs.get.ReadAll;
import de.uniba.dsg.jaxrs.get.Search;
import de.uniba.dsg.jaxrs.health.Liveness;
import de.uniba.dsg.jaxrs.health.Readiness;
import de.uniba.dsg.jaxrs.post.Create;
import de.uniba.dsg.jaxrs.put.Update;
import de.uniba.dsg.jaxrs.get.ReadSingle;

@ApplicationPath("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ExamplesApi extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(ReadAll.class);
        resources.add(Search.class);
        resources.add(ReadSingle.class);
        resources.add(Delete.class);
        resources.add(Create.class);
        resources.add(Update.class);
        resources.add(Liveness.class);
        resources.add(Readiness.class);

        return resources;
    }
}
