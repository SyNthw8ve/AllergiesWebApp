/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import data.Location;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author nuno1
 */
public interface ReplicaManager extends java.rmi.Remote {
    
    public boolean is_alive() throws RemoteException;
    
    public void promote() throws RemoteException;
    
    public boolean is_primary() throws RemoteException;
    
    public String get_address() throws RemoteException;
    
    public int get_port() throws RemoteException;
}
