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
@XmlRootElement(name = "new_location")
public class NewLocation implements java.io.Serializable {
    
    @XmlElement(required = true)
    private float lng;
    
    @XmlElement(required = true)
    private float lat;
    
    @XmlElement(required = true)
    private int type;
    
    @XmlElement(required = true)
    private int user_id;
    
    @XmlElement(required = true)
    private long date;
    
    @XmlElement(required = true)
    private String request_id;
    
    public NewLocation() {
        
        
    }
    
    public NewLocation(float lng, float lat, int type, int user_id, long date, String request_id) {
        
        this.lng = lng;
        this.lat = lat;
        this.type = type;
        this.user_id = user_id;
        this.date = date;
        this.request_id = request_id;
    }
    
    public float get_lng() {
        
        return this.lng;
    }
    
    public float get_lat() {
        
        return this.lat;
    }
    
    public int get_type() {
        
        return this.type;
    }
    
    public int get_user_id() {
        
        return this.user_id;
    }
    
    public long get_date() {
        
        return this.date;
    }
    
    public String get_request_id() {
        
        return this.request_id;
    }
    
    public void set_request_id(String req_id) {
        
        this.request_id = req_id;
    }
}  
