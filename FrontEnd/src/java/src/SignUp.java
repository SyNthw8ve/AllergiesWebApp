/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src;

import backend.Allergy;
import backend.Locations;
import backend.ReplicaManager;
import backend.User;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@ManagedBean(name="signup")
@RequestScoped
public class SignUp {

    /**
     * Creates a new instance of SignUp
     */
    
    private String username;
    private String password;
    
    public SignUp() {
    }
    
    public String getUsername() 
    {
        return username;
    }
    public String getPassword()
    {
        return password;
    }
    public void setUsername(String username)
    {
        this.username = username;
    }
    public void setPassword(String password)
    {
        this.password = password;
    }
    
    public void add() throws IOException {
        
        try {
            
            
            ReplicaManager rm = (ReplicaManager) java.rmi.Naming.lookup("rmi://" + "localhost" + ":"
                    + 9000 + "/primary");
            
            User new_user = new User(this.username, this.password, new Vector<Allergy>(), -1);
            
            Client client = ClientBuilder.newClient();
            
            String uri = "http://" + rm.get_address() + ":" + rm.get_port() + "/allergies/replica/";

            
            WebTarget webTarget = client.target(uri).path("user");
            
            
            webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(Entity.entity(new_user, MediaType.APPLICATION_XML));
            
            FacesContext.getCurrentInstance().getExternalContext().redirect("profile.xhtml");
            
        } catch (NotBoundException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (MalformedURLException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (RemoteException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
