/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import com.sun.jersey.spi.resource.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nuno1
 */
@Singleton
@Path(value = "/replica")
public class ReplicaResource {

    private DataObject database;

    @POST
    @Path("/rep")
    @Consumes({"application/json", "application/xml"})
    public synchronized void put_replica(DataObject database) {

        this.database = database;
        System.out.println("added data");
    }

    @POST
    @Path("/user")
    @Consumes({"application/json", "application/xml"})
    public synchronized void add_user(User u) {

        
        System.out.println(database.get_db());
        
        System.out.println("Adding user " + u.get_username());
    }

    @PUT
    @Path("/location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void add_location() {

        System.out.println("Adding location");
    }

    @DELETE
    @Path("/location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void delete_location() {

        System.out.println("Removing location");
    }

}
