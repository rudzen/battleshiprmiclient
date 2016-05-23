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

import dataobjects.Player;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Server -> Client callback interface. These are the methods being called on
 * the client.
 *
 * @author Rudy Alex Kohn (s133235@student.dtu.dk)
 */
public interface IClientRMI extends Remote {

    /**
     * Informs client if it can play or not, this is called with basic lee every
     * callback. When this is called, player action will be send to server.
     *
     * @param canPlay The client will en-/disable it's controls based on this
     * @throws RemoteException If client is unavailable
     */
    void canPlay(final boolean canPlay) throws RemoteException;

    /**
     * Show a message from the server
     *
     * @param message The message to show
     * @param title The window title of the message
     * @param modal The modal (JOPtionPane constant!)
     * @throws RemoteException If client is unavailable
     */
    void showMessage(final String message, final String title, final int modal) throws RemoteException;

    /**
     * The opponent fired a shot!
     *
     * @param x The X coordinate
     * @param y The Y coordinate
     * @param hit was it a hit?
     * @throws RemoteException If client is unavailable
     */
    void shotFired(final int x, final int y, final boolean hit) throws RemoteException;

    /**
     * Ship has been sunk.
     *
     * @param shipType The type of ship sunk
     * @param yourShip This is true if the ship is belonging to the client
     * @throws RemoteException If client is unavailable
     */
    void shipSunk(final int shipType, final boolean yourShip) throws RemoteException;

    /**
     * The game is over
     *
     * @param won did you win?
     * @throws RemoteException If client is unavailable
     */
    void gameOver(final boolean won) throws RemoteException;

    /**
     * If this is called, the opponent has either quit or lost contact
     * completely to server.
     *
     * @throws RemoteException If client is unavailable
     */
    void opponentQuit() throws RemoteException;

    /**
     * The client should update the playing field. This method is usually called
     * when the opponent has deployed.
     *
     * @param board The board to update it with.
     * @throws RemoteException If client is unavailable
     */
    void updateOpponentBoard(final int[][] board) throws RemoteException;

    /**
     * The client should update it's own board. This method should RARELY (if
     * ever) be called.
     *
     * @param board The board to update it with.
     * @throws RemoteException If client is unavailable
     */
    void updateBoard(final int[][] board) throws RemoteException;

    /**
     * Just to test if the client is alive! .. if this throws an exception,
     * terminate the game.
     *
     * @param time The time the ping was called
     * @throws RemoteException If client is unavailable
     */
    void ping(long time) throws RemoteException;

    /**
     * Simple method just to check if the client is still alive.
     * @throws RemoteException If the client is offline.
     */
    void hello() throws RemoteException;

    /**
     * Receive a list of players to pick between
     *
     * @param players The list of players from which you can play against.
     * @throws RemoteException If client is unavailable
     */
    void playerList(ArrayList<String> players) throws RemoteException;

    /**
     * Will retrieve the player object from the client forefully and out of the
     * clients control.<br>
     * This is done because i can.
     *
     * @return The player object from the client.
     * @throws RemoteException If client is unavailable
     */
    Player getPlayer() throws RemoteException;

    /**
     * Sets the Player, either the client itself or the opponent, object on the client
     *
     * @param player The player object.
     * @param opponent If true, the object belongs to the opponent.
     * @throws RemoteException If client is unavailable
     */
    void setPlayer(Player player, boolean opponent) throws RemoteException;

    /**
     * Returns the status to the client about it's current login status.
     *
     * @param wasOkay true if login was ok, false if failed.
     * @throws RemoteException If client is unavailable
     */
    void loginstatus(boolean wasOkay) throws RemoteException;

    /**
     * Update the sessionID for the client.
     *
     * @param newID The new session ID
     * @throws RemoteException If client is unavailable
     */
    void updateSessionID(String newID) throws RemoteException;

    /**
     * If list contains at least one element, open a list box to let player
     * choose which to join.
     *
     * @param lobbies The list of free joinable lobbies
     * @throws RemoteException If client is unavailable
     */
    void setFreeLobbies(ArrayList<String> lobbies) throws RemoteException;

    /**
     * Set a new lobbyID @ the client.
     *
     * @param lobbyID the new lobby id retrieved from the main game server
     * @throws RemoteException If client is unavailable
     */
    void setLobbyID(int lobbyID) throws RemoteException;

    
    /**
     * Let the client know how the deployment went, and send the opponent if OK.
     * @param sucess Was the deployment ok?
     * @param ready Is the game ready to be played now?
     * @param opponent The opponent player name and ID (ID:Name), used for name information.
     * @throws RemoteException 
     */
    void deployed(boolean sucess, boolean ready, String opponent) throws RemoteException;
    
}
