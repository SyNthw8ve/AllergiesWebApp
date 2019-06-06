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
@ManagedBean
@RequestScoped
public class Home {

    /**
     * Creates a new instance of Home
     */
    String name = "Nuno";
    
    public Home() {
    }
    
}
