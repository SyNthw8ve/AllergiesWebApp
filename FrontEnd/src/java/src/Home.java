/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import backend.Location;
import java.awt.Event;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

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
    String name = "";

    int[] filter;
    public List<Location> locations;
    Client cl;

    public Home() {

        filter = new int[4];
        cl = new Client();
    }

    public List<Location> get_locations() {

        return this.locations;
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
  
    @PostConstruct
    public void init()  {

        try {
            
            this.locations = this.cl.get_locations();

        } catch (MalformedURLException ex) {

            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);

        } catch (RemoteException ex) {

            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (IOException ex) {
            
            Logger.getLogger(Home.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
