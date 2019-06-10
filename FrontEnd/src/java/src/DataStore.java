/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package src;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author nuno1
 */
@ManagedBean(name="data")
@RequestScoped
public class DataStore {

    /**
     * Creates a new instance of DataStore
     */
    public DataStore() {
    }
    
    @PostConstruct
    public void init() {
        
        get_primary();
    }
    
    public ReplicaManager get_primary() {
        
        ReplicaManager rm = null;
        
        try {
            
            rm = (ReplicaManager) java.rmi.Naming.lookup("rmi://" + "localhost" + ":"
                    + 9000 + "/primary");
            
            System.out.println("Got it");
            
        } catch (NotBoundException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (MalformedURLException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
            
        } catch (RemoteException ex) {
            
            Logger.getLogger(DataStore.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return rm;
    }
    
}
