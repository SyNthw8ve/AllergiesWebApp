/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author nuno1
 */

@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "locations")
public class Locations {
    
    @XmlElement(required = true)
    protected List<Location> locations;
    
    
    public Locations () {
        
        this.locations = new LinkedList<>();
    }
    
    public Locations (List<Location> list) {
        
        this.locations = list;
    }
    
    public List<Location> getLocations() {
        
        return this.locations;
    }
}
