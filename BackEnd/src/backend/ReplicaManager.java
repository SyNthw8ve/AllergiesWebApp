/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author nuno1
 */
public interface ReplicaManager extends java.rmi.Remote {
    
    public void is_alive() throws RemoteException;
    
    public Vector<Location> get_locations() throws RemoteException;
    
    public User auth(AuthRequest a) throws RemoteException;
    
    public void add_user(User u) throws RemoteException;
    
    public Vector<Location> get_risk(Location p, int distance, Date days, Vector<Integer> polen) throws RemoteException;
    
    public void add_location(User u, Location p) throws RemoteException;
    
    public void remove_location(User u, int id) throws RemoteException;
    
    public Vector<Location> get_user_locations(User u) throws RemoteException;
    
    public void promote() throws RemoteException;
    
    public boolean is_primary() throws RemoteException;
}
