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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
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
import javax.ws.rs.core.Response;


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
    private List<String> allergies;
    private boolean in_use = false;
    
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
    
    public void setIn_use(boolean in_use) {
        
        this.in_use = in_use;
    }
    
    public boolean getIn_use() {
        
        return this.in_use;
    }
    
    public List<String> getAllergies() {
        
        return this.allergies;
    }
    
    public void setAllergies(List<String> allergies) {
        
        this.allergies = allergies;
    }
    
    public String get_unique_id() {
        
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
    
    public void add() throws IOException {
        
        try {
            
            this.in_use = false;
            String request_id = this.get_unique_id();
           
            LinkedList<Allergy> user_allergies = new LinkedList<>();
            
            for(String al : allergies) {
                
                user_allergies.add(new Allergy(-1, Integer.parseInt(al), ""));
            }
            
            User new_user = new User(this.username, this.password, user_allergies, -1, request_id);
            
            Client client = ClientBuilder.newClient();
            
            String uri = this.get_replica_location();
            
            WebTarget webTarget = client.target(uri).path("user");
            
            Response resp = webTarget.request(javax.ws.rs.core.MediaType.APPLICATION_XML).put(Entity.entity(new_user, MediaType.APPLICATION_XML));
            
            System.out.println(resp.getStatusInfo());
            
            if(resp.getStatus() == 406) {
                
                this.in_use = true;
                
            } else {
                
                FacesContext.getCurrentInstance().getExternalContext().redirect("/FrontEnd/profile/profile.xhtml");
            }
            
        } catch (MalformedURLException | RemoteException ex) {
            
            Logger.getLogger(SignUp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
