/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package backend;

import java.rmi.RemoteException;
import java.util.Vector;


/**
 *
 * @author nuno1
 */
public interface ServiceManager extends java.rmi.Remote{
    
    public void add_replica(ReplicaManager rm) throws RemoteException;
    
    public Vector<ReplicaManager> get_replicas() throws RemoteException;
    
    public void try_request() throws RemoteException;
}
