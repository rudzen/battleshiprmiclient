/*
 * The MIT License
 *
 * Copyright 2016 rudz.
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
package ui;

import java.awt.BorderLayout;
import java.awt.Point;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import dataobjects.Player;
import dataobjects.Ship;
import interfaces.IBattleShip;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;
import utility.Statics;
import utility.XOR;

/**
 * Simple UI helper functions
 *
 * @author rudz
 */
public final class UIHelpers {

    public static final String MSG_GAME_OVER = "Game is over!";

    enum SHIP_PLACE {
        REMOVE, ADD, SUNK
    }

    public static final int OFFLINE = 0;
    public static final int ONLINE = 1;
    public static final int PLACEMENT = 2;
    public static final int PLACED = 3;
    public static final int PLAYING = 4;
    public static final int WAITING = 5;

    /**
     * Convert selected combobox direction to internat data structure.
     *
     * @param index_direction The index direction index chosen in the UI
     * @return The direction selected by the user as defined by the IShip
     * interface.
     */
    public static boolean getSelectedDirection(final int index_direction) {
        return index_direction == 0;
    }

    public static int getIndexDirection(final boolean dir) {
        return dir ? 0 : 1;
    }

    /**
     * Helper function to determine if the location is valid..
     *
     * @param x the X location clicked
     * @param y the Y location clicked
     * @param s the Ship
     * @param player The current player
     * @param horizontal The direction of placement
     * @return true if possible, otherwise false
     */
    public static boolean isValidPos(final int x, final int y, final Ship s, final Player player, final boolean horizontal) {
        if (horizontal && x + s.getLength() > 10) {
            return false;
        } else if (y + s.getLength() > 10) {
            return false;
        }
        for (int i = 0; i < player.getShips().size(); i++) {
            Ship ps = player.getShip(i);
            if (ps.isPlaced() && ps.getType() != s.getType()) {
                Point[] p = ps.getLocation();
                for (Point p1 : p) {
                    if (p1.x == x && p1.y == y) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static String getPlayerName() {
        String daName = inputDialog("Please enter your name.", "Enter name.");
        int dummy = 0;
        while ((daName == null || daName.isEmpty()) && dummy < 3) {
            daName = inputDialog("You have to input something.", "Enter name.");
            if (daName != null && !daName.isEmpty()) {
                break;
            } else if (++dummy == 3) {
                messageDialog("Since you're having trouble inputting your name, I'll just call you stupid.", "");
                daName = "Stupid";
            }
        }
        return daName;
    }

    //creates a panel that tells whose board is which
    public static JPanel whoseBoard(final int lobbyID, final Player... players) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(players[0].getName() + "'s Board", SwingConstants.LEFT), BorderLayout.WEST);
        panel.add(new JLabel("Lobby : " + Integer.toString(lobbyID), SwingConstants.CENTER), BorderLayout.CENTER);
        panel.add(new JLabel(players[1].getName() + "'s Board", SwingConstants.RIGHT), BorderLayout.EAST);
        return panel;
    }

    public static int confirmDialog(final String text, final String title) {
        return JOptionPane.showConfirmDialog(null, text, title, JOptionPane.YES_NO_OPTION);
    }

    public static void messageDialog(final String text, final String title) {
        messageDialog(text, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static void messageDialog(final String text, final String title, final int messageType) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, text, title, messageType);
        });
    }

    public static String inputDialog(final String text, final String title) {
        return JOptionPane.showInputDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static String inputDialog(final String text, final String title, final int messageType) {
        return JOptionPane.showInputDialog(null, text, title, messageType);
    }

    public static String inputDialog(final String text, final String title, final int defaultValue, final String... possibleValues) {
        return (String) JOptionPane.showInputDialog(null, text, title, JOptionPane.INFORMATION_MESSAGE, null, possibleValues, possibleValues[defaultValue]);
    }

    public static boolean isConnected(final IBattleShip game) {
        if (game == null) {
            messageDialog("Please connect to server first.\nTry restarting the application.", "Not connected to server.", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public static void saveProperties() {
        try {
            Properties props = new Properties();
            props.setProperty("lastUser", XOR.encode(Statics.lastUser, XOR.DEF_KEY));
            props.setProperty("lastPassword", XOR.encode(Statics.lastPassword, XOR.DEF_KEY));
            File f = new File(Statics.PROPERTIES);
            OutputStream out = new FileOutputStream(f);
            props.store(out, "BattleshipRMI Client properties file...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
