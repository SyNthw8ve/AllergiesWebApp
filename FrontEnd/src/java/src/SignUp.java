/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;


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
    public SignUp() {
    }
    
}