package battleshiprmiclient;

import dataobjects.Player;
import interfaces.IBattleShip;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import interfaces.IClientListener;

/**
 *
 * @author rudz
 */
public class Battleship extends UnicastRemoteObject implements IClientListener {

    private static final long serialVersionUID = 1L;

    private String username;


    
    public Battleship() throws RemoteException { }

    
    
    
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
            Battleship monitor = new Battleship();
            game.fireShot(3, 5, monitor);
            game.registerClient(monitor);
        } catch (NotBoundException nbe) {
            System.out.println("No game server available");
        } catch (RemoteException re) {
            System.out.println("RMI Error - " + re);
        } catch (Exception e) {
            System.out.println("Error - " + e);
        }
    }

    @Override
    public void shotFired(int x, int y, boolean hit) throws RemoteException {
        System.out.println("Shot fired from server : [" + x + ", " + y + "]");
    }

    @Override
    public void shipSunk(int x, int y, int direction, int len) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setBoard(int[][] board) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void gameOver(boolean won) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void canPlay(boolean canPlay) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showMessage(String title, String message, int modal) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void opponentQuit() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateOpponent(Player player) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateOpponentBoard(int[][] board) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateBoard(int[][] board) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ping() throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
