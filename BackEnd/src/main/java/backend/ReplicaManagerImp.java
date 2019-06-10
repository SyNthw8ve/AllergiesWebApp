/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import java.lang.Math;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;

/**
 *
 * @author nuno1
 */
public class ReplicaManagerImp extends UnicastRemoteObject implements ReplicaManager, java.io.Serializable {

    private final float earth_radius = 6371000;
    
    private String address;
    private int port;
    boolean is_primary = false;

    ServiceManager sm;
    PostgresConnector pc;

    public ReplicaManagerImp(String address, int port, ServiceManager sm, PostgresConnector pc) throws RemoteException {

        super();

        this.address = address;
        this.port = port;
        this.sm = sm;
        this.pc = pc;
    }

    @Override
    public boolean is_alive() throws RemoteException {

        return true;
    }

    @Override
    public Vector<Location> get_locations() throws RemoteException {

        Vector<Location> locations = new Vector<>();

        try {
            Statement state = this.pc.getStatement();

            String query = "SELECT polen_type, long, lat FROM polen;";

            ResultSet set = state.executeQuery(query);

            while (set.next()) {

                locations.add(new Location(set.getFloat("long"), set.getFloat("lat"), set.getInt("polen_type")));
            }

            set.close();

            state.close();
        } catch (SQLException ex) {
            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return locations;
    }

    @Override
    public User auth(AuthRequest a) throws RemoteException {

        String username;
        String password;
        int id;

        try {

            Statement state = this.pc.getStatement();

            String query = "SELECT id, username, password FROM users WHERE username = '" + a.get_username() + "';";

            ResultSet set = state.executeQuery(query);

            set.next();

            id = set.getInt(1);
            username = set.getString(2);
            password = set.getString(3);

            set.close();

            state.close();

            if (password == a.get_password()) {

                return new User(username, password, null, id);
            }

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public void add_user(User u) throws RemoteException {

        try {

            Statement state = this.pc.getStatement();

            String query = "INSERT INTO users (username, password) VALUES ('" + u.get_username() + "','" + u.get_password() + "') RETURNING id;";
            ResultSet set = state.executeQuery(query);

            set.next();

            u.id = set.getInt(1);

            set.close();
            state.close();

        } catch (SQLException ex) {

            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.update_user(u);
    }

    @Override
    public Vector<Location> get_risk(Location p, float distance, Date days, Vector<Integer> polen) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add_location(User u, Location p) throws RemoteException {

        try {
            
            Statement state = this.pc.getStatement();
            Date date = new Date();

            String query = "INSERT INTO polen (polen_type, long, lat, user_id, date) VALUES (" + p.get_type() + "," + p.get_long() + ","
                    + p.get_lat() + "," + u.id + "," + date.getTime() + ");";
            
            state.executeUpdate(query);
            
        } catch (SQLException ex) {
            
            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void remove_location(User u, int id) throws RemoteException {
        
        try {
            
            Statement state = this.pc.getStatement();
            
            String query = "DELETE FROM polen WHERE id = " + id + " AND user_id = " + u.id + ";";
            
            state.executeUpdate(query);
            state.close();
            
        } catch (SQLException ex) {
            
            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Vector<Location> get_user_locations(User u) throws RemoteException {
        
        Vector<Location> locations = new Vector<>();
        
        try {
            
            Statement state = this.pc.getStatement();
            
            String query = "SELECT polen_type, long, lat FROM polen WHERE user_id =" + u.id + ";";
            
            ResultSet set = state.executeQuery(query);
            
            while(set.next()) {
                
                locations.add(new Location(set.getFloat("long"), set.getFloat("lat"), set.getInt("type")));
                
            }
            
            set.close();
            state.close();
            
        } catch (SQLException ex) {
            
            Logger.getLogger(ReplicaManagerImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return locations;
    }

    @Override
    public void promote() throws RemoteException {

        this.is_primary = true;
    }

    @Override
    public boolean is_primary() throws RemoteException {

        return this.is_primary;
    }

    @Override
    public String get_address() throws RemoteException {

        return this.address;
    }

    @Override
    public int get_port() throws RemoteException {

        return this.port;
    }
    
    public double haversine(double longA, double latA, double longB, double latB) {
        
        double longRadA = to_radian(longA);
        double longRadB = to_radian(longB);
        double latRadA = to_radian(latA);
        double latRadB = to_radian(latB);
        
        double havLat = Math.pow(Math.sin((latRadA - latRadB)/2), 2);
        double havLong = Math.pow(Math.sin((longRadA - longRadB)/2), 2);
        
        double square = Math.sqrt(havLat + Math.cos(latA)*Math.cos(latB)*havLong);
        
        return 2*earth_radius*Math.asin(square);
    }
    
    public double to_radian(double x) {
        
        return x*(Math.PI/180);
    }

    public void update_user(User u) throws RemoteException {

        Vector<ReplicaManager> rms = this.sm.get_replicas();
        String uri;

        for (ReplicaManager rm : rms) {

            if (!rm.is_primary()) {

                Client client = Client.create();
                WebResource web_resource;

                uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica";
                web_resource = client.resource(uri);

                System.out.println(uri);

                ClientResponse res = web_resource.accept("application/json").get(ClientResponse.class);
            }
        }
    }

    private static int getPort(int defaultPort) {
        return defaultPort;
    }

    private static URI getBaseURI(String address, int port) {
        return UriBuilder.fromUri("http://" + address + "/allergies/").port(getPort(port)).build();
    }

    public static HttpServer startServer(URI base_uri) throws IOException {
        

        ResourceConfig rc = new PackagesResourceConfig("backend");
        return GrizzlyServerFactory.createHttpServer(base_uri, rc);
    }

    public static void main(String args[]) throws RemoteException, NotBoundException, MalformedURLException, IOException, Exception {

        String regHost = "localhost";
        int regPort = 9000;

        if (args.length == 2) {

            String address = args[0];
            int port = Integer.parseInt(args[1]);

            ServiceManager sm = (ServiceManager) java.rmi.Naming.lookup("rmi://" + regHost + ":"
                    + regPort + "/service");

            PostgresConnector pc = new PostgresConnector("localhost", "l38489", "l38489", "1234");

            pc.connect();

            ReplicaManagerImp replica = new ReplicaManagerImp(args[0], port, sm, pc);

            sm.add_replica(replica);

            URI uri = getBaseURI(address, port);

            HttpServer httpServer = startServer(uri);

            System.in.read();

            httpServer.stop();

            pc.disconnect();

        } else {

            System.out.println("Insuficient args");
        }

    }

}
