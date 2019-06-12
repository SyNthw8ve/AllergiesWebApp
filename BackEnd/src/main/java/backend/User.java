/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

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
    private List<Integer> polen;
    @XmlElement(required = true)
    private String password;
    
    public User() {}
    
    public User(String username, String password, List<Integer> polen, int id) {
        
        this.username = username;
        this.password = password;
        this.polen = polen;
        this.id = id;
    }
    
    public String get_username() {
        
        return this.username;
    }
    
    public String get_password() {
        
        return this.password;
    }
    
    public List<Integer> get_polen() {
        
        return this.polen;
    }
    
    public int get_id() {
        
        return this.id;
    }
    
    public void set_id(int id) {
        
        this.id = id;
    }
    
}
