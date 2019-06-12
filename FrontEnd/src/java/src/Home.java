/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src;

import backend.Location;
import backend.Locations;
import backend.ReplicaManager;
import java.awt.Event;
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

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

/*
 *
 * @author nuno1
 */
@ManagedBean(name="home")
@RequestScoped
public class Home {

    /**
     * Creates a new instance of Home
     */
    String name = "Nuno";
    
    
    int[] filter;
    public List<Location> locations;
    
    public Home() {
        
        filter = new int[4];
        //locations = new Vector<>();
    }
    
    public List<Location> get_locations() {
        
        return this.locations;
    }
    
    public void print() {
        
        for(int i : filter) {
            
            System.out.print(i);
        }
        
        System.out.println("-----------------");

    }
    
    public void changeValues(Event e) {
        
        System.out.println(e.toString());
    }
    
    public int[] getFilter() {
        
        return this.filter;
    }
    
    public void setFilter(int[] f) {
        
        
        this.filter = f;
    }
    
    public String to_Js() {
        
        String r = "[";
        Location l;
        
        for(int i = 0; i < this.locations.size(); i++) {
            
            l = this.locations.get(i);
            
            r += "{\"long\":" + l.get_long() + ", \"lat\":" + l.get_lat() + ", \"type\":" + l.get_type() + "}";
            
            if (i + 1 != this.locations.size()) {
                
                r += ",";
            }
        }
        
        
        r += "]";
        
        return r;
    }
    
    @PostConstruct
    public void init() {
        
        try {
            
            ReplicaManager rm = (ReplicaManager) java.rmi.Naming.lookup("rmi://" + "localhost" + ":"
                    + 9000 + "/primary");
            
            Client client = ClientBuilder.newClient();
            
            String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/";

            
            WebTarget webTarget = client.target(uri).path("location");
            
            
            Locations loc = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(Locations.class);
            
            this.locations = loc.getLocations();
            
            

        } catch (NotBoundException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (MalformedURLException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (RemoteException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
