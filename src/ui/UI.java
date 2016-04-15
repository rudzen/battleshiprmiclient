/*
 * The MIT License
 *
 * Copyright 2016 rudz.
 *
 * Permission is hereby granted, free of charge, to any person obtaining battleshipPlaced copy
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

import dataobjects.GameState;
import dataobjects.Player;
import dataobjects.Ship;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import utility.Statics;

/**
 *
 * @author rudz
 */
public class UI extends JFrame {

    private static final long serialVersionUID = 6911438361535703573L;
    /* the game state */
    private static GameState gameState = new GameState();

    /* options window frame */
    private static Options options = new Options("Options");

    /* for manually inputting ships */
    private static JPanel inputpanel;

    /* board and input panel */
    private static Container b, c, d;

    /* input bar */
    private JPanel input;

    /* menu items */
    private static JMenuItem m, pvp;

    /* arrays for combo boxes */
    private static final String[] cletters = {" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final String[] cnumbers = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static final String[] ships = {"Carrier", "Battleship", "Submarine", "Destroyer", "Patrol Boat"};
    private static final String[] direction = {"Horizontal", "Vertical"};

    /* ships */
    private final JComboBox cshi = new JComboBox(ships);

    /* directions */
    private final JComboBox cdir = new JComboBox(direction);

    /* message bar */
    private JTextField mbar = new JTextField();

    /* deploy button */
    private static final JButton deploy = new JButton("DEPLOY");

    /* ship counters */
    private static int carrierPlaced, battleshipPlaced, submarinePlaced, destroyerPlaced, patrolPlaced;

    /* ship hit matrix */
    private static final String[][] SHIPHIT = new String[10][10];

    /* the last selected value for game type (local / internet) */
    static String selectedValue = " ";

    /* the game type */
    private static JMenuItem gametype;

    private static int length = 5;

    /* is game ready to receive next action from human player? */
    static int ready;

    /* ship index tracker */
    private static int sindex;

    /* direction array tracker */
    private static int dindex;

    /* and the local action listeners! */
    BoardListener boardListener = new BoardListener();
    DirectListener directionListener = new DirectListener();

    private static Player[] players = new Player[2];

    /* this is just to save time! */
    // TODO : move to GameState
    protected static String user, user2;
    protected static boolean gameover;

    public UI() {
        super();
        user = UIHelpers.getPlayerName();

        players[0] = new Player(user, this);
        //players[Statics.enemy] = new Player("Computer");

        setTitle("Battleship");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        setResizable(false);

        b = getContentPane();
        b.add(setBoard(0), BorderLayout.CENTER);
        c = getContentPane();
        d = getContentPane();
        inputpanel = shipinput();
        d.add(inputpanel, BorderLayout.NORTH);
        pack();
        setVisible(true);
    }

    /**
     * Creates Game menu and sub menus
     *
     * @return The menu bar which was created.
     */
    public JMenuBar createMenuBar() {
        JMenu menu;
        JMenuBar menuBar = new JMenuBar();
        menu = new JMenu("Game");
        menuBar.add(menu);
        m = new JMenu("New Game");
        menu.add(m);

        /* submenu of new game */
        GameListener stuff = new GameListener();
        pvp = new JMenuItem("Player vs. Player");
        pvp.addActionListener(stuff);
        m.add(pvp);

        /* regular menu */
        m = new JMenuItem(gameState.isLoggedIn() ? "Logout" : "Login");
        m.addActionListener(new LoginListener());
        menu.add(m);
        m = new JMenuItem("Options");
        m.addActionListener(new OptionsListener(this));
        menu.add(m);
        m = new JMenuItem("Exit");
        m.addActionListener(new ExitListener());
        menu.add(m);
        return menuBar;
    }

    /**
     * Creates panels that is used to place ships
     *
     * @return The panel which to place ships in
     */
    public JPanel shipinput() {
        input = new JPanel();
        mbar.setText("Select a ship, its front position and direction.");
        mbar.setFont(new Font("Courier New", Font.BOLD, 14));
        mbar.setEditable(false);
        //input.add(mbar);
        cshi.setSelectedIndex(0);
        cshi.addActionListener(new ShipsListener());
        TitledBorder titleBorder;//used for titles around combo boxes
        titleBorder = BorderFactory.createTitledBorder("Ships");
        cshi.setBorder(titleBorder);
        input.add(cshi);
        cdir.setSelectedIndex(0);
        cdir.addActionListener(directionListener);
        input.add(cdir);
        titleBorder = BorderFactory.createTitledBorder("Direction");
        cdir.setBorder(titleBorder);
        deploy.setEnabled(false);
        deploy.addActionListener(new DeployListener());
        input.add(deploy);
        return input;
    }

    /**
     * Creates board for manual ship placement
     *
     * @param n The player to place the ship.
     * @return The JPanel which contains the ship placement.
     */
    public JPanel setBoard(int n) {
        players[n].setMyBoard(new JPanel(new GridLayout(11, 11)));//panel to store board		
        JTextField k;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (j != 0 && i != 0) {
                    players[n].getBboard(i - 1, j - 1).addActionListener(boardListener);
                    players[n].getMyBoard().add(players[n].getBboard(i - 1, j - 1));
                }
                if (i == 0) {
                    if (j != 0) {
                        //used to display row of numbers
                        k = new JTextField(UI.getCnumbers(j));
                        k.setEditable(false);
                        k.setHorizontalAlignment((int) JFrame.CENTER_ALIGNMENT);
                    } else {
                        //used to display column of numbers
                        k = new JTextField();
                        k.setEditable(false);
                    }
                    players[n].getMyBoard().add(k);
                } else if (j == 0) {
                    k = new JTextField(UI.getCletters(i));
                    k.setEditable(false);
                    k.setHorizontalAlignment((int) JFrame.CENTER_ALIGNMENT);
                    players[n].getMyBoard().add(k);
                }
            }
        }
        return players[n].getMyBoard();
    }

    public void resetGame() {
        b.removeAll();
        c.removeAll();
        d.removeAll();
        players[0] = new Player(user, this);

        b.add(setBoard(0), BorderLayout.CENTER);
        inputpanel = shipinput();
        d.add(UI.inputpanel, BorderLayout.NORTH);
    }

    /**
     * Determines whether or not is shipLayout is set to automatic
     *
     * @return
     */
    public static boolean isAutoSet() {
        return Options.SHIP_LAYOUT.getSelectedIndex() != 0;
    }

    /**
     * Asks if two players are playing on the same computer or over the web
     *
     * @return
     */
    public static boolean isLocal() {
        return gametype == pvp && "Local".equals(selectedValue);
    }

    /**
     * Returns ship colour, as selected by the user
     *
     * @return
     */
    public static Color getColor() {
        return Options.COLOURS[Options.SHIP_COLOUR.getSelectedIndex()];
    }

    /**
     * Variable that determines whether or not the carrier has been placed
     *
     * @return
     */
    public static int getCarrierPlaced() {
        return carrierPlaced;
    }

    /**
     * Variable that determines whether or not the battleship has been placed
     *
     * @return
     */
    public static int getBattleshipPlaced() {
        return battleshipPlaced;
    }

    /**
     * Variable that determines whether or not the submarine has been placed
     *
     * @return
     */
    public static int getSubmarinePlaced() {
        return submarinePlaced;
    }

    /**
     * Variable that determines whether or not the destroyer has been placed
     *
     * @return
     */
    public static int getDestroyerPlaced() {
        return destroyerPlaced;
    }

    /**
     * Variable that determines whether or not the patrol boat has been placed
     *
     * @return
     */
    public static int getPatrolPlaced() {
        return patrolPlaced;
    }

    public static boolean getGameOver() {
        return gameover;
    }

    public static void setGameOver(boolean b) {
        gameover = b;
    }

    public static int getReady() {
        return ready;
    }

