package interfaces;

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
    boolean registerClient(IClientListener clientInterface) throws RemoteException;
    
    /**
     * Removes a client registration at the server.
     * @param clientInterface The client interface to remove
     * @param player The player
     * @return true if removed, otherwise false.
     * @throws RemoteException 
     */
    boolean removeClient(IClientListener clientInterface) throws RemoteException;

    
    /**
     * Get the opponent information object.
     * @return The Player object which contains public information about the opponent.
     * @throws RemoteException 
     */
    IPlayer getOther() throws RemoteException;

    /**
     * Let the server know at what location you attempted to fire a shot at.
     * @param x the X
     * @param y the Y
     * @param player The player shooting
     * @throws RemoteException 
     */
    void fireShot(int x, int y, IPlayer player) throws RemoteException;
    
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
     * @param player The Player
     * @return true if logged out, false if failed (should NEVER happend!)
     * @throws RemoteException Meh..
     */
    boolean logout(IPlayer player) throws RemoteException;
    
    /**
     * Response to client callback method ping().
     * @param player The player
     * @throws RemoteException 
     */
    void pong(IPlayer player) throws RemoteException;
    
    /**
     * Deploy set-up of ships to the server.
     * @param player The player
     * @throws RemoteException 
     */
    void deployShips(IPlayer player) throws RemoteException;

    
    /**
     * Requests a list of players currently available to play against.
     * @param player This is my player object.
     * @throws RemoteException 
     */
    void requestPlayers(IPlayer player) throws RemoteException;
    
    
    /**
     * Updates the player that belongs to the client listener interface.
     * @param newPlayer The new player object
     * @throws RemoteException
     */
    void updatePlayer(IPlayer newPlayer) throws RemoteException;
    
}
