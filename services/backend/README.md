# JAX-RS Samples

*Sample projects using JAX-RS technologies.*

Use `gradlew run` to start the JAX-RS server.
The server is available at `localhost:9999` per default. 
This can be configured via `src/main/resources/config.properties`.

The following resources are available:

`GET /cats`  
Returns all available famous cats.  
Implementation inside `ReadAll.java`.

`GET /cats/1`  
Returns a famous cat via its identifier.  
Implementation inside `ReadSingle.java`.

`GET /search?movie=Garfield`  
Returns famous cats that are featured in a particular movie.  
Implementation inside `Search.java`.

`POST /cats`  
Adds a new cat to the list of famous cats.  
Implementation inside `Create.java`.

`PUT /cats/1`  
Updates the attributes of an existing famous cat.   
Implementation inside `Update.java`.

`DELETE /cats/1`  
Deletes an existing famous cat via its id.  
Implementation inside `Delete.java`.