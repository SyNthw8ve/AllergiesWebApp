/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import com.sun.jersey.spi.resource.Singleton;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
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
    private PostgresConnector pc;

    @POST
    @Path("/rep")
    @Consumes({"application/json", "application/xml"})
    public synchronized void put_replica(DataObject database) throws Exception {

        this.database = database;
        
        pc = new PostgresConnector(this.database.get_host(),this.database.get_db(), this.database.get_user(), this.database.get_psw());
        pc.connect();
        System.out.println("added data");
    }
    
    @GET
    @Path("/user_locations")
    @Produces({"application/json", "application/xml"})
    public synchronized Locations get_user_locations(@QueryParam("id") int id) {
        
        LinkedList<Location> locations = new LinkedList<>();

        try {

            Statement state = this.pc.getStatement();

            String query = "SELECT id, polen_type, long, lat FROM polen WHERE user_id =" + id + ";";

            ResultSet set = state.executeQuery(query);

            while (set.next()) {

                int loc_id = set.getInt("id");
                int type = set.getInt("polen_type");
                float lng = set.getFloat("long");
                float lat = set.getFloat("lat");
                
                locations.add(new Location( lng, lat, type, loc_id));

            }

            set.close();
            

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("User locations");

        return new Locations(locations);
    }
    
    @GET
    @Path("/user")
    @Produces({"application/json", "application/xml"})
    public synchronized User get_user(@QueryParam("username") String username) throws Exception {
    
        User user = new User();
        
        try {

            Statement state = pc.getStatement();

            String query = "SELECT * FROM users WHERE username = '" + username + "';";
            ResultSet set = state.executeQuery(query);
            
            set.next();

            user = new User(set.getString("username"), "", null, set.getInt("id"));
            

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return user;
    
    }
    
    @Path("/user")
    @PUT
    @Consumes({"application/json", "application/xml"})
    public synchronized void add_user(User u) throws Exception {
        
        try {

            Statement state = pc.getStatement();

            String query = "INSERT INTO users (username, password) VALUES ('" + u.get_username() + "',md5('" + u.get_password() + "')) RETURNING id;";
            ResultSet set = state.executeQuery(query);

            set.next();

            u.id = set.getInt(1);
            
            query = "INSERT INTO user_roles(user_id, username, role) VALUES (" + u.id + ",'" + u.get_username() +"', 'user');";
            state.execute(query);
            
            set.close();

            /*for (int polen : u.get_polen()) {

                query = "INSERT INTO allergies (user_id, type) VALUES (" + u.id + "," + polen + ");";

                state.execute(query);
            }*/

            

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        System.out.println("Adding user " + u.get_username());
    }

    @Path("/location")
    @PUT
    @Consumes({"application/json", "application/xml"})
    public synchronized void add_location(Location l, @QueryParam("id") int id) throws Exception {
        System.out.println("Adding location1");
        try {

            Statement state = pc.getStatement();

            Date date = new Date();

            String query = "INSERT INTO polen (polen_type, long, lat, user_id, date) VALUES (" + l.get_type() + "," + l.get_long() + ","
                    + l.get_lat() + "," + id + "," + date.getTime() + ");";
            
            state.executeQuery(query);
            
        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        System.out.println("Adding location");
    }

    @DELETE
    @Path("/location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void delete_location(@QueryParam("id") int id, @QueryParam("user_id") int user_id) {

        try {

            Statement state = this.pc.getStatement();

            String query = "DELETE FROM polen WHERE id = " + id + " AND user_id = " + user_id + ";";

            state.executeUpdate(query);
            

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println("Removing location");
    }
    
    @GET
    @Path("/location")
    @Produces({"application/json", "application/xml"})
    public synchronized Locations get_locations() throws Exception {
        
        List<Location> locations = new LinkedList<>();

        try {
            Statement state = pc.getStatement();

            String query = "SELECT id, polen_type, long, lat FROM polen;";

            ResultSet set = state.executeQuery(query);

            while (set.next()) {

                int loc_id = set.getInt("id");
                int type = set.getInt("polen_type");
                float lng = set.getFloat("long");
                float lat = set.getFloat("lat");
                
                locations.add(new Location( lng, lat, type, loc_id));
            }

            set.close();

        } catch (SQLException ex) {
            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new Locations(locations);
    }

}
