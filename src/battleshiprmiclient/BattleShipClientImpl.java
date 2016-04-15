/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package battleshiprmiclient;

import dataobjects.Player;
import interfaces.IClientListener;
import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * Callback interface from server -> client
 * @author rudz
 */
public class BattleShipClientImpl implements IClientListener, Serializable {

    private static final long serialVersionUID = 4226319365341048644L;

    BattleShipClientImpl() {
    }

    @Override
    public void shotFired(int x, int y, boolean hit) throws RemoteException {
        System.out.println("Hej");
    }

    @Override
    public void shipSunk(int x, int y, int direction, int len) throws RemoteException {
        System.out.println("Hej");
    }

    @Override
    public void gameOver(boolean won) throws RemoteException {
        System.out.println("Hej");
    }

    @Override
    public void setBoard(int[][] board) throws RemoteException {
        System.out.println("Hej");
    }

    @Override
    public void canPlay(boolean canPlay) throws RemoteException {
        System.out.println("Hej");
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

}
