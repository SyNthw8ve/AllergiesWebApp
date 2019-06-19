/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno1
 */
@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "delete")
public class DeleteLocation {
    
    @XmlElement(required = true)
    private int id;
    
    @XmlElement(required = true)
    private int user_id;
    
    public DeleteLocation() {
        
        
    }
    
    public DeleteLocation(int id, int user_id) {
        
        this.id = id;
        this.user_id = user_id;
    }
    
    public int get_id() {
        
        return this.id;
    }
    
    public int get_user_id() {
        
        return this.user_id;
    }
    
}
