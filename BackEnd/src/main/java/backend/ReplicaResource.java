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

/**
 *
 * @author nuno1
 */

@Singleton
@Path(value = "/replica")
public class ReplicaResource {
    
    @PUT
    @Consumes({"application/json", "application/xml"})
    public synchronized void add_user(User u) {
        
        System.out.println("Adding user");
    }
    
    @PUT
    @Consumes({"application/json", "application/xml"})
    public synchronized void add_location() {
        
        System.out.println("Adding location");
    }
    
    @DELETE
    @Consumes({"application/json", "application/xml"})
    public synchronized void delete_location() {
        
        System.out.println("Removing location");
    }
    
}
