package interfaces;

import dataobjects.Player;
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
     * @param player The player
     * @return true if okay, else false.
     * @throws RemoteException 
     */
    boolean registerClient(IClientListener clientInterface, Player player) throws RemoteException;
    
    /**
     * Removes a client registration at the server.
     * @param clientInterface The client interface to remove
     * @param player The player
     * @return true if removed, otherwise false.
     * @throws RemoteException 
     */
    boolean removeClient(IClientListener clientInterface, Player player) throws RemoteException;

    
    /**
     * Get the opponent information object.
     * @return The Player object which contains public information about the opponent.
     * @throws RemoteException 
     */
    Player getOther() throws RemoteException;

    /**
     * Let the server know at what location you attempted to fire a shot at.
     * @param x the X
     * @param y the Y
     * @param client Who the fuck is actually firering! :-)
     * @param player The player shooting
     * @throws RemoteException 
     */
    void fireShot(int x, int y, IClientListener client, Player player) throws RemoteException;
    
    /**
     * Login attempt
     * @param user username
     * @param pw password
     * @return true/false depending on success.
     * @throws RemoteException 
     */
    boolean login(final String user, final String pw) throws RemoteException;
    
    /**
     * Logs the user out from the system
     * @param client The client to log out
     * @param player The Player
     * @return true if logged out, false if failed (should NEVER happend!)
     * @throws RemoteException Meh..
     */
    boolean logout(IClientListener client, Player player) throws RemoteException;
    
    /**
     * Response to client callback method ping().
     * @param client The client which is responding.
     * @param player The player
     * @throws RemoteException 
     */
    void pong(IClientListener client, Player player) throws RemoteException;
    
    /**
     * Deploy set-up of ships to the server.
     * @param client The client
     * @param player The player
     * @throws RemoteException 
     */
    void deployShips(IClientListener client, Player player) throws RemoteException;

    
    
}
