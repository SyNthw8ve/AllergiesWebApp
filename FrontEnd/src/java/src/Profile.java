/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import backend.Allergy;
import backend.Location;
import backend.NewLocation;
import backend.User;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

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
    
    boolean submitted = false;
    
    int submission_code = -1;
    
    private src.Client cl;

    public Profile() {
        
        this.user_locations = new LinkedList<>();
        this.risk_locations = new LinkedList<>();
        
        this.cl = new src.Client();
    }
    
    public boolean getSubmitted() {
        
        return this.submitted;
    }
    
    public void setSubmitted(boolean sub) {
        
        this.submitted = sub;
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
    
    public int getSubmission_code() {
        
        return this.submission_code;
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

        try {
            
            this.user = this.cl.get_user(this.username);

            this.get_user_locations();
            
        } catch (IOException ex) {
            
            Logger.getLogger(Profile.class.getName()).log(Level.SEVERE, null, ex);
        }

        
    }

    public void add_location() {

        this.submitted = false;
        
        try {

            Date date = new Date();
            
            NewLocation l = new NewLocation(this.lng, this.lat, this.polen_type, this.user.get_id(), date.getTime(), "");
            
            this.submission_code = this.cl.add_location(l);
            
            this.submitted = true;
            
            this.get_user_locations();

        } catch (IOException e) {

            System.out.println(e.toString());
        }
    }

    public void get_user_locations() throws IOException {

        this.user_locations = this.cl.get_user_locations(this.user.get_id());
    }

    public void remove_location(int id) {

        try {

            this.cl.remove_location(id, this.user.get_id());
            
            this.get_user_locations();
            
        } catch (IOException e) {

            System.out.println(e.toString());
        }
    }
    
    public void get_allergies() throws IOException {
        
        List<Allergy> up_allergies = this.cl.get_allergies(this.user.get_id());
        
        this.user.set_allergies(up_allergies); 
    }

    public void update_location() throws IOException {
        
        Date date = new Date();

        int id = (int) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("up_id");
        
        Location l = new Location(this.up_lng, this.up_lat, this.up_polen_type, id, "");
        
        l.set_date(date.getTime());
        
        this.cl.update_location(l);
        
        this.get_user_locations();
    }

    public void logout() throws IOException {

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        FacesContext.getCurrentInstance().getExternalContext().redirect("/FrontEnd/index.xhtml");
    }

    public void add_allergies() {

        try {

            Allergy new_allergy = new Allergy(this.user.get_id(), this.allergy, "");
            
            this.cl.add_allergy(new_allergy);

            this.get_allergies();
            
        } catch (IOException e) {

            System.out.println(e.toString());
        }
    }

    public void remove_allergies(Allergy allergy) {

        try {
            
            this.cl.remove_allergy(allergy.get_id(), allergy.get_type());
            
            this.get_allergies();
            
        } catch (IOException e) {

            System.out.println(e.toString());
        }
    }

    public void get_risk() {

        try {

            this.risk_locations = this.cl.get_risk_locations(this.user.get_id(), this.risk_lng, this.risk_lat);

        } catch (IOException e) {

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
                
                return "Plátanos";
                
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
