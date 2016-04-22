package battleshiprmiclient;

import interfaces.IBattleShip;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import ui.UI;

/**
 *
 * @author rudz
 */
public class Battleship extends UnicastRemoteObject {

    private static final long serialVersionUID = 1L;

    private String username;

    public Battleship() throws RemoteException {
    }

    public static void main(String args[]) {

        // Only required for dynamic class loading
        //System.setSecurityManager(new RMISecurityManager());
        // Check to see if a registry was specified
        String registry = "localhost";
        if (args.length >= 1) {
            registry = args[0];
        }

        /* Create a new game window and register it as a listener with remote game */
        UI.runGame(registry);

    }
}
