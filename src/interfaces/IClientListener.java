package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Server -> Client callback interface.
 *
 * @author rudz
 */
public interface IClientListener extends Remote {

    /**
     * The opponent fired a shot!
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param hit was it a hit?
     * @throws RemoteException
     */
    void shotFired(final int x, final int y, final boolean hit) throws RemoteException;

    /**
     * Ship has been sunk.
     *
     * @param ship the ship that are sunk
     * @throws RemoteException
     */
    void shipSunk(final IShip ship) throws RemoteException;

    /**
     * The game is over
     *
     * @param won did you win?
     * @throws RemoteException
     */
    void gameOver(final boolean won) throws RemoteException;

    /**
     * Informs client if it can play or not, this is called with basic lee every
     * callback.
     * When this is called, player action will be send to server.
     * @param canPlay
     * @throws RemoteException
     */
    void canPlay(final boolean canPlay) throws RemoteException;

    /**
     * Show a message from the server
     * @param title The window title of the message
     * @param message The message to show
     * @param modal The modal (JOPtionPane constant!)
     * @throws RemoteException 
     */
    void showMessage(final String title, final String message, final int modal) throws RemoteException;

    /**
     * If this is called, the opponent has either quit or lost contact completely to server.
     * @throws RemoteException 
     */
    void opponentQuit() throws RemoteException;

    /**
     * The client should update the information about the opponent
     * @param player The opponent data object which contains the new information
     * @throws RemoteException 
     */
    void updateOpponent(final IPlayer player) throws RemoteException;

    /**
     * The client should update the playing field.
     * This method is usually called when the opponent has deployed.
     * @param board The board to update it with.
     * @throws RemoteException 
     */
    void updateOpponentBoard(final int[][] board) throws RemoteException;

    /**
     * The client should update it's own board.
     * This method should RARELY (if ever) be called.
     * @param board The board to update it with.
     * @throws RemoteException 
     */
    void updateBoard(final int[][] board) throws RemoteException;
    
    /**
     * Just to test if the client is alive! .. if this throws an exception, terminate the game.
     * @throws RemoteException 
     */
    void ping() throws RemoteException;

    /**
     * Sends a notification to the client that the user has been logged out.
     * @param status The status of the log out attempt, true if logged out, false if not.
     * @throws RemoteException 
     */
    void isLoggedOut(boolean status) throws RemoteException;
    
    /**
     * Receive a list of players to pick between
     * @param players The list of players from which you can play against.
     * @throws RemoteException 
     */
    void playerList(ArrayList<IPlayer> players) throws RemoteException;
    
    
    IPlayer getPlayer() throws RemoteException;
    
}
