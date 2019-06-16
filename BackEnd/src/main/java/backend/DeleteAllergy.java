/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno1
 */
@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "deleteall")
public class DeleteAllergy {
    
    @XmlElement(required = true)
    private int user_id;
    
    @XmlElement(required = true)
    private int type;
    
    public DeleteAllergy() {
        
        
    }
    
    public DeleteAllergy(int user_id, int type) {
        
        this.user_id = user_id;
        this.type = type;
    }
    
    public int get_type() {
        
        return this.type;
    }
    
    public int get_user_id() {
        
        return this.user_id;
    }
    
}
