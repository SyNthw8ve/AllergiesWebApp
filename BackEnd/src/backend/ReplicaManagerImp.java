/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author nuno1
 */
public class ReplicaManagerImp extends UnicastRemoteObject implements ReplicaManager, java.io.Serializable {

    private String address;
    private int port;
    boolean is_primary = false;
    
    ServiceManager sm;
    
    public ReplicaManagerImp(String address, int port, ServiceManager sm) throws RemoteException {
        
        super();
        
        this.address = address;
        this.port = port;
        this.sm = sm;
    }
    
    @Override
    public void is_alive() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Location> get_locations() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public User auth(AuthRequest a) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add_user(User u) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Location> get_risk(Location p, int distance, Date days, Vector<Integer> polen) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void add_location(User u, Location p) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void remove_location(User u, int id) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<Location> get_user_locations(User u) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public void promote() throws RemoteException {
        
        this.is_primary = true;
    }
    
    @Override
    public boolean is_primary() throws RemoteException{
        
        return this.is_primary;
    }
    
    public void update_user(User u) throws RemoteException {
        
        Vector<ReplicaManager> rms = this.sm.get_replicas();
        
        for(ReplicaManager rm : rms) {
            
            if(!rm.is_primary()) {
                
                
            }
        }
    }
    
    public static void main(String args[]) throws RemoteException, NotBoundException, MalformedURLException {
        
        String regHost = "localhost";
        int regPort = 9000;
        
        if (args.length == 2) {
            
            String address = args[0];
            int port = Integer.parseInt(args[1]);
            
            ServiceManager sm = (ServiceManager) java.rmi.Naming.lookup("rmi://" + regHost + ":"
                    + regPort + "/service");
            
            ReplicaManagerImp replica = new ReplicaManagerImp(args[0], port, sm);
            
            sm.add_replica(replica);
            
        }
        
        else {
            
            System.out.println("Insuficient args");
        }
        
    }
    
}
