/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import backend.Allergies;
import backend.Allergy;
import backend.Location;
import backend.Locations;
import backend.NewLocation;
import backend.ReplicaManager;
import backend.SubCode;
import backend.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nuno1
 */
public class Client {

    public Client() {

    }

    private String get_unique_id() {

        UUID id = UUID.randomUUID();

        return id.toString();
    }

    private String get_replica_location() throws IOException {

        String uri = "";

        try {

            InputStream in = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/WEB-INF/conf.properties");

            Properties prop = new Properties();

            prop.load(in);

            String regHost = prop.getProperty("reg.host", "localhost");
            int regPort = Integer.parseInt(prop.getProperty("reg.port", "9000"));

            ReplicaManager rm = (ReplicaManager) java.rmi.Naming.lookup("rmi://" + regHost + ":"
                    + regPort + "/primary");

            uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/";

        } catch (NotBoundException | MalformedURLException | RemoteException | FileNotFoundException ex) {

            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);

        }

        return uri;
    }

    public List<Location> get_locations() throws IOException {

        String request_id = this.get_unique_id();

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        String uri = this.get_replica_location();

        WebTarget webTarget = client.target(uri).path("location").queryParam("request_id", request_id);

        Locations loc = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(Locations.class);

        return loc.getLocations();
    }

    public Response add_user(User new_user) throws IOException {

        Response resp = Response.ok().build();

        try {

            String request_id = this.get_unique_id();

            new_user.set_request_id(request_id);

            javax.ws.rs.client.Client client = ClientBuilder.newClient();

            String uri = this.get_replica_location();

            WebTarget webTarget = client.target(uri).path("user");

            resp = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(Entity.entity(new_user, MediaType.APPLICATION_XML));

        } catch (MalformedURLException | RemoteException ex) {

            Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
        }

        return resp;
    }

    public User get_user(String username) throws IOException {

        String uri = this.get_replica_location();

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        String request_id = this.get_unique_id();

        WebTarget webTarget = client.target(uri).path("user").queryParam("request_id", request_id).queryParam("username", username);

        User u = webTarget.request(MediaType.APPLICATION_XML).get(User.class);

        return u;

    }

    public List<Location> get_user_locations(int id) throws IOException {

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        String uri = this.get_replica_location();
        String request_id = this.get_unique_id();

        WebTarget webTarget = client.target(uri).path("user_locations").queryParam("request_id", request_id).queryParam("id", id);

        return webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(Locations.class).getLocations();
    }

    public int add_location(NewLocation l) throws IOException {

        String request_id = this.get_unique_id();

        l.set_request_id(request_id);

        String uri = this.get_replica_location();

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("location");

        Response resp = webTarget.request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(Entity.entity(l, MediaType.APPLICATION_XML));

        return resp.readEntity(SubCode.class).get_code();
    }

    public void remove_location(int id, int user_id) throws IOException {

        String uri = this.get_replica_location();
        String request_id = this.get_unique_id();

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("location").queryParam("request_id", request_id).queryParam("id", id).queryParam("user_id", user_id);

        webTarget.request(MediaType.APPLICATION_XML).delete();
    }

    public List<Allergy> get_allergies(int user_id) throws IOException {

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        String uri = this.get_replica_location();
        String request_id = this.get_unique_id();

        WebTarget webTarget = client.target(uri).path("allergies").queryParam("request_id", request_id).queryParam("id", user_id);

        return webTarget.request(MediaType.APPLICATION_XML).get(Allergies.class).getAllergies();

    }

    public void add_allergy(Allergy new_allergy) throws IOException {

        String uri = this.get_replica_location();
        String request_id = this.get_unique_id();

        new_allergy.set_request_id(request_id);

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("allergies");

        webTarget.request(MediaType.APPLICATION_XML).post(Entity.entity(new_allergy, MediaType.APPLICATION_XML));

    }

    public void remove_allergy(int user_id, int type) throws IOException {

        String uri = this.get_replica_location();
        String request_id = this.get_unique_id();

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("allergies").queryParam("request_id", request_id).queryParam("user_id", user_id).queryParam("type", type);

        webTarget.request(MediaType.APPLICATION_XML).delete();

    }

    public void update_location(Location l) throws IOException {

        String uri = this.get_replica_location();
        String request_id = this.get_unique_id();

        l.set_request_id(request_id);

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("location");

        webTarget.request(MediaType.APPLICATION_XML).put(Entity.entity(l, MediaType.APPLICATION_XML));
    }

    public List<Location> get_risk_locations(int user_id, float risk_lng, float risk_lat) throws IOException {

        String uri = this.get_replica_location();
        String request_id = this.get_unique_id();

        javax.ws.rs.client.Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("risk").queryParam("request_id", request_id).queryParam("id", user_id).queryParam("lng", risk_lng).queryParam("lat", risk_lat);

        Locations risks = webTarget.request(MediaType.APPLICATION_XML).get(Locations.class);
        
        return risks.getLocations();

    }

}
