/*
 * The MIT License
 *
 * Copyright 2016 Rudy Alex Kohn <s133235@student.dtu.dk>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
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
