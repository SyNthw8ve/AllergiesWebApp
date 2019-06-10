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
@XmlRootElement(name = "data")
public class DataObject {
    
    @XmlElement(required = true)
    private String host;
    
    @XmlElement(required = true)
    private String db;
    
    @XmlElement(required = true)
    private String user;
    
    @XmlElement(required = true)
    private String psw;
    
    public DataObject() {}
    
    public DataObject(String host, String db, String user, String psw) {
        
        this.host = host;
        this.db = db;
        this.user= user;
        this.psw = psw;
    }
    
    public String get_host() {
        
        return this.host;
    }
    
    public String get_db() {
        
        return this.db;
    }
    
    public String get_user() {
        
        return this.user;
    }
    
    public String get_psw() {
        
        return this.psw;
    }
}
