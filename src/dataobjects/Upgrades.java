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

import java.io.Serializable;

/**
 *
 * @author Rudy Alex Kohn s133235@student.dtu.dk
 */
public class Upgrades implements Serializable {

    private static final long serialVersionUID = -6735966214424190784L;

    /**
     * The possible ship upgrades.
     */
    public enum UPGRADES {
        ARMOR, SONAR, POWER, DECOY
    }

    /**
     * The armor
     */
    private int armor;

    /**
     * The sonar
     */
    private int sonar;

    /**
     * The power
     */
    private int power;

    
    /**
     * The decoy
     */
    private int decoy;
    
    
    /**
     * Default constructor
     */
    public Upgrades() {
    }

    /**
     * Constructs upgrade with specific amounts of upgrades
     *
     * @param armor The armor
     * @param sonar The sonar
     * @param power The power
     * @param decoy The decoy
     */
    public Upgrades(final int armor, final int sonar, final int power, final int decoy) {
        this.armor = armor;
        this.sonar = sonar;
        this.power = power;
        this.decoy = decoy;
    }

    /**
     * Constructs upgrade using supplied object
     *
     * @param upgrades The object to copy from
     */
    public Upgrades(final Upgrades upgrades) {
        this(upgrades.armor, upgrades.sonar, upgrades.power, upgrades.decoy);
    }

    @Override
    public String toString() {
        return "Upgrades{" + "armor=" + armor + ", sonar=" + sonar + ", power=" + power + ", decoy=" + decoy + '}';
    }

    /**
     * Resets the upgrades to 0 (ZERO!)
     */
    public void reset() {
        armor = sonar = power = decoy = 0;
    }

    /**
     * Determine if the upgrade contains _any_ values.
     *
     * @return true if ship has at least one upgrade, otherwise false.
     */
    public boolean hasUpgrades() {
        return armor + sonar + power > 0;
    }

    /**
     * Add a single upgrade
     *
     * @param upgrade The upgrade type to add
     */
    public void addUpgrade(final UPGRADES upgrade) {
        switch (upgrade) {
            case ARMOR:
                armor++;
                break;
            case POWER:
                power++;
                break;
            case SONAR:
                sonar++;
                break;
            case DECOY:
                decoy++;
                break;
        }
    }

    /**
     * Remove an upgrade
     *
     * @param upgrade The upgrade to remove
     */
    public void removeUpgrade(final UPGRADES upgrade) {
        switch (upgrade) {
            case ARMOR:
                if (armor > 0) {
                    armor--;
                }
                break;
            case POWER:
                if (power > 0) {
                    power--;
                }
                break;
            case SONAR:
                if (sonar > 0) {
                    sonar--;
                }
                break;
            case DECOY:
                if (decoy > 0) {
                    decoy--;
                }
                break;
        }
    }

    /**
     * Adds a specified amount of upgrades of a specific type
     *
     * @param upgrade The upgrade type to add
     * @param amount The amount of upgrades to add
     */
    public void addUpgrade(final UPGRADES upgrade, final int amount) {
        if (amount > 0) {
            switch (upgrade) {
                case ARMOR:
                    armor += amount;
                    break;
                case POWER:
                    power += amount;
                    break;
                case SONAR:
                    sonar += amount;
                    break;
                case DECOY:
                    decoy += amount;
                    break;
            }
        }
    }

    public int getArmor() {
        return armor;
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getSonar() {
        return sonar;
    }

    public void setSonar(int sonar) {
        this.sonar = sonar;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }
    
    public void setDecoy(int decoy) {
        this.decoy = decoy;
    }
    
    public int getDecoy() {
        return decoy;
    }
    
}
