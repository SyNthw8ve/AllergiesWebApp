/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package backend;

import data.DataObject;
import data.Location;
import data.User;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import java.io.IOException;
import java.lang.Math;

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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;

/**
 *
 * @author nuno1
 */
public class ReplicaManagerImp extends UnicastRemoteObject implements ReplicaManager, java.io.Serializable {

    

    private String address;

    private int port;

    boolean is_primary = false;

    private ServiceManager sm;

    private PostgresConnector pc;

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
    public Vector<Location> get_risk(Location p, float distance, Date days, Vector<Integer> polen) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    
    public ServiceManager get_service() {

        return this.sm;
    }

    public void update_user(User u) throws RemoteException {

        Vector<ReplicaManager> rms = this.get_service().get_replicas();
        String uri;

        for (ReplicaManager rm : rms) {

            if (!rm.is_primary()) {

                Client client = Client.create();
                WebResource web_resource;

                uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/user";
                web_resource = client.resource(uri);

                System.out.println(uri);

                try {

                    web_resource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, u);

                } catch (Exception e) {

                    System.out.println(e.toString());
                }

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

    public static void add_database_info(DataObject data, String address, int port) {

        Client client = Client.create();
        WebResource web_resource;

        String url = "http://" + address + ":" + port + "/allergies/replica/rep";

        web_resource = client.resource(url);

        try {

            web_resource.type(MediaType.APPLICATION_XML).post(ClientResponse.class, data);

        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }

    public static void main(String args[]) throws RemoteException, NotBoundException, MalformedURLException, IOException, Exception {

        String regHost = "localhost";
        int regPort = 9000;

        if (args.length == 6) {

            try {
                String address = args[0];
                int port = Integer.parseInt(args[1]);
                
                String db_host = args[2];
                String db = args[3];
                String user = args[4];
                String password = args[5];

                ServiceManager sm = (ServiceManager) java.rmi.Naming.lookup("rmi://" + regHost + ":"
                        + regPort + "/service");

                PostgresConnector pc = new PostgresConnector(db_host, db, user, password);
                DataObject data = new DataObject(db_host, db, user, password);

                checkTables(db_host, db, user, password);

                ReplicaManagerImp replica = new ReplicaManagerImp(args[0], port, sm, pc);

                sm.add_replica(replica);

                URI uri = getBaseURI(address, port);

                HttpServer httpServer = startServer(uri);

                add_database_info(data, address, port);

                System.in.read();

                httpServer.stop();


            } catch (Exception e) {

                System.out.println(e.toString());
            }

        } else {

            System.out.println("Insuficient args");
        }

    }

    public static void checkTables(String host, String database, String user, String psw) throws Exception {

        PostgresConnector pc = new PostgresConnector(host, database, user, psw);

        pc.connect();

        try (Statement state = pc.getStatement()) {
            ResultSet set = pc.con.getMetaData().getTables(null, null, "users", null);

            if (!set.next()) {

                System.out.println("Tabela users não encontrada. A criar...");
                String query = "create table users(id serial unique, username varchar unique not null primary key, password varchar);";

                state.executeUpdate(query);

                System.out.println("Tabela criada");

            } else {

                System.out.println("Tabela users existe.");
            }

            set = pc.con.getMetaData().getTables(null, null, "user_roles", null);

            if (!set.next()) {

                System.out.println("Tabela user_roles não encontrada. A criar...");
                String query = "create table user_roles(id serial unique, user_id integer "
                        + ", username varchar references users (username) on delete cascade, role varchar,"
                        + "primary key (username, role));";

                state.executeUpdate(query);

                System.out.println("Tabela criada");

            } else {

                System.out.println("Tabela user_roles existe.");
            }

            set = pc.con.getMetaData().getTables(null, null, "polen", null);

            if (!set.next()) {

                System.out.println("Tabela polen não encontrada. A criar...");
                String query = "create table polen(id serial primary key, polen_type integer, long float, lat float, user_id integer "
                        + "references users (id) on delete cascade, data bigint);";

                state.executeUpdate(query);

                System.out.println("Tabela criada");

            } else {

                System.out.println("Tabela polen existe.");
            }

            set = pc.con.getMetaData().getTables(null, null, "allergies", null);

            if (!set.next()) {

                System.out.println("Tabela allergies não encontrada. A criar...");
                String query = "create table allergies("
                        + "id serial primary key, user_id integer "
                        + "references users (id) on delete cascade, type integer);";

                state.executeUpdate(query);

                System.out.println("Tabela criada");

            } else {

                System.out.println("Tabela allergies existe.");
            }

            set.close();
        }

        pc.disconnect();
    }

}
