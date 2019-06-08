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
public class Location {
    
    private String type;
    private int lng;
    private int lat;
    
    public Location(int lng, int lat, String type) {
        
        this.lng = lng;
        this.lat= lat;
        this.type = type;
    }
    
    public int get_long() {
        
        return this.lng;
    }
    
    public int get_lat() {
        
        return this.lat;
    }
    
    public String get_type() {
        
        return this.type;
    }
}
