/*
  The MIT License

  Copyright 2016, 2017, 2018 Rudy Alex Kohn.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/

package interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import dataobjects.Player;

/**
 * The server's interface. Called by the client.
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
public interface IBattleShip extends Remote {

    /**
     * Registers a client at the server.
     *
     * @param clientInterface The client's interface.
     * @param playerName The player
     * @return true if okay, else false.
     * @throws RemoteException If server is unreachable
     */
    boolean registerClient(final IClientRMI clientInterface, final String playerName) throws RemoteException;

    /**
     * Removes a client registration at the server.
     *
     * @param clientInterface The client interface to remove
     * @param playerName The player
     * @param sessionID The session md5 string ID
     * @return true if removed, otherwise false.
     * @throws RemoteException If server is unreachable
     */
    boolean removeClient(IClientRMI clientInterface, String playerName, String sessionID) throws RemoteException;

    Player getOther(Player playerOne) throws RemoteException;

    /**
     * Let the server know at what location you attempted to fire a shot at.
     *
     * @param client The client interface
     * @param lobbyID The lobby ID
     * @param playerID The player shooting
     * @param x the X
     * @param y the Y
     * @throws RemoteException If server is unreachable
     */
    void fireShot(final IClientRMI client, final int lobbyID, final int playerID, final int x, final int y) throws RemoteException;

    /**
     * Login attempt
     *
     * @param user username The username
     * @param pw password The password
     * @param client The client interface that attempts to login, needed for
     * callback
     * @throws RemoteException If server is unreachable
     */
    void login(IClientRMI client, String user, String pw) throws RemoteException;

    /**
     * Logs the user out from the system
     *
     * @param player The Player
     * @return true if logged out, false if failed (should NEVER happend!)
     * @throws RemoteException If server is unreachable
     */
    boolean logout(String player) throws RemoteException;

    /**
     * Response to client callback method ping(). This is used to determine the
     * latency
     *
     * @param client The client
     * @param time The time the actual pong was initiated
     * @throws RemoteException If server is unreachable
     */
    void ping(IClientRMI client, long time) throws RemoteException;

    /**
     * Deploy set-up of ships to the server.
     *
     * @param client The client interface
     * @param player The player
     * @param lobbyID The lobby ID
     * @throws RemoteException If server is unreachable
     */
    void deployShips(final IClientRMI client, final int lobbyID, final Player player) throws RemoteException;

    /**
     * Updates the player that belongs to the client listener interface.
     *
     * @param client The client interface
     * @param player The new player object
     * @throws RemoteException If server is unreachable
     */
    void updatePlayer(IClientRMI client, Player player) throws RemoteException;

    /**
     * Sends a message to all the other RMI clients.
     *
     * @param origin The player who sent it
     * @param message The message
     * @param title The title of the message
     * @param modal The dialog modal
     * @throws RemoteException If server is unreachable
     */
    void publicMessage(String origin, String message, String title, int modal) throws RemoteException;

    /**
     * Updates a player object in a specific game session which is identified by
     * the sessionID that the
     *
     * @param seesionID The sessionID to update
     * @param playerObject The playerObject to update (will be determined by the
     * Player name!)
     * @return true if the object was updated, otherwise false
     * @throws RemoteException If server is unreachable
     */
    boolean updatePlayerObject(String seesionID, Player playerObject) throws RemoteException;

    /**
     * Will, if possible, generate a new sessionID and send it back, if this is
     * invoked by<br>
     * a client currently in a game session with another player, the other
     * player will<br>
     * automaticly get the new session ID also through clientCallBack :-)<br>
     *
     * @param currentSessionID The current sessionID that the player has which
     * can be null.<br>
     * @param playerObject The player that requests the sessionID, this object
     * is used to :<br>
     * <li> Figure out which game session the player is currently in and
     * <li> Which player is the opponent so the server can send that player the
     * new ID as well.
     * @return The new session ID (MD5 hash of the session) if successful,
     * otherwise NULL.
     * @throws RemoteException If server is unreachable
     */
    String requestSessionID(String currentSessionID, Player playerObject) throws RemoteException;

    /**
     * Send a message to the opponent.<br>
     * Requires that the opponent is playing through a interface which supports
     * the functionality.<br>
     *
     * @param title The title of the message
     * @param message The message body
     * @return true if the opponent's interface support the feature, otherwise
     * false.
     * @throws RemoteException If server is unreachable
     */
    boolean messageOpponent(String title, String message) throws RemoteException;

    /**
     * Request the other player currently on the server
     *
     * @param client The client
     * @param playerID The playerID (for filtering)
     * @throws RemoteException If server is unreachable
     */
    void requestOtherPlayers(final IClientRMI client, final int playerID) throws RemoteException;

    /**
     * Request free lobbies (lobbies with just one player) from the server.
     *
     * @param client The client requesting the free lobbies
     * @param playerID The playerID requesting the free lobbies (for filtering)
     * @throws RemoteException If server is unreachable
     */
    void requestFreeLobbies(final IClientRMI client, final int playerID) throws RemoteException;

    /**
     * Request that the server sends a new lobbyID
     *
     * @param client The client requesting lobbyID
     * @param playerID The player ID requesting the lobby ID
     * @throws RemoteException If server is unreachable
     */
    void requestLobbyID(IClientRMI client, int playerID) throws RemoteException;

    /**
     * Requests all player id's from server.
     *
     * @param client The client
     * @throws RemoteException If server is unreachable
     */
    void requestAllPlayerIDs(IClientRMI client) throws RemoteException;

    /**
     * Wait for opponent to do his/hers turn. This is auto-invoked when shot is
     * fired.
     *
     * @param client The client
     * @param lobbyID The lobby to wait for opponent to finish his/hers move.
     * @param playerID My player ID
     * @throws RemoteException If server is unreachable
     */
    void wait(IClientRMI client, int lobbyID, int playerID) throws RemoteException;

    /**
     * Attempts to join a specific lobby
     *
     * @param cliet The client
     * @param lobbyID The lobby ID to join
     * @param playerID The clients player ID
     * @throws RemoteException If server is unreachable
     */
    void joinLobby(IClientRMI cliet, int lobbyID, final int playerID) throws RemoteException;

    /**
     * Request all lobbies from the server
     *
     * @param client The client
     * @param playerID The playerID
     * @throws RemoteException If server is unreachable
     */
    void requestAllLobbies(IClientRMI client, int playerID) throws RemoteException;

    /**
     * Debug functionality to add X lobbies.
     *
     * @param client The client
     * @param amount The amount of lobbies to create
     * @throws RemoteException If server is unreachable
     */
    void debug_CreateLobbies(IClientRMI client, int amount) throws RemoteException;

    /* chat test */
    
    
    /**
     * Registers a client chat remote object on the server.
     * @param client The client remote object to register
     * @param name The name of the user
     * @throws RemoteException If server is unreachable
     */
    void registerChatClient(IChatClient client, String name) throws RemoteException;

    /**
     * Removes a client chat remote object from the server
     * @param client The client remote object to remove
     * @param name The name of the user
     * @throws RemoteException If server is unreachable
     */
    void removeChatClient(IChatClient client, String name) throws RemoteException;

    /**
     * Sends a message to the current chat room
     * @param client The client remote object whom are sending the message
     * @param name The name of the user sending the message
     * @param message The message being sent
     * @throws RemoteException If server is unreachable
     */
    void sendMessage(IChatClient client, String name, String message) throws RemoteException;

    /**
     * Updates a client in the chat. This only has effect for messages from this point on.
     * @param client The remote client object to be updated
     * @param oldUser The old username
     * @param newUser The new username
     * @throws RemoteException If server is unreachable
     */
    void updateChatClient(IChatClient client, String oldUser, String newUser) throws RemoteException;
    
}
