/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src;

import backend.Location;
import backend.Locations;
import backend.ReplicaManager;
import backend.User;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author nuno1
 */
@ManagedBean(name="profile")
@RequestScoped
public class Profile {

    /**
     * Creates a new instance of Profile
     */
    String username = "";
    User user;
    int polen_type = 1;
    float lng;
    float lat;
    
    List<Location> user_locations;
    
    public Profile() {
    }
    
    public String getUsername() {
        
        return this.user.get_username();
    }
    
    public int getPolen_type() {
        
        return this.polen_type;
    }
    
    public void setPolen_type(int polen) {
        
        this.polen_type = polen;
    }
    
    public void setLng(float lng) {
        
        this.lng = lng;
    }
    
    public void setLat(float lat) {
        
        this.lat = lat;
    }
    
    public float getLng() {
        
        return this.lng;
    }
    
    public float getLat() {
        
        return this.lat;
    }
    
    public List<Location> getUser_Locations() {
        
        return this.user_locations;
    }
    
    @PostConstruct
    public void init() {
        
        this.username = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();
        
        String uri = this.get_replica_location();
        
        Client client = ClientBuilder.newClient();
        
        WebTarget webTarget = client.target(uri).path("user").queryParam("username", this.username);
            
        this.user = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(User.class);
        
        WebTarget webTarget2 = client.target(uri).path("user_locations").queryParam("id", this.user.get_id());
        
        this.user_locations = webTarget2.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(Locations.class).getLocations();
        
    }
    
    private String get_replica_location() {
        
        String uri = "";
        
        try {
            
            ReplicaManager rm = (ReplicaManager) java.rmi.Naming.lookup("rmi://" + "localhost" + ":"
                    + 9000 + "/primary");
            
            uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/";
            
            
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            
        }
        
        return uri;
    }
    
    public void add_location() {
        
        System.out.println(this.lat);
        System.out.println(this.lng);
        System.out.println(this.polen_type);
        System.out.println(this.user.get_id());
        
        try {
            
            Location l = new Location(this.lng, this.lat, this.polen_type, -1);
        
            String uri = this.get_replica_location();

            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(uri).path("location").queryParam("id", this.user.get_id());

            webTarget.request(MediaType.APPLICATION_XML).put(Entity.entity(l, MediaType.APPLICATION_XML));
        }
        
        catch(Exception e) {
            
            System.out.println(e.toString());
        }
        
    }
    
    public void remove_location(int id) {
        
        try {
            
            System.out.println(id);
            
            String uri = this.get_replica_location();

            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(uri).path("location").queryParam("id", id).queryParam("user_id", this.user.get_id());

            webTarget.request(MediaType.APPLICATION_XML).delete();
        }
        
        catch(Exception e) {
            
            System.out.println(e.toString());
        }
    }
    
    public void logout() throws IOException {
        
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
    }
}
