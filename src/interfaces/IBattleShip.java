package interfaces;

import dataobjects.PlayerOld;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The server's interface.
 * Called by the client.
 * @author rudz
 */
public interface IBattleShip extends Remote {

    /**
     * Registers a client at the server.
     * @param clientInterface The client's interface.
     * @return true if okay, else false.
     * @throws RemoteException 
     */
    boolean registerClient(IClientListener clientInterface) throws RemoteException;
    
    /**
     * Removes a client registration at the server.
     * @param clientInterface The client interface to remove
     * @return true if removed, otherwise false.
     * @throws RemoteException 
     */
    boolean removeClient(IClientListener clientInterface) throws RemoteException;

    /**
     * Get server information about me!
     * @return The player object for the client.
     * @throws RemoteException 
     */
    PlayerOld getMe() throws RemoteException; // PlayerDTO
    
    /**
     * Get the opponent information object.
     * @return The Player object which contains public information about the opponent.
     * @throws RemoteException 
     */
    PlayerOld getOther() throws RemoteException;

    /**
     * Let the server know at what location you attempted to fire a shot at.
     * @param x the X
     * @param y the Y
     * @param client Who the fuck is actually firering! :-)
     * @throws RemoteException 
     */
    void fireShot(int x, int y, IClientListener client) throws RemoteException;
    
    /**
     * Login attempt
     * @param user username
     * @param pw password
     * @return true/false depending on success.
     * @throws RemoteException 
     */
    boolean login(final String user, final String pw) throws RemoteException;
    
    
    /**
     * Response to client callback method ping().
     * @param client The client which is responding.
     * @throws RemoteException 
     */
    void pong(IClientListener client) throws RemoteException;
    
}
