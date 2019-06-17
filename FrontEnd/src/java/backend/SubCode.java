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

@XmlRootElement(name = "code")
public class SubCode {
    
    @XmlElement(required = true)
    private int code;
    
    public SubCode() {
        
        
    }
    
    public SubCode(int code) {
        
        this.code = code;
    }
    
    public int get_code() {
        
        return this.code;
    }
}
