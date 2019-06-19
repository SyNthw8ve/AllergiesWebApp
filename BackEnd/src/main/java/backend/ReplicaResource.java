/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import data.DeleteLocation;
import data.DeleteAllergy;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.spi.resource.Singleton;
import data.Allergies;
import data.Allergy;
import data.DataObject;
import data.Location;
import data.Locations;
import data.NewLocation;
import data.SMath;
import data.SubCode;
import data.User;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
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

    private final Hashtable<String, Object> responses;

    public ReplicaResource() {

        this.responses = new Hashtable<>();
    }

    @POST
    @Path("/rep")
    @Consumes({"application/json", "application/xml"})
    public synchronized void put_replica(DataObject database) throws Exception {

        this.database = database;

        pc = new PostgresConnector(this.database.get_host(), this.database.get_db(), this.database.get_user(), this.database.get_psw());
        pc.connect();
        System.out.println("added data");
    }

    @GET
    @Path("/user_locations")
    @Produces({"application/json", "application/xml"})
    public synchronized Locations get_user_locations(@QueryParam("request_id") String request_id, @QueryParam("id") int id) {

        LinkedList<Location> locations = new LinkedList<>();

        if (this.responses.containsKey(request_id)) {

            return (Locations) this.responses.get(request_id);
        }

        try {

            Statement state = this.pc.getStatement();

            String query = "SELECT id, polen_type, long, lat, data FROM polen WHERE user_id =" + id + ";";

            ResultSet set = state.executeQuery(query);

            while (set.next()) {

                int loc_id = set.getInt("id");
                int type = set.getInt("polen_type");
                float lng = set.getFloat("long");
                float lat = set.getFloat("lat");
                long date = set.getLong("data");

                Location l = new Location(lng, lat, type, loc_id, "");

                l.set_date(date);

                locations.add(l);

            }

            set.close();

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("User locations");

        this.responses.put(request_id, new Locations(locations));

        return new Locations(locations);
    }

    @GET
    @Path("/user")
    @Produces({"application/json", "application/xml"})
    public synchronized User get_user(@QueryParam("request_id") String request_id, @QueryParam("username") String username) throws Exception {

        User user = new User();

        if (this.responses.containsKey(request_id)) {

            return (User) this.responses.get(request_id);
        }

        try {

            Statement state = pc.getStatement();

            String query = "SELECT * FROM users WHERE username = '" + username + "';";
            ResultSet set = state.executeQuery(query);

            set.next();

            int user_id = set.getInt("id");
            String username_db = set.getString("username");
            LinkedList<Allergy> allergies = new LinkedList<>();

            query = "SELECT user_id, type FROM allergies WHERE user_id = " + user_id + ";";

            set = state.executeQuery(query);

            while (set.next()) {

                allergies.add(new Allergy(set.getInt("user_id"), set.getInt("type"), ""));
            }

            user = new User(username_db, "", allergies, user_id, "");

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.responses.put(request_id, user);

        return user;

    }

    @Path("/user")
    @PUT
    @Consumes({"application/json", "application/xml"})
    public synchronized Response add_user(User u) throws Exception {

        if (this.responses.containsKey(u.get_request_id())) {

            return (Response) this.responses.get(u.get_request_id());
        }

        Response resp;

        try {

            if (!check_existance(u.get_username())) {

                Statement state = pc.getStatement();

                String query = "INSERT INTO users (username, password) VALUES ('" + u.get_username() + "',md5('" + u.get_password() + "')) RETURNING id;";
                ResultSet set = state.executeQuery(query);

                set.next();

                u.set_id(set.getInt(1));

                query = "INSERT INTO user_roles(user_id, username, role) VALUES (" + u.get_id() + ",'" + u.get_username() + "', 'user');";
                state.execute(query);

                if (null != u.get_polen()) {

                    for (Allergy al : u.get_polen()) {

                        query = "INSERT INTO allergies(user_id, type) VALUES (" + u.get_id() + "," + al.get_type() + ")";
                        state.execute(query);
                    }

                }

                set.close();

            } else {

                resp = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Username alreay in use").build();

                this.responses.put(u.get_request_id(), resp);

                return resp;

            }

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

            resp = Response.status(Response.Status.NOT_ACCEPTABLE).entity("Username alreay in use").build();

            this.responses.put(u.get_request_id(), resp);

            return resp;
        }

        this.propagate_user(u);

        System.out.println("Adding user " + u.get_username());

        resp = Response.ok().build();

        this.responses.put(u.get_request_id(), resp);

        return resp;
    }

    @Path("/location")
    @POST
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public synchronized SubCode add_location(NewLocation l) throws Exception {

        int submissionCode = -1;

        if (this.responses.containsKey(l.get_request_id())) {

            return (SubCode) this.responses.get(l.get_request_id());
        }

        try {

            Statement state = pc.getStatement();

            String query = "INSERT INTO submission_codes DEFAULT VALUES RETURNING codesub;";

            ResultSet result = state.executeQuery(query);
            result.next();

            submissionCode = result.getInt(1);

            query = "INSERT INTO polen (polen_type, long, lat, user_id, data, cod_sub) VALUES (" + l.get_type() + "," + l.get_lng() + ","
                    + l.get_lat() + "," + l.get_user_id() + "," + l.get_date() + "," + submissionCode + ");";

            state.execute(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.propagate_add_location(l);

        this.responses.put(l.get_request_id(), new SubCode(submissionCode));

        System.out.println("Adding location");

        return new SubCode(submissionCode);
    }

    @DELETE
    @Path("/location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void delete_location(@QueryParam("request_id") String request_id, @QueryParam("id") int id, @QueryParam("user_id") int user_id) throws NotBoundException, MalformedURLException, RemoteException, IOException {

        if (this.responses.containsKey(request_id)) {

            return;
        }

        try {

            Statement state = this.pc.getStatement();

            String query = "DELETE FROM polen WHERE id = " + id + " AND user_id = " + user_id + ";";

            state.executeUpdate(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.propagate_remove_location(user_id, id);

        System.out.println("Removing location");
    }

    @GET
    @Path("/location")
    @Produces({"application/json", "application/xml"})
    public synchronized Locations get_locations(@QueryParam("request_id") String request_id) throws Exception {

        List<Location> locations = new LinkedList<>();

        if (this.responses.containsKey(request_id)) {

            return (Locations) this.responses.get(request_id);
        }

        try {
            Statement state = pc.getStatement();

            String query = "SELECT id, polen_type, long, lat, data FROM polen;";

            ResultSet set = state.executeQuery(query);

            while (set.next()) {

                int loc_id = set.getInt("id");
                int type = set.getInt("polen_type");
                float lng = set.getFloat("long");
                float lat = set.getFloat("lat");
                long date = set.getLong("data");

                Location l = new Location(lng, lat, type, loc_id, "");

                l.set_date(date);

                locations.add(l);
            }

            set.close();

        } catch (SQLException ex) {
            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.responses.put(request_id, new Locations(locations));

        return new Locations(locations);
    }

    @PUT
    @Path("/location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void update_location(Location l) throws Exception {

        if (this.responses.containsKey(l.get_request_id())) {

            return;
        }

        try {

            Statement state = pc.getStatement();

            String query = "UPDATE polen SET polen_type = " + l.get_type() + ", long = " + l.get_long() + ", lat = " + l.get_lat() + ""
                    + "WHERE id = " + l.get_id() + ";";

            state.executeUpdate(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.propagate_update_location(l);

        System.out.println("Updating location");
    }

    @GET
    @Path("/allergies")
    @Consumes({"application/json", "application/xml"})
    public synchronized Allergies get_allergy(@QueryParam("request_id") String request_id, @QueryParam("id") int id) throws Exception {

        LinkedList<Allergy> allergies = new LinkedList<>();

        if (this.responses.containsKey(request_id)) {

            return (Allergies) this.responses.get(request_id);
        }

        try {

            Statement state = pc.getStatement();

            String query = "SELECT user_id, type FROM allergies WHERE user_id = " + id + ";";

            ResultSet set = state.executeQuery(query);

            while (set.next()) {

                allergies.add(new Allergy(set.getInt("user_id"), set.getInt("type"), ""));
            }

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

        }

        this.responses.put(request_id, new Allergies(allergies));

        return new Allergies(allergies);

    }

    @POST
    @Path("/allergies")
    @Consumes({"application/json", "application/xml"})
    public synchronized void add_allergy(Allergy allergy) throws Exception {

        if (this.responses.containsKey(allergy.get_request_id())) {

            return;
        }

        if (!allergic(allergy)) {

            try {

                Statement state = pc.getStatement();

                String query = "INSERT INTO allergies (user_id, type) VALUES (" + allergy.get_id() + "," + allergy.get_type() + ");";

                state.execute(query);

            } catch (SQLException ex) {

                Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
            }

            this.propagate_add_allergy(allergy);

            System.out.println("Adding allergy");

        } else {
            
            System.out.println("Already allergic");
        }
    }

    @DELETE
    @Path("/allergies")
    @Consumes({"application/json", "application/xml"})
    public synchronized void remove_allergy(@QueryParam("request_id") String request_id, @QueryParam("user_id") int user_id, @QueryParam("type") int type) throws Exception {

        if (this.responses.containsKey(request_id)) {

            return;
        }

        try {
            Statement state = pc.getStatement();

            String query = "DELETE FROM allergies WHERE user_id =" + user_id + " AND type =" + type + ";";

            state.execute(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.propagate_remove_allergy(user_id, type);

        System.out.println("Removing allergy");
    }

    @GET
    @Path("/risk")
    @Consumes({"application/json", "application/xml"})
    @Produces({"application/json", "application/xml"})
    public synchronized Locations get_risk(@QueryParam("request_id") String request_id, @QueryParam("id") int id, @QueryParam("lng") float lng, @QueryParam("lat") float lat) {

        LinkedList<Location> risk_locations = new LinkedList<>();
        LinkedList<Integer> types = new LinkedList<>();

        if (this.responses.containsKey(request_id)) {

            return (Locations) this.responses.get(request_id);
        }

        try {
            Statement state = pc.getStatement();

            String query = "SELECT type from allergies WHERE user_id = " + id + ";";

            ResultSet set = state.executeQuery(query);

            while (set.next()) {

                types.add(set.getInt("type"));
            }

            query = "SELECT polen_type, long, lat, data FROM polen;";

            set = state.executeQuery(query);

            while (set.next()) {

                if (types.contains(set.getInt("polen_type"))) {

                    double distance = SMath.haversine(lng, lat, set.getFloat("long"), set.getFloat("lat"));

                    if (distance <= 100) {

                        int type = set.getInt("polen_type");
                        float lng_risk = set.getFloat("long");
                        float lat_risk = set.getFloat("lat");
                        long date = set.getLong("data");

                        Location l = new Location(lng_risk, lat_risk, type, -1, "");
                        l.set_date(date);

                        risk_locations.add(l);
                    }
                }
            }

            set.close();

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Getting risks");

        this.responses.put(request_id, new Locations(risk_locations));

        return new Locations(risk_locations);

    }

    public Vector<ReplicaManager> get_replicas() throws NotBoundException, MalformedURLException, RemoteException, IOException {

        InputStream in = new FileInputStream("./conf.properties");

        Properties prop = new Properties();

        prop.load(in);

        String regHost = prop.getProperty("reg.host", "localhost");
        String regPort = prop.getProperty("reg.port", "9000");

        ServiceManager sm = (ServiceManager) java.rmi.Naming.lookup("rmi://" + regHost + ":"
                + regPort + "/service");

        return sm.get_replicas();
    }

    public void propagate_user(User u) throws NotBoundException, MalformedURLException, RemoteException, IOException {

        Vector<ReplicaManager> replicas = this.get_replicas();

        for (ReplicaManager rm : replicas) {

            if (!rm.is_primary()) {

                String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/prop_user";

                com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
                WebResource web_resource;

                web_resource = client.resource(uri);

                try {

                    web_resource.type(MediaType.APPLICATION_XML).post(User.class, u);

                } catch (Exception e) {

                    System.out.println(e.toString());
                }
            }
        }
    }

    public void propagate_add_location(NewLocation l) throws NotBoundException, MalformedURLException, RemoteException, IOException {

        Vector<ReplicaManager> replicas = this.get_replicas();

        for (ReplicaManager rm : replicas) {

            if (!rm.is_primary()) {

                String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/prop_location";

                com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
                WebResource web_resource;

                web_resource = client.resource(uri);

                try {

                    web_resource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, l);

                } catch (Exception e) {

                    System.out.println(e.toString());
                }
            }
        }
    }

    public void propagate_remove_location(int user_id, int id) throws NotBoundException, MalformedURLException, RemoteException, IOException {

        Vector<ReplicaManager> replicas = this.get_replicas();

        DeleteLocation del = new DeleteLocation(id, user_id);

        for (ReplicaManager rm : replicas) {

            if (!rm.is_primary()) {

                String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/prop_location";

                com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
                WebResource web_resource;

                web_resource = client.resource(uri);

                try {

                    web_resource.type(MediaType.APPLICATION_XML).delete(del);

                } catch (Exception e) {

                    System.out.println(e.toString());
                }
            }
        }
    }

    public void propagate_update_location(Location l) throws NotBoundException, MalformedURLException, RemoteException, IOException {

        Vector<ReplicaManager> replicas = this.get_replicas();

        for (ReplicaManager rm : replicas) {

            if (!rm.is_primary()) {

                String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/prop_location";

                com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
                WebResource web_resource;

                System.out.println("Updating location to replica");

                web_resource = client.resource(uri);

                try {

                    web_resource.type(MediaType.APPLICATION_XML).put(ClientResponse.class, l);

                } catch (Exception e) {

                    System.out.println(e.toString());
                }
            }
        }
    }

    public void propagate_add_allergy(Allergy allergy) throws NotBoundException, MalformedURLException, RemoteException, IOException {

        Vector<ReplicaManager> replicas = this.get_replicas();

        for (ReplicaManager rm : replicas) {

            if (!rm.is_primary()) {

                String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/prop_allergy";

                com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
                WebResource web_resource;

                web_resource = client.resource(uri);

                try {

                    web_resource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, allergy);

                } catch (Exception e) {

                    System.out.println(e.toString());
                }
            }
        }
    }

    public void propagate_remove_allergy(int user_id, int type) throws NotBoundException, MalformedURLException, RemoteException, IOException {

        Vector<ReplicaManager> replicas = this.get_replicas();

        DeleteAllergy del = new DeleteAllergy(user_id, type);

        for (ReplicaManager rm : replicas) {

            if (!rm.is_primary()) {

                String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/prop_allergy";

                com.sun.jersey.api.client.Client client = com.sun.jersey.api.client.Client.create();
                WebResource web_resource;

                web_resource = client.resource(uri);

                try {

                    web_resource.type(MediaType.APPLICATION_XML).delete(del);

                } catch (Exception e) {

                    System.out.println(e.toString());
                }
            }
        }
    }

    @POST
    @Path("/prop_user")
    @Consumes({"application/json", "application/xml"})
    public synchronized void prop_user(User u) {

        System.out.println("Adding user prop");

        try {

            if (!check_existance(u.get_username())) {

                Statement state = pc.getStatement();

                String query = "INSERT INTO users (username, password) VALUES ('" + u.get_username() + "',md5('" + u.get_password() + "')) RETURNING id;";
                ResultSet set = state.executeQuery(query);

                set.next();

                u.set_id(set.getInt(1));

                query = "INSERT INTO user_roles(user_id, username, role) VALUES (" + u.get_id() + ",'" + u.get_username() + "', 'user');";
                state.execute(query);

                if (null != u.get_polen()) {

                    for (Allergy al : u.get_polen()) {

                        query = "INSERT INTO allergies(user_id, type) VALUES (" + u.get_id() + "," + al.get_type() + ")";
                        state.execute(query);
                    }

                }

                set.close();

            }

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    @POST
    @Path("/prop_location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void prop_add_location(NewLocation l) {

        int submissionCode = -1;

        try {

            Statement state = pc.getStatement();

            String query = "INSERT INTO submission_codes DEFAULT VALUES RETURNING codesub;";

            ResultSet result = state.executeQuery(query);
            result.next();

            submissionCode = result.getInt(1);

            query = "INSERT INTO polen (polen_type, long, lat, user_id, data, cod_sub) VALUES (" + l.get_type() + "," + l.get_lng() + ","
                    + l.get_lat() + "," + l.get_user_id() + "," + l.get_date() + "," + submissionCode + ");";

            state.execute(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

        }

        System.out.println("Added location replica");
    }

    @DELETE
    @Path("/prop_location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void prop_remove_location(DeleteLocation del) {

        try {

            Statement state = this.pc.getStatement();

            String query = "DELETE FROM polen WHERE id = " + del.get_id() + " AND user_id = " + del.get_user_id() + ";";

            state.executeUpdate(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

        }

        System.out.println("Removing prop");
    }

    @PUT
    @Path("/prop_location")
    @Consumes({"application/json", "application/xml"})
    public synchronized void prop_update_location(Location l) {

        try {

            Statement state = pc.getStatement();

            String query = "UPDATE polen SET polen_type = " + l.get_type() + ", long = " + l.get_long() + ", lat = " + l.get_lat() + ""
                    + "WHERE id = " + l.get_id() + ";";

            state.executeUpdate(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

        }

        System.out.println("Updating location from replica");

    }

    @POST
    @Path("/prop_allergy")
    @Consumes({"application/json", "application/xml"})
    public synchronized void prop_add_allergy(Allergy allergy) {

        try {
            Statement state = pc.getStatement();

            String query = "INSERT INTO allergies (user_id, type) VALUES (" + allergy.get_id() + "," + allergy.get_type() + ");";

            state.execute(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    @DELETE
    @Path("/prop_allergy")
    @Consumes({"application/json", "application/xml"})
    public synchronized void prop_remove_allergy(DeleteAllergy del) {

        try {
            Statement state = pc.getStatement();

            String query = "DELETE FROM allergies WHERE user_id =" + del.get_user_id() + " AND type =" + del.get_type() + ";";

            state.execute(query);

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    public boolean check_existance(String username) throws SQLException {

        Statement state = pc.getStatement();

        String query = "SELECT COUNT(*) AS rowcount FROM users WHERE username = '" + username + "';";

        ResultSet results = state.executeQuery(query);

        results.next();

        int size = results.getInt("rowcount");

        return size > 0;
    }

    public boolean allergic(Allergy allergy) throws SQLException {

        Statement state = pc.getStatement();

        String query = "SELECT type FROM allergies WHERE user_id =" + allergy.get_id() + ";";

        ResultSet set = state.executeQuery(query);

        LinkedList<Integer> allergies = new LinkedList<>();

        while (set.next()) {

            allergies.add(set.getInt("type"));
        }

        return allergies.contains(allergy.get_type());
    }

}
