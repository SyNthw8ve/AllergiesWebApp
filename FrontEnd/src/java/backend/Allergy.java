/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno1
 */
@XmlRootElement(name = "allergy")
public class Allergy implements java.io.Serializable {
    
    @XmlElement(required = true)
    private int id;
    @XmlElement(required = true)
    private int type;
    
    public Allergy() {
        
        
    }
    
    public Allergy (int id, int type) {
        
        this.id = id;
        this.type = type;
    }
    
    public int get_id() {
        
        return this.id;
    }
    
    public int get_type() {
        
        return this.type;
    }
}
