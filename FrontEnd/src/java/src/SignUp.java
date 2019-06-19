/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src;

import backend.Allergy;
import backend.ReplicaManager;
import backend.User;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
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
    
    private Client cl;
    
    public SignUp() {
        
        cl = new Client();
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
    
    public void handleUsernameChange(ValueChangeEvent event) {
        
        System.out.println("username changed");
        
        if (null != event.getNewValue()) {

            
            
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("username", event.getNewValue());
        }
    }
    
    public void handleAllergiesChange(ValueChangeEvent event) {
        
        if (null != event.getNewValue()) {
            
            System.out.println("aleerrgies");

            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("allergies", event.getNewValue());
        }
    }
    
    public void add() throws IOException {
        
        try {
            
            this.in_use = false;
           
            LinkedList<Allergy> user_allergies = new LinkedList<>();
            
            for(String al : allergies) {
                
                user_allergies.add(new Allergy(-1, Integer.parseInt(al), ""));
            }
            
            User new_user = new User(this.username, this.password, user_allergies, -1, "");
            
            Response resp = this.cl.add_user(new_user);
            
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
