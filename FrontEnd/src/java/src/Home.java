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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

/*
 *
 * @author nuno1
 */
@ManagedBean(name = "home")
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
    }

    public List<Location> get_locations() {

        return this.locations;
    }

    public String get_unique_id() {

        UUID id = UUID.randomUUID();

        return id.toString();
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

        for (int i = 0; i < this.locations.size(); i++) {

            l = this.locations.get(i);

            r += "{\"long\":" + l.get_long() + ", \"lat\":" + l.get_lat() + ", \"type\":" + l.get_type() + "}";

            if (i + 1 != this.locations.size()) {

                r += ",";
            }
        }

        r += "]";

        return r;
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
    
    @PostConstruct
    public void init()  {

        try {

            String request_id = this.get_unique_id();

            Client client = ClientBuilder.newClient();

            String uri = this.get_replica_location();

            WebTarget webTarget = client.target(uri).path("location").queryParam("request_id", request_id);

            Locations loc = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).get(Locations.class);

            this.locations = loc.getLocations();

        } catch (MalformedURLException ex) {

            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);

        } catch (RemoteException ex) {

            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
