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

import interfaces.IBattleShip;
import interfaces.IPlayer;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Simple UI helper functions
 *
 * @author rudz
 */
public final class UIHelpers {

    public static String MSG_GAME_OVER = "Game is over!";
    
    
    public static String getPlayerName() {
        String daName = inputDialog("Please enter your name.", "Enter name.");
        int dummy = 0;
        while (((daName == null) || (daName.equals(""))) && (dummy < 3)) {
            daName = inputDialog("You have to input something.", "Enter name.");
            if ((daName != null) && (!daName.equals(""))) {
                break;
            } else {
                if (++dummy == 3) {
                    messageDialog("Since you're having trouble inputting your name, I'll just call you stupid.", "");
                    daName = "Stupid";
                }
            }
        }
        return daName;
    }

    //creates a panel that tells whose board is which
    public static JPanel whoseBoard(final IPlayer[] players) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(players[0].getName() + "'s Board", SwingConstants.LEFT), BorderLayout.WEST);
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
        JOptionPane.showMessageDialog(null, text, title, messageType);
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
    
}