//    public static void setData(JLabel x) {
//        data = x;
//    }
//
//    public static JLabel getData() {
//        return data;
//    }
    public static void setDeploy(boolean k) {
        deploy.setEnabled(k);
    }

    public static Player getPlayer(int x) {
        return players[x];
    }

    public static Player[] getPlayers() {
        return players;
    }

    public static void setPlayer(final Player[] newPlayers) {
        players = newPlayers;
    }

    public static String getDirection(int i) {
        return direction[i];
    }

    public static String getCletters(int i) {
        return cletters[i];
    }

    public static String getShips(int i) {
        return ships[i];
    }

    public static String getCnumbers(int i) {
        return cnumbers[i];
    }

    public static int getSIndex() {
        return sindex;
    }

    public static int getDIndex() {
        return dindex;
    }

    /**
     * The listener for the buttons on the board.
     */
    private class BoardListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            if (ready == 0) {
                if (players[0].getBoats(sindex) != null) {
                    players[0].getBoats(sindex).clearship(players[0]);
                }
                Object source = v.getSource();
                outer:
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (source == players[0].getBboard(i, j)) {
                            switch (sindex) {
                                case 0:
                                    if (carrierPlaced == 0) {
                                        carrierPlaced++;
                                    }
                                    break;
                                case 1:
                                    if (battleshipPlaced == 0) {
                                        battleshipPlaced++;
                                    }
                                    break;
                                case 2:
                                    if (submarinePlaced == 0) {
                                        submarinePlaced++;
                                    }
                                    break;
                                case 3:
                                    if (destroyerPlaced == 0) {
                                        destroyerPlaced++;
                                    }
                                    break;
                                case 4:
                                    if (patrolPlaced == 0) {
                                        patrolPlaced++;
                                    }
                                    break;
                            }
                            players[0].setBoats(sindex, new Ship(ships[sindex], dindex, length, i, j));
                            break outer;
                        }
                    }
                }
                players[0].getBoats(sindex).placeship();
            }
        }
    }

    /**
     * Direction combobox listener.
     */
    private class DirectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            dindex = cdir.getSelectedIndex();
            if (players[0].getBoats(sindex) != null) {
                Ship boat = new Ship(ships[sindex], dindex, players[0].getBoats(sindex).getLength(), players[0].getBoats(sindex).getX(), players[0].getBoats(sindex).getY());
                players[0].getBoats(sindex).clearship(players[0]);
                players[0].setBoats(sindex, boat);
                players[0].getBoats(sindex).placeship();
            }
        }
    }

    /**
     * Exit menu item listener.
     */
    private class ExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (UIHelpers.confirmDialog("Are you sure you would like to exit Battleship?", "Exit?") == 0) {
                // TODO : Add sending defeat to server + log out.
                System.exit(0);
            }
        }
    }

    /**
     * Combobox for layout of ships listener.
     */
    private class ShipsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            sindex = cshi.getSelectedIndex();
            if (players[0].getBoats(sindex) != null) {
                cdir.setSelectedIndex(players[0].getBoats(sindex).getDirect());
            }
            switch (sindex) {
                case 0:
                    length = 5;
                    break;
                case 1:
                    length = 4;
                    break;
                case 2:
                    length = 3;
                    break;
                case 3:
                    length = 3;
                    break;
                case 4:
                    length = 2;
                    break;
            }
            if (players[0].getBoats(sindex) != null) {
                Ship boat = new Ship(ships[sindex], players[0].getBoats(sindex).getDirect(), length, players[0].getBoats(sindex).getX(), players[0].getBoats(sindex).getY());
                players[0].getBoats(sindex).clearship(players[0]);
                players[0].setBoats(sindex, boat);
                players[0].getBoats(sindex).placeship();
            }
        }
    }

    //listener for New Game submenu
    private class GameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!Statics.isLoggedIn) {
                UIHelpers.messageDialog("You are not logged in.", "Log in required to play.");
            } else if (Statics.isLoggedIn && Statics.gameInProgress) {
                int q = UIHelpers.confirmDialog("Are you sure you would like to start a new game?\nYou will loose this game!", "New Game?");
                if (q == 0) {

                    //resets variables
                    b.removeAll();
                    c.removeAll();
                    d.removeAll();

                    Statics.yourTurn = false;
                    Statics.gameInProgress = false;

                    ready = 0;

                    if (players[0].getTimer() != null) {
                        if (players[0].getTimer().isRunning()) {
                            players[0].getTimer().stop();
                        }
                    }

                    gametype = (JMenuItem) e.getSource();

                    if (gametype == pvp) {

                        // TODO : Get playerlist from server *OR* Input name of opponent!
                        // TODO : Make better abstraction!!!!!
                        players[0] = new Player(user, this);

                        // TODO : Ask server for opponent here !
                        if ("Online".equals(selectedValue)) {
                            players[1] = new Player("Unknown");
                            b.add(setBoard(0), BorderLayout.CENTER);
                            deploy.setEnabled(false);
                            d.add(inputpanel, BorderLayout.NORTH);
                        }
                        //ready=1;
                    }
                    pack();
                    repaint();
                }
            }
        }
    }

    /**
     * Listener for Login
     */
    private class LoginListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (gameState.isLoggedIn()) {
                gameState.setLoggedIn(false);
                // TODO : Add RMI interface to actually let the server know about it.
            } else {
                LoginDialog.login();
            }
        }
    }

    //Listener for Deploy Button
    private class DeployListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            if (UIHelpers.confirmDialog("Are you sure you would like to deploy your ships?", "Deploy Ships?") == 0) {
                // TODO : Send the idiotic stuff to the server, aparently someone thought that was a good idea..
                //        .. Well, have fun...
                /*
                carrierPlaced = battleshipPlaced = submarinePlaced = destroyerPlaced = patrolPlaced = 0;
                d.remove(input);
                b.add(players[0].getMyBoard(), BorderLayout.WEST);
                ready = 1;
                c.add(autoBoard(1, 0), BorderLayout.EAST);
                d.add(new JPanel(), BorderLayout.CENTER);
                if (!"Online".equals(selectedValue)) {
                    whoGoesFirst();
                }
                pack();
                repaint();
                */
            }
        }
    }

    //Listener for Options menu
    public class OptionsListener implements ActionListener {

        private final WeakReference<UI> weakReference;

        public OptionsListener(final UI ui) {
            weakReference = new WeakReference<>(ui);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            final UI ui = weakReference.get();
            if (ui != null) {
                if (Options.opts == null) {
                    options.setup(ui);
                } else {
                    options.setVisible(true);
                }
            }
        }
    }
    

}
