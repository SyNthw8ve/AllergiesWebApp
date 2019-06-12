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
@XmlRootElement(name = "location")
public class Location implements java.io.Serializable {
    
    @XmlElement(required = true)
    private int type;
    @XmlElement(required = true)
    private float lng;
    @XmlElement(required = true)
    private float lat;
    @XmlElement(required = true)
    private int id;
    
    public Location() {
        
        
    }
    
    public Location(float lng, float lat, int type, int id) {
        
        this.lng = lng;
        this.lat= lat;
        this.type = type;
        this.id = id;
    }
    
    public float get_long() {
        
        return this.lng;
    }
    
    public float get_lat() {
        
        return this.lat;
    }
    
    public int get_type() {
        
        return this.type;
    }
    
    public int get_id() {
        
        return this.id;
    }
}
