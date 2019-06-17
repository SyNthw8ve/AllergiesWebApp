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
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author nuno1
 */
@ManagedBean(name = "profile")
@RequestScoped
public class Profile {

    String username = "";
    int allergy = 1;
    User user;
    int polen_type = 1;
    float lng;
    float lat;

    int up_polen_type = 1;
    float up_lng;
    float up_lat;

    float risk_lng;
    float risk_lat;

    List<Location> user_locations;
    List<Location> risk_locations;

    public Profile() {
        
        this.user_locations = new LinkedList<>();
        this.risk_locations = new LinkedList<>();
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

    public List<Location> getRisk_Locations() {

        return this.risk_locations;
    }

    public List<Allergy> getUser_Allergies() {

        return this.user.get_polen();
    }

    public int getAllergy() {

        return this.allergy;
    }

    public void setAllergy(int allergy) {

        this.allergy = allergy;
    }

    public int getUp_polen_type() {

        return this.up_polen_type;
    }

    public void setUp_polen_type(int polen) {

        this.up_polen_type = polen;
    }

    public void setUp_lng(float lng) {

        this.up_lng = lng;
    }

    public void setUp_lat(float lat) {

        this.up_lat = lat;
    }

    public float getUp_lng() {

        return this.up_lng;
    }

    public float getUp_lat() {

        return this.up_lat;
    }

    public void setRisk_lng(float lng) {

        this.risk_lng = lng;
    }

    public void setRisk_lat(float lat) {

        this.risk_lat = lat;
    }

    public float getRisk_lng() {

        return this.risk_lng;
    }

    public float getRisk_lat() {

        return this.risk_lat;
    }

    public void handleLatitudeChange(ValueChangeEvent event) {

        if (null != event.getNewValue()) {

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("lat", event.getNewValue());
        }
    }

    public void handleLongitudeChange(ValueChangeEvent event) {

        if (null != event.getNewValue()) {

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("long", event.getNewValue());
        }
    }

    public void choosePolenType(ValueChangeEvent event) {

        if (null != event) {

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("polen_type", event.getNewValue());
        }
    }
    
    public void handleUpLatitudeChange(ValueChangeEvent event) {

        if (null != event.getNewValue()) {

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("up_lat", event.getNewValue());
        }
    }

    public void handleUpLongitudeChange(ValueChangeEvent event) {

        if (null != event.getNewValue()) {

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("up_long", event.getNewValue());
        }
    }

    public void chooseUpPolenType(ValueChangeEvent event) {

        if (null != event) {

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("up_polen_type", event.getNewValue());
        }
    }
    
    public void chooseAllergyType(ValueChangeEvent event) {
        
        if (null != event) {

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("allergy", event.getNewValue());
        }
    }

    @PostConstruct
    public void init() {

        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("polen_type", 1);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("allergy", 1);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("lat", 0.0);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("long", 0.0);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().putIfAbsent("req_id", 0);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().putIfAbsent("up_id", -1);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("up_polen_type", 1);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("up_lat", 0.0);
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("up_long", 0.0);
        
        this.username = FacesContext.getCurrentInstance().getExternalContext().getUserPrincipal().getName();

        String uri = this.get_replica_location();

        Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("user").queryParam("username", this.username);

        this.user = webTarget.request(MediaType.APPLICATION_XML).get(User.class);

        this.get_user_locations();
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
    
    public int get_unique_id() {
        
        UUID id = UUID.randomUUID();
        
        return id.hashCode();
    }

    public void add_location() {

        try {

            Date date = new Date();
            
            int request_id = this.get_unique_id();

            NewLocation l = new NewLocation(this.lng, this.lat, this.polen_type, this.user.get_id(), date.getTime(), request_id);
            String uri = this.get_replica_location();

            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(uri).path("location");

            Response resp = webTarget.request(MediaType.APPLICATION_XML).accept(MediaType.APPLICATION_XML).post(Entity.entity(l, MediaType.APPLICATION_XML));
            
            int sub_code = resp.readEntity(SubCode.class).get_code();
            
            this.get_user_locations();

        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }

    public void get_user_locations() {

        Client client = ClientBuilder.newClient();

        String uri = this.get_replica_location();
        int request_id = this.get_unique_id();

        WebTarget webTarget = client.target(uri).path("user_locations").queryParam("request_id", request_id).queryParam("id", this.user.get_id());

        this.user_locations = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(Locations.class).getLocations();
    }

    public void remove_location(int id) {

        try {

            String uri = this.get_replica_location();
            int request_id = this.get_unique_id();

            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(uri).path("location").queryParam("request_id", request_id).queryParam("id", id).queryParam("user_id", this.user.get_id());

            webTarget.request(MediaType.APPLICATION_XML).delete();
            
            this.get_user_locations();
        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }
    
    public void get_allergies() {
        
        Client client = ClientBuilder.newClient();

        String uri = this.get_replica_location();
        int request_id = this.get_unique_id();

        WebTarget webTarget = client.target(uri).path("allergies").queryParam("request_id", request_id).queryParam("id", this.user.get_id());
        
        List<Allergy> up_allergies = webTarget.request(MediaType.APPLICATION_XML).get(Allergies.class).getAllergies();
        
        this.user.sert_allergies(up_allergies); 
    }

    public void update_location() {

        int id = (int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("up_id");
        
        String uri = this.get_replica_location();
        int request_id = this.get_unique_id();

        Client client = ClientBuilder.newClient();

        WebTarget webTarget = client.target(uri).path("location");

        Location l = new Location(this.up_lng, this.up_lat, this.up_polen_type, id, request_id);

        webTarget.request(MediaType.APPLICATION_XML).put(Entity.entity(l, MediaType.APPLICATION_XML));
        
        this.get_user_locations();
    }

    public void logout() throws IOException {

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/FrontEnd/index.xhtml");
    }

    public void add_allergies() {

        try {

            String uri = this.get_replica_location();
            int request_id = this.get_unique_id();

            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(uri).path("allergies");

            Allergy new_allergy = new Allergy(this.user.get_id(), this.allergy, request_id);
            
            webTarget.request(MediaType.APPLICATION_XML).post(Entity.entity(new_allergy, MediaType.APPLICATION_XML));

            this.get_allergies();
        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }

    public void remove_allergies(Allergy allergy) {

        try {

            String uri = this.get_replica_location();
            int request_id = this.get_unique_id();

            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(uri).path("allergies").queryParam("request_id", request_id).queryParam("user_id", allergy.get_id()).queryParam("type", allergy.get_type());

            webTarget.request(MediaType.APPLICATION_XML).delete();
            
            this.get_allergies();
        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }

    public void get_risk() {

        try {

            String uri = this.get_replica_location();
            int request_id = this.get_unique_id();

            Client client = ClientBuilder.newClient();

            WebTarget webTarget = client.target(uri).path("risk").queryParam("request_id", request_id).queryParam("id", this.user.get_id()).queryParam("lng", this.risk_lng).queryParam("lat", this.risk_lat);

            Locations risks = webTarget.request(MediaType.APPLICATION_XML).get(Locations.class);

            this.risk_locations = risks.getLocations();

        } catch (Exception e) {

            System.out.println(e.toString());
        }
    }
    
    public String to_Js(List<Location> locations) {
        
        String r = "[";
        Location l;
        
        for(int i = 0; i < locations.size(); i++) {
            
            l = locations.get(i);
            
            r += "{\"long\":" + l.get_long() + ", \"lat\":" + l.get_lat() + ", \"type\":" + l.get_type() + "}";
            
            if (i + 1 != locations.size()) {
                
                r += ",";
            }
        }
        
        
        r += "]";
        
        return r;
    }
    
    public void save_id(int id) {
        
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("up_id", id);
    }
    
    public String get_allergy_text(int type) {
        
        switch(type) {
            
            case 1:
                
                return "Plátano";
                
            case 2:
                
                return "Gramídeas";
                
            case 3:
                
                return "Oliveira";
                
            case 4:
                
                return "Azinheira";
                
            default:
                
                return "";
        }
       
    }
}
