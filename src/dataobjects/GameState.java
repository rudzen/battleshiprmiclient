/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dataobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The game state object, this is send back and forth between the Swing client
 * and the server.
 *
 * @author rudz
 */
public class GameState implements Serializable {

    private static final long serialVersionUID = -7033076611469483025L;

    private int dimentions = 5;
    private Player[] players = new Player[2];
    private int[][] playerOneBoard = new int[dimentions][dimentions];
    private int[][] playerTwoBoard = new int[dimentions][dimentions];
    private boolean hasEnded;
    private int winner;
    private boolean loggedIn;
    private HashMap<Integer, ArrayList<Ship>> shipList = new HashMap<>();

    private void switchTurn() {

    }

    private boolean isShipSunken(Ship ship) {
        return true;
    }

    public void fire(int x, int y) {

    }

    private void damageShip(Ship ship, int x, int y) {

    }

    /* getters and setters, required to be correctly serializeable */
    public boolean isHasEnded() {
        return hasEnded;
    }

    public void setHasEnded(final boolean newValue) {
        hasEnded = newValue;
    }

    public void setShipList(final HashMap<Integer, ArrayList<Ship>> newList) {
        shipList = newList;
    }

    public HashMap<Integer, ArrayList<Ship>> getShipList() {
        return shipList;
    }

    public int getDimentions() {
        return dimentions;
    }

    public void setDimentions(int dimentions) {
        this.dimentions = dimentions;
    }

    public Player[] getPlayers() {
        return players;
    }

    public void setPlayers(Player[] players) {
        this.players = players;
    }

    public int[][] getPlayerOneBoard() {
        return playerOneBoard;
    }

    public void setPlayerOneBoard(int[][] playerOneBoard) {
        this.playerOneBoard = playerOneBoard;
    }

    public int[][] getPlayerTwoBoard() {
        return playerTwoBoard;
    }

    public void setPlayerTwoBoard(int[][] playerTwoBoard) {
        this.playerTwoBoard = playerTwoBoard;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

}
