/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src;

/**
 *
 * @author nuno1
 */
public class Location implements java.io.Serializable {
    
    private int type;
    private float lng;
    private float lat;
    
    public Location(float lng, float lat, int type) {
        
        this.lng = lng;
        this.lat= lat;
        this.type = type;
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
}
