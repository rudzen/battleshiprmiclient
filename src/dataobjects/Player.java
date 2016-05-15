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
package dataobjects;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Player class for BattleShip.<br>
 *
 * @author Rudy Alex Kohn s133235@student.dtu.dk
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private String token;

    /*
    board defined as :
    0 = empty not shot
    1 = shot, no hit
    2 = shot, hit
    3 = shot, hit, armor rating
    4 = ship location
    5 = ship, sunk
     */
    private int[][] board = new int[10][10];

    /**
     * The ships which are available to the player.
     */
    private ArrayList<Ship> ships = new ArrayList<>(5);

    /**
     * Empty constructor
     */
    public Player() {
    }

    public Player(final String name) {
        this.name = name;
    }

    public Player(final Player player) {
        this(player.getName());
        id = player.getId();
        token = player.getToken();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; i++) {
                board[i][j] = player.getBoard()[i][j];
            }
        }
        ships.addAll(player.getShips());
    }

    public void initShips() {
        ships.clear();
        ships.add(new Ship(-1, -1, Ship.TYPE.AIRCRAFT_CARRIER, false));
        ships.add(new Ship(-1, -1, Ship.TYPE.BATTLESHIP, false));
        ships.add(new Ship(-1, -1, Ship.TYPE.SUBMARINE, false));
        ships.add(new Ship(-1, -1, Ship.TYPE.DESTROYER, false));
        ships.add(new Ship(-1, -1, Ship.TYPE.PATROL_BOAT, false));
        ships.stream().map((s) -> {
            s.setIsPlaced(false);
            return s;
        }).forEach((s) -> {
            Point[] p = new Point[s.getLength()];
            for (int i = 0; i < p.length; i++) {
                p[i] = new Point(-1, -1);
            }
            s.setLocation(p);
        });
    }

    public void setShip(final int index, final Ship ship) {
        ships.set(index, new Ship(ship));
    }

    public Ship getShip(final int index) {
        return ships.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(150);
        sb.append("Name : ").append(name).append("\n");
        sb.append("ID   : ").append(id).append("\n");
        sb.append("------------\n-");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                sb.append(Integer.toString(board[i][j]));
            }
            sb.append("-\n");
        }
        ships.stream().forEach((s) -> {
            sb.append(s);
        });
        return sb.toString();
    }
    
    /* getters & setters */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int[][] getBoard() {
        return board;
    }

    public void setBoard(int[][] board) {
        this.board = board;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public void setShips(ArrayList<Ship> ships) {
        this.ships = ships;
    }

}
