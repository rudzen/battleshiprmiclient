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
        System.out.println("Looking for battleship server");

        // Only required for dynamic class loading
        //System.setSecurityManager(new RMISecurityManager());
        try {
            // Check to see if a registry was specified
            String registry = "localhost";
            if (args.length >= 1) {
                registry = args[0];
            }
            // Registration format
            //registry_hostname :port/service
            // Note the :port field is optional
            String registration = "rmi://" + registry + "/Battleship";
            /* Lookup the service in the registry, and obtain a remote service */
            Remote remoteService = Naming.lookup(registration);
            IBattleShip game = (IBattleShip) remoteService;

            /* Create a new monitor and register it as a listener with remote game */
            UI ui = new UI(registry);


        } catch (NotBoundException nbe) {
            System.out.println("No game server available");
        } catch (RemoteException re) {
            System.out.println("RMI Error - " + re);
        } catch (Exception e) {
            System.out.println("Error - " + e);
        }
    }
}
