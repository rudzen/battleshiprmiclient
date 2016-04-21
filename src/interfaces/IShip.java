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

import dataobjects.PPoint;
import dataobjects.Upgrades;

/**
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
public interface IShip {

    /**
     * The type of possible ship types. Patrol = 2 length Destroyer & Submarine
     * = 3 length Cruiser = 4 length Carrier = 5 length
     */
    public enum TYPE {
        PATROL, DESTROYER, SUBMARINE, CRUISER, CARRIER
    }

    /**
     * The possible directions for the ship<br>
     * HORIZONTAL = right-left<br>
     * VERTICAL = up-down
     */
    public enum DIRECTION {
        HORIZONTAL, VERTICAL;
    }

    /**
     * Adds an upgrade to the ship (if bought by the player)
     *
     * @param upgradeType The upgrade type.
     */
    void addUpgrade(final Upgrades.UPGRADES upgradeType);

    DIRECTION getDirection();

    int getLength();

    /* getters & setters */
    int getLife();

    PPoint getLocEnd();

    PPoint getLocStart();

    /**
     * Get the ship name defined by it's type
     *
     * @return The string name of the ship
     */
    String getShipType();

    TYPE getType();

    Upgrades getUpgrades();

    /* helper methods */
    /**
     * Is the ship dead?
     *
     * @return true if ship if dead, otherwise false
     */
    boolean isDead();

    boolean isHasUpgrade();

    boolean isPlaced();
    
    /**
     * Overload of {@link #isHit(byte x, byte y)} to check for hit with
     * integers.
     *
     * @param x The X coordinate to check
     * @param y The Y coordinate to check
     * @return true if ship is hit, otherwise false.
     */
    boolean isHit(int x, int y);

    /**
     * Removes an upgrade from the ship (wahh, what why?)
     *
     * @param upgradeType The upgrade type.
     */
    void removeUpgrade(final Upgrades.UPGRADES upgradeType);

    void setDirection(DIRECTION direction);

    void setHasUpgrade(boolean hasUpgrade);

    void setLength(int length);

    void setLife(int life);

    void setLocEnd(PPoint locEnd);

    void setLocStart(PPoint locStart);

    void setType(TYPE type);

    void setUpgrades(Upgrades upgrades);

    void setIsPlaced(boolean isPlaced);
    
    @Override
    String toString();

}
