/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import java.util.List;
import java.util.Vector;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno1
 */
@XmlRootElement(name = "user")
public class User implements java.io.Serializable {
    
    @XmlElement(required = true)
    private int id;
    
    @XmlElement(required = true)
    private String username;
    
    @XmlElement(required = true)
    private List<Allergy> polen;
    
    @XmlElement(required = true)
    private String password;
    
    @XmlElement(required = true)
    private String request_id;
    
    public User() {}
    
    public User(String username, String password, List<Allergy> polen, int id, String request_id) {
        
        this.username = username;
        this.password = password;
        this.polen = polen;
        this.id = id;
        this.request_id = request_id;
    }
    
    public String get_username() {
        
        return this.username;
    }
    
    public String get_password() {
        
        return this.password;
    }
    
    public List<Allergy> get_polen() {
        
        return this.polen;
    }
    
    public int get_id() {
        
        return this.id;
    }
    
    public void set_id(int id) {
        
        this.id = id;
    }
    
    public void set_allergies(List<Allergy> allergies) {
        
        this.polen = allergies;
    }
    
    public String get_request_id() {
        
        return this.request_id;
    }
    
    public void set_request_id(String req_id) {
        
        this.request_id = req_id;
    }
    
}

