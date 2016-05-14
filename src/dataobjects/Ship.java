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

import dataobjects.Upgrades.UPGRADES;
import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Basic ship class, it defines what ship type it is, and what else there is to
 * know about it.
 *
 * @author rudz
 * @version 1.0
 * @since 16-04-2016
 */
public class Ship implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * The type of possible ship types.<br>
     * Patrol = 2 length<br>
     * Destroyer, Submarine = 3 length<br>
     * Cruiser = 4 length<br>
     * Carrier = 5 length
     */
    public enum TYPE {
        PATROL, DESTROYER, SUBMARINE, CRUISER, CARRIER
    }

    /**
     * The ship type
     */
    private TYPE type;

    /**
     * The ship direction
     */
    private boolean horizontal;

    /**
     * The upgrades for the ship.
     */
    private Upgrades upgrades;

    /**
     * Points for ship location locStart = start locEnd = end
     */
    private int startX;
    private int startY;
    private int endX;
    private int endY;

    /**
     * The length of the ship
     */
    private int length;

    /**
     * The life of the ship. This is modified by armor upgrade(s).
     */
    private int life;

    /**
     * If the ship has _any_ type of upgrade, this is set to true
     */
    private boolean hasUpgrade;

    /**
     * The basic hit index. 0 = not hit, 1 = hit
     */
    private int[] hits;

    /**
     * The location of the ship on the board
     */
    private Point[] location;

    /**
     * Is the ship placed on the board?
     */
    private boolean isPlaced;

    /**
     * Default constructor.
     */
    public Ship() {
    }

    /**
     * Constructor without upgrades defined.
     *
     * @param x The X start location
     * @param y The Y start location
     * @param type The type of the ship
     * @param horizontal The direction of placement
     */
    public Ship(final int x, final int y, final TYPE type, final boolean horizontal) {
        startX = x;
        startY = y;
        length = getLen(type);
        location = new Point[length];
        for (int i = 0; i < length; i++) {
            location[i] = new Point(0, 0);
        }
        if (horizontal) {
            endX = x + length;
            endY = y;
        } else {
            endX = x;
            endY = y + length;
        }
        this.type = type;
        this.horizontal = horizontal;
        life = length;
        hits = new int[length];
        upgrades = new Upgrades(); // just to make sure, java is picky with it's serialization!
    }

    /**
     * Constructor which contain upgrade(s)
     *
     * @param x The X start location
     * @param y The Y start location
     * @param type The type of the ship
     * @param horizontal The placement direction
     * @param upgrades The upgrades for the ship.
     */
    public Ship(final int x, final int y, final TYPE type, final boolean horizontal, final Upgrades upgrades) {
        this(x, y, type, horizontal);
        hasUpgrade = upgrades.hasUpgrades();
        if (hasUpgrade) {
            this.upgrades = new Upgrades(upgrades);
            if (upgrades.getArmor() > 0) {
                life += upgrades.getArmor();
            }
        }
    }

    public Ship(final Ship ship) {
        this.endX = ship.endX;
        this.endY = ship.endY;
        this.hasUpgrade = ship.hasUpgrade;
        this.hits = ship.hits;
        this.horizontal = ship.horizontal;
        this.isPlaced = ship.isPlaced;
        this.length = ship.length;
        this.life = ship.life;
        this.location = ship.location;
        this.startX = ship.startX;
        this.startY = ship.startY;
        this.upgrades = ship.upgrades;
        this.type = ship.type;
    }
    
    
    /* upgrade helper functions */
    /**
     * Adds an upgrade to the ship (if bought by the player)
     *
     * @param upgradeType The upgrade type.
     */
    public void addUpgrade(final UPGRADES upgradeType) {
        upgrades.addUpgrade(upgradeType);
    }

    public void addUpgrade(final UPGRADES upgradeType, final int amount) {
        upgrades.addUpgrade(upgradeType, amount);
    }

    /**
     * Removes an upgrade from the ship (wahh, what why?)
     *
     * @param upgradeType The upgrade type.
     */
    public void removeUpgrade(final UPGRADES upgradeType) {
        upgrades.removeUpgrade(upgradeType);
    }

    /* helper methods */
    /**
     * Is the ship dead?
     *
     * @return true if ship if dead, otherwise false
     */
    public boolean isDead() {
        return life == 0;
    }

    /**
     * Determines the length of the ship based on it's type.
     *
     * @param type The type of the ship
     * @return The length of the ship
     */
    private static int getLen(final TYPE type) {
        final int returnValue;
        if (type == TYPE.DESTROYER || type == TYPE.SUBMARINE) {
            returnValue = 3;
        } else if (type == TYPE.CARRIER) {
            returnValue = 5;
        } else if (type == TYPE.CRUISER) {
            returnValue = 4;
        } else { // patrol boat
            returnValue = 2;
        }
        return returnValue;
    }

    /**
     * Get the ship name defined by it's type
     *
     * @return The string name of the ship
     */
    public String getShipType() {
        if (type == TYPE.CARRIER) {
            return "Carrier";
        } else if (type == TYPE.DESTROYER) {
            return "Destroyer";
        } else if (type == TYPE.SUBMARINE) {
            return "Submarine";
        } else if (type == TYPE.CRUISER) {
            return "Cruiser";
        } else { // patrol boat
            return "Patrol boat";
        }
    }

    public void addLocation(int x, int y, int pos) {
        location[pos] = new Point(x, y);
    }

    public Point getLocation(int pos) {
        return location[pos];
    }

    /* getters & setters */
    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getStartX() {
        return startX;
    }

    public void setStartX(int startX) {
        this.startX = startX;
    }

    public int getStartY() {
        return startY;
    }

    public void setStartY(int startY) {
        this.startY = startY;
    }

    public int getEndX() {
        return endX;
    }

    public void setEndX(int endX) {
        this.endX = endX;
    }

    public int getEndY() {
        return endY;
    }

    public void setEndY(int endY) {
        this.endY = endY;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isHasUpgrade() {
        return hasUpgrade;
    }

    public void setHasUpgrade(boolean hasUpgrade) {
        this.hasUpgrade = hasUpgrade;
    }

    public Upgrades getUpgrades() {
        return upgrades;
    }

    public void setUpgrades(Upgrades upgrades) {
        this.upgrades = upgrades;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public void setIsPlaced(boolean isPlaced) {
        this.isPlaced = isPlaced;
    }

    public Point[] getLocation() {
        return location;
    }

    public void setLocation(Point[] location) {
        this.location = location;
    }

    public int[] getHits() {
        return hits;
    }

    public void setHits(int[] hits) {
        this.hits = hits;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    public String toString() {
        return "Ship{" + "type=" + type + ", horizontal=" + horizontal + ", upgrades=" + upgrades + ", startX=" + startX + ", startY=" + startY + ", endX=" + endX + ", endY=" + endY + ", length=" + length + ", life=" + life + ", hasUpgrade=" + hasUpgrade + ", hits=" + Arrays.toString(hits) + ", location=" + Arrays.toString(location) + ", isPlaced=" + isPlaced + '}';
    }
}
