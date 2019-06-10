/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import java.util.Vector;

/**
 *
 * @author nuno1
 */
public class User implements java.io.Serializable {
    
    int id;
    private String username;
    private Vector<Integer> polen;
    private String password;
    
    public User(String username, String password, Vector<Integer> polen, int id) {
        
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
    
    public Vector<Integer> get_polen() {
        
        return this.polen;
    }
    
}
