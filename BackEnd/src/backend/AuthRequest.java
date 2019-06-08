/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

/**
 *
 * @author nuno1
 */
public class AuthRequest {
    
    private String username;
    private String password;
    
    public AuthRequest(String username, String password) {
        
        this.username = username;
        this.password = password;
    }
    
    public String get_username() {
        
        return this.username;
    }
    
    public String get_password() {
        
        return this.password;
    }
}
