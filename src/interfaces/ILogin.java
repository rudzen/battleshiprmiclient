/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Login interface for RMI Client<->Server login attempt.
 * @author rudz
 */
public interface ILogin extends Remote {

    String login(String theLoginInformation) throws RemoteException;
    
    boolean logout() throws RemoteException;
    
}
