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
    
    @XmlElement(required = true)
    private String request_id;
    
    public Allergy() {
        
        
    }
    
    public Allergy (int id, int type, String request_id) {
        
        this.id = id;
        this.type = type;
        this.request_id = request_id;
    }
    
    public int get_id() {
        
        return this.id;
    }
    
    public int get_type() {
        
        return this.type;
    }
    
    public String get_text_type() {
        
        switch(this.type) {
            
            case 1:
                
                return "Plátanos";
                
            case 2:
                
                return "Gramídeas";
                
            case 3:
                
                return "Oliveira";
                
            case 4:
                
                return "Azinheira";
                
            default:
                
                return "";
        }
    }
    
    public String get_request_id() {
        
        return this.request_id;
    }
    
}
