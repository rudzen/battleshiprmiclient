package battleshiprmiclient;

import dataobjects.Player;
import dataobjects.Ship;
import interfaces.IBattleShip;
import interfaces.IClientListener;
import interfaces.IPlayer;
import interfaces.IShip;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import ui.UI;
import ui.UIHelpers;

/**
 *
 * @author rudz
 */
public class Battleship {

    public static void main(String args[]) {

        // Only required for dynamic class loading
        //System.setSecurityManager(new RMISecurityManager());
        // Check to see if a registry was specified
        String registry;
        if (args.length >= 1) {
            registry = args[0];
        } else {
            registry = "localhost";
        }

        try {
            new Battleship(registry);
        } catch (RemoteException ex) {
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static final long serialVersionUID = 1L;

    private String username;

    private String registry;

    //private UI ui;
    private IBattleShip game;

    public Battleship() throws RemoteException {
    }

    public Battleship(final String registry) throws RemoteException {
        this.registry = registry;

        try {

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
                System.out.println("SecurityManager created.");
            }

            // Registration format
            //registry_hostname :port/service
            // Note the :port field is optional
            String registration = "rmi://" + registry + "/Battleship";
            /* Lookup the service in the registry, and obtain a remote service */
            Remote remoteService = Naming.lookup(registration);

            //UI.runGame(registry);
            UI ui = new UI(registry);

            game = (IBattleShip) remoteService;

            System.out.println("RMI SEEMS OKAY!");

            game.login(ui.toString(), "password");

            IPlayer p = new Player("Palle");
            p.initShips();

            game.registerClient(ui);
            //registerClient(p);

            //game.fireShot(3, 5, new Player("abe"));
        } catch (final RemoteException re) {
            UIHelpers.messageDialog("RMI Error - RemoteException()\n" + re.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, re);
        } catch (final NotBoundException ex) {
            UIHelpers.messageDialog("No game server available - NotBountException()", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final MalformedURLException ex) {
            UIHelpers.messageDialog("No game server available - MalformedURLException()", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Create a new game window and register it as a listener with remote game */
        //UI.runGame(registry);
    }

}
