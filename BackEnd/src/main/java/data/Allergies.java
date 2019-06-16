/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package data;

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

@XmlRootElement(name = "allergies")
public class Allergies {
    
    @XmlElement(required = true)
    protected List<Allergy> allergies;
    
    
    public Allergies () {
        
        this.allergies = new LinkedList<>();
    }
    
    public Allergies (List<Allergy> list) {
        
        this.allergies = list;
    }
    
    public List<Allergy> getAllergies() {
        
        return this.allergies;
    }
}
