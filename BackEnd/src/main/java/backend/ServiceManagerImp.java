/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import java.util.Timer;	
import java.util.TimerTask;

import java.util.Vector;

/**
 *
 * @author nuno1
 */
public class ServiceManagerImp extends UnicastRemoteObject implements ServiceManager, java.io.Serializable {

    Vector<ReplicaManager> RMs;
    ReplicaManager primary = null;
    
    int regPort;
    
    
    public ServiceManagerImp(int regPort) throws RemoteException {
        
        super();
        
        this.RMs = new Vector<>();
        this.regPort = regPort;
    }
    
    @Override
    public void add_replica(ReplicaManager rm) throws RemoteException {
        
        this.RMs.add(rm);
        
        if (primary == null) {
            
            primary = rm;
            
            rm.promote();
            
            java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(this.regPort);

            registry.rebind("primary", rm);
            
            System.out.println("bound primary");
            
        } else {
            
            System.out.println("added another replica");
            
        }
    }
    
    @Override
    public Vector<ReplicaManager> get_replicas() throws RemoteException {
        
        return this.RMs;
    }
    
    public ReplicaManager get_primary() {
        
        return this.primary;
    }
    
    public void check_primary() {
        
        try {
            
            if (this.primary != null) {
                
                this.primary.is_alive();
            }
            
        } catch (RemoteException e) {
            
            elect_primary();
        }
    }
    
    public void elect_primary() {
        
        this.RMs.remove(this.primary);
        
        for( ReplicaManager rm : this.RMs) {
            
            try {
                
               rm.is_alive();
               
               java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(this.regPort);
               
               registry.rebind("primary", rm);
               
               rm.promote();
               
               this.primary = rm;
               
               System.out.println("Elected new primary");
               break;
                
            } catch (RemoteException e) {
                
                
            }
        }
        
        
    }
    
    public static void main(String args[]) throws RemoteException {
        
        int regPort = 9000;
        
        if (args.length > 0) {
            
            regPort = Integer.parseInt(args[0]);
        }
        
        final ServiceManagerImp sm = new ServiceManagerImp(regPort);
        
        java.rmi.registry.Registry registry = java.rmi.registry.LocateRegistry.getRegistry(regPort);

        registry.rebind("service", sm);
        
        Timer timer = new Timer();
        
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                
                sm.check_primary();
            }
        };
        
        timer.schedule(task, 0, 1000);
        
        System.out.println("Bound");
    }
}


