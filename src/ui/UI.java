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

import dataobjects.Player;
import dataobjects.Ship;
import interfaces.IBattleShip;
import interfaces.IClientListener;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
 * The User-Interface for the Swing client.<br>
 * This class is build from scratch to support new data structure with basic
 * idea taken from:
 * http://www.cs.princeton.edu/academics/ugradpgm/spe/summer04/chwillia/
 *
 * It handles : - Swing events - Server callbacks - Holding the Login dialog
 * using a tight leash.
 *
 * Since the data for the client needs to be represented through awt/swing
 * objects, it will not need to send everything back and forth, but only the
 * indexes etc.
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
@SuppressWarnings("serial")
public class UI extends UnicastRemoteObject implements IClientListener {

    /**
     * The remote object
     */
    private IBattleShip game;

    public static UI getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static void setInstance(UI ui) {
        SingletonHolder.INSTANCE = ui;
    }

    private static class SingletonHolder {

        public static UI INSTANCE;
    }

    /**
     * for expansion of game system
     */
    private static String sessionID;

    /**
     * The game state of the client, this controls what the client can and can't
     * do
     */
    private static byte gamestate;

    /**
     * Current game lobby ID
     */
    private static int lobbyID;

    /* UI Stuff */
    private JDialog loginDialog;
    private JFrame mainFrame;
    private Output output;

    /* the two playing field button arrays, shown on the UI when playing.. */
    private static JButton[][] ownButtons = new JButton[10][10];
    private static JButton[][] oppButtons = new JButton[10][10];

    /**
     * The two boards as panels where the buttons are located inside. 0 = local
     * player 1 = remote player
     */
    private static JPanel[] boards = new JPanel[2];

    /* options window frame */
    //private static Options options = new Options("Options");

    /* for manually inputting ships */
    private static JPanel inputpanel;

    /* board and input panel */
    private static Container b, c, d;

    /* input bar */
    private JPanel input;

    /* arrays for combo boxes */
    private static final String[] cletters = {" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final String[] cnumbers = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static final String[] ships = {"Carrier", "Cruiser", "Destroyer", "Submarine", "Patrol Boat"};
    private static final String[] direction = {"Horizontal", "Vertical"};

    /* ships */
    private final JComboBox combo_ship = new JComboBox(ships);
    private static int index_ship;

    /* directions */
    private final JComboBox combo_direction = new JComboBox(direction);
    private static int index_direction;

    /* message bar */
    private JTextField mbar = new JTextField();

    /* deploy button */
    private static final JButton deploy = new JButton("DEPLOY");

    /* the game type */
    private static JMenuItem pvp;

    /* is game ready to receive next action from human player?
     0 = in deployment mode
     1 = game in progress, this players turn
     2 = game in progress, opponents turn
     */
    private static int ready;


    /* and the local action listeners! */
    DirectListener directionListener = new DirectListener();

    /* the player whom plays from this UI */
    private static Player me;

    /* the opponent.
     Just basic information :
    the board (without ships)
    the name
     */
    private static Player other;

    /* this is just to save time! */
    private static String user;


    public UI(final IBattleShip game) throws RemoteException {
        super();
        java.awt.EventQueue.invokeLater(() -> {
            output = new Output();
            output.setVisible(true);
            Output.redirectSystemStreams(true, output);
        });

        this.game = game;

        handleState();

        me = new Player("User" + Double.toString(Math.random() * 10)); // temporary Player object as player hasnt logged in yet
        me.initShips();
        game.registerClient(this, me.getName());
        setupUI();

    }

    public UI(final IBattleShip game, final Player me) throws RemoteException {
        this(game);
        UI.me = me;
    }

    /**
     * Sets up the window and stuff.
     */
    private void setupUI() {

        mainFrame = new JFrame("Battleship");

        /* set up the buttons */
        for (int k = 0; k < ownButtons.length; k++) {
            for (int j = 0; j < ownButtons.length; j++) {
                ownButtons[j][k] = new JButton();
                ownButtons[j][k].setBackground(Color.GRAY);
                ownButtons[j][k].addActionListener(new BoardListener(game, this, j, k));

                oppButtons[j][k] = new JButton();
                oppButtons[j][k].setBackground(Color.GRAY);
            }
        }

        //setTitle("Battleship");
        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainFrame.setJMenuBar(createMenuBar());
        mainFrame.setResizable(false);

        b = mainFrame.getContentPane();
        b.add(setBoard(0), BorderLayout.CENTER);
        c = mainFrame.getContentPane();
        d = mainFrame.getContentPane();
        inputpanel = shipinput();
        d.add(inputpanel, BorderLayout.NORTH);

        //statusBar = new JPanel();
        //d.add(statusBar);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    /**
     * Creates Game menu and sub menus
     *
     * @return The menu bar which was created.
     */
    private JMenuBar createMenuBar() {
        JMenu menu = new JMenu("Game");
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        JMenuItem m = new JMenu("New Game");
        menu.add(m);

        /* submenu of new game */
        pvp = new JMenuItem("Player vs. Player");
        pvp.addActionListener(new GameListener());
        m.add(pvp);

        
        /* debug sub-menu */
        JMenu debug = new JMenu("Debug");
        m = new JMenuItem("Show all player IDs");
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        debug.add(m);
        m = new JMenuItem("GetLobbys");
        m.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        debug.add(m);

        m = new JMenuItem("Ping RMI server");
        m.addActionListener(new PingListener(game));
        debug.add(m);

        menu.add(debug);

        /* regular menu */
        m = new JMenuItem(Statics.isLoggedIn ? "Logout" : "Login");
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
        mbar.setFont(new Font("Ariel", Font.BOLD, 14));
        mbar.setEditable(false);
        //input.add(mbar);
        combo_ship.setSelectedIndex(0);
        combo_ship.addActionListener(new ShipsListener());
        TitledBorder titleBorder;//used for titles around combo boxes
        titleBorder = BorderFactory.createTitledBorder("Ships");
        combo_ship.setBorder(titleBorder);
        input.add(combo_ship);
        combo_direction.setSelectedIndex(0);
        combo_direction.addActionListener(directionListener);
        input.add(combo_direction);
        titleBorder = BorderFactory.createTitledBorder("Direction");
        combo_direction.setBorder(titleBorder);
        deploy.setEnabled(false);
        deploy.addActionListener(new DeployListener());
        deploy.setAlignmentX(JFrame.CENTER_ALIGNMENT);
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
        boards[n] = new JPanel(new GridLayout(11, 11));

        JTextField k;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (j != 0 && i != 0) {
                    if (n == 0) {
                        //ownButtons[i - 1][j - 1].addActionListener(boardListener);
                        boards[n].add(ownButtons[i - 1][j - 1]);
                    } else {
                        //oppButtons[i - 1][j - 1].addActionListener(boardListener);
                        boards[n].add(oppButtons[i - 1][j - 1]);
                    }
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
                    boards[n].add(k);
                } else if (j == 0) {
                    k = new JTextField(UI.getCletters(i));
                    k.setEditable(false);
                    k.setHorizontalAlignment((int) JFrame.CENTER_ALIGNMENT);
                    boards[n].add(k);
                }
            }
        }
        return boards[n];
    }

    public void resetGame() {
        b.removeAll();
        c.removeAll();
        d.removeAll();
        me = new Player(user);

        b.add(setBoard(0), BorderLayout.CENTER);
        inputpanel = shipinput();
        d.add(UI.inputpanel, BorderLayout.NORTH);
    }

    /**
     * configures the UI depending on the current state
     */
    private static void handleState() {
        if (gamestate == UIHelpers.OFFLINE) {
            // disable everything except relevant menus
        } else if (gamestate == UIHelpers.PLACEMENT) {
            // disable everything except menu and placement field
        } else if (gamestate == UIHelpers.ONLINE) {
            // enable all menus, disable everything else
        } else if (gamestate == UIHelpers.PLACED) {
            // enable the deployment button
        } else if (gamestate == UIHelpers.PLAYING) {
            // enable both player fields and set UI
        } else if (gamestate == UIHelpers.WAITING) {
            // user is waiting for opponent
        }

    }

    private void drawBoard(int[][] board, int fieldIndex) throws RemoteException {
        /*
    board defined as :
    0 = empty not shot
    1 = shot, no hit
    2 = shot, hit
    3 = shot, hit, armor rating
    4 = ship location
    5 = ship, sunk
         */
        if (fieldIndex == 0) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    switch (board[i][j]) {
                        case 0:
                            ownButtons[i][j].setBackground(null);
                            break;
                        case 1:
                            ownButtons[i][j].setBackground(Color.BLUE);
                            break;
                        case 2:
                            ownButtons[i][j].setBackground(Color.RED);
                            break;
                        case 3:
                            ownButtons[i][j].setBackground(Color.MAGENTA);
                            break;
                        case 4:
                            ownButtons[i][j].setBackground(Color.YELLOW);
                            break;
                        case 5:
                            ownButtons[i][j].setBackground(Color.BLACK);
                            break;
                    }
                }
            }
        } else {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    switch (board[i][j]) {
                        case 0:
                            oppButtons[i][j].setBackground(null);
                            break;
                        case 1:
                            oppButtons[i][j].setBackground(Color.BLUE);
                            break;
                        case 2:
                            oppButtons[i][j].setBackground(Color.RED);
                            break;
                        case 3:
                            oppButtons[i][j].setBackground(Color.MAGENTA);
                            break;
                        case 4:
                            //oppButtons[i][j].setBackground(Color.YELLOW);
                            break;
                        case 5:
                            oppButtons[i][j].setBackground(Color.BLACK);
                            break;
                    }
                }
            }
        }
    }

    private static boolean contains(int what, int... array) {
        for (int i : array) {
            if (what == i) {
                return true;
            }
        }
        return false;
    }

    private static void clearBoard() {
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                ownButtons[x][y].setBackground(Color.GRAY);
            }
        }
        for (int i = 0; i < me.getShips().length; i++) {
            handleShip(me.getShip(i).getLocStart().x, me.getShip(i).getLocStart().y, 0, me.getShip(i), UIHelpers.SHIP_PLACE.ADD);
        }
    }


    /**
     * Helper function to draw the ship on the buttons
     *
     * @param x The X start coordinate
     * @param y The Y start coordinate
     * @param s The ship to draw
     * @param col The colour to draw it with (null will remove)
     */
    private static void colorShip(final int x, final int y, final Ship s, final Color col) {
        if (s.getDirection() == Ship.DIRECTION.HORIZONTAL) {
            for (int i = x; i < s.getLocEnd().getX(); i++) {
                ownButtons[i][s.getLocStart().y].setBackground(col);
            }
        } else if (s.getDirection() == Ship.DIRECTION.VERTICAL) {
            for (int i = y; i < s.getLocEnd().getY(); i++) {
                ownButtons[s.getLocStart().x][i].setBackground(col);
            }
        }
    }

    /**
     * Handles ship on the board.<br>
     * It supports removal and adding the ship to the board. Furthermore it can
     * also mark a ship as sunk.
     *
     * @param s The ship to handle
     */
    private static void handleShip(final int x, final int y, final int fieldIndex, final Ship s, final UIHelpers.SHIP_PLACE place) {

        // clear the ship
        colorShip(x, y, s, Color.GRAY);

        System.out.println("Ship placement at : [" + x + ", " + y + "] lenght = " + s.getLength());

        if (place == UIHelpers.SHIP_PLACE.ADD) {
            /* if the ship is to be added */

 /* update the ship with the new coordinated */
            s.setLocStart(new Point(x, y));
            s.setLocEnd(Ship.setEnd(s.getLocStart(), s.getLength(), s.getDirection()));

            Point[] newLoc = new Point[s.getLength()];
            if (s.getDirection() == Ship.DIRECTION.HORIZONTAL) {
                for (int i = 0; i < newLoc.length; i++) {
                    newLoc[i] = new Point(x, y + i);
                }
            } else {
                for (int i = 0; i < newLoc.length; i++) {
                    newLoc[i] = new Point(x + i, y);
                }
            }
            s.setLocation(newLoc);


            /* re-draw the ship with the new coordinated */
            colorShip(x, y, s, Color.YELLOW);

            s.setIsPlaced(true);

            /* update the ship in the player */
            me.setShip(index_ship, s);

        } else if (place == UIHelpers.SHIP_PLACE.REMOVE) {
            /* if the ship is to be removed */
            s.setIsPlaced(false);
        }

    }

    /**
     * Update the username, this is called from the login dialog.
     *
     * @param name The name of the user that was entered.
     * @param pw The password of the user which was entered
     */
    public void updateUser(final String name, final String pw) {
        if (name != null && !"".equals(name)) {
            try {
                //game.removeClient(this, user, sessionID);
                if (me != null) {
                    user = name;
                    me.setName(user);
                } else {
                    user = name;
                    me = new Player(name);
                }
                LoginDialog.closeThis(loginDialog);
//                if (loginDialog != null) {
//                    loginDialog.dispose();
//                }
                game.registerClient(this, name);
                game.login(name, pw, this);
                //game.requestFreeLobbies(this);

            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    /**
     * The listener for the buttons on the board. Purpose : Ship placement
     */
    private class BoardListener implements ActionListener {

        private final int x;
        private final int y;
        private final UI ui;
        private final IBattleShip game;

        public BoardListener(final IBattleShip game, final UI ui, final int x, final int y) {
            this.x = x;
            this.y = y;
            this.ui = ui;
            this.game = game;
        }

        @Override
        public void actionPerformed(ActionEvent v) {
            if (ready == 0) {
                // we are in construction faze

                Ship s = me.getShip(index_ship);

                if (UIHelpers.isValidPos(x, y, s, me)) {
                    System.out.println("Ship placement for -> " + s);
                    if (s.isPlaced()) {
                        /* since the ship appears to be placed, just remove it if user clicked another button */
                        handleShip(s.getLocStart().x, s.getLocStart().y, 0, s, UIHelpers.SHIP_PLACE.REMOVE);
                    }
                    handleShip(x, y, 0, s, UIHelpers.SHIP_PLACE.ADD);
                    boolean ok = true;
                    for (int i = 0; i < me.getShips().length; i++) {
                        if (!me.getShip(i).isPlaced()) {
                            ok = false;
                            break;
                        }
                    }
                    deploy.setEnabled(ok);
                } else {
                    UIHelpers.messageDialog("Unable to place the selected ship at this location.", "Error");
                }

            } else {
                try {
                    /* user is fireing at the opponent!!! */
                    game.fireShot((IClientListener) ui, UI.lobbyID, UI.me.getId(), x, y);
                } catch (RemoteException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Direction combobox listener. Purpose : Alters which ship that should be
     * placed.
     */
    private class DirectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            index_direction = combo_direction.getSelectedIndex();

            System.out.println("Direction combobox used");

            Ship s = me.getShip(index_ship);

            if (s.isPlaced()) {
                if (UIHelpers.isValidPos(s.getLocStart().y, s.getLocStart().y, s, me)) {
                    s = me.getShip(index_ship);
                    if (s.isPlaced()) {
                        handleShip(s.getLocStart().x, s.getLocStart().x, 0, s, UIHelpers.SHIP_PLACE.REMOVE);
                    }

                    s.setDirection(UIHelpers.getSelectedDirection(index_direction));
                    s.setLocEnd(Ship.setEnd(s.getLocStart(), s.getLength(), s.getDirection()));

                    handleShip(s.getLocStart().x, s.getLocStart().y, 0, s, UIHelpers.SHIP_PLACE.ADD);

                    me.setShip(index_ship, s);
                } else {
                    UIHelpers.messageDialog("You can not turn the ship direction based on it's location.\nMove the ship first", "Error");
                }
            }

        }
    }

    /**
     * Exit menu item listener. Purpose : Handles the users request to exit the
     * program.
     */
    private class ExitListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (UIHelpers.confirmDialog("Are you sure you would like to exit Battleship?\nYou will loose points", "Exit?") == 0) {
                try {
                    // TODO : Add forefeit command in game object

                    UIHelpers.messageDialog(game.logout(me.getName()) ? "You have been logged out." : "Failed to log out, server could be unresponsive.", "Logged out");
                } catch (RemoteException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    if (loginDialog != null) {
                        loginDialog.dispose();
                    }
                    UI.getInstance().mainFrame.dispose();
                    System.exit(0);
                }
            }
        }
    }

    /**
     * Combobox for layout of ships listener. Purpose : Alters the direction of
     * the current selected ship.
     */
    private class ShipsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            index_ship = combo_ship.getSelectedIndex();
            System.out.println("Ship used");

            if (me.getShip(index_ship).isPlaced() && combo_direction.getSelectedIndex() != UIHelpers.getIndexDirection(me.getShip(index_ship).getDirection())) {
                System.out.println("Direction combobox changed because ship has different direction.");
                combo_direction.setSelectedIndex(UIHelpers.getIndexDirection(me.getShip(index_ship).getDirection()));
            }
        }
    }

    /**
     * Listener for New Game submenu Purpose : 1. If player is not logged in,
     * ask player to log in. 2. If player is logged in, request availble players
     * from the server. 3. If the player list is empty, auto create a new
     * session. 4. If the player list exists, display it and let the user choose
     * opponent or create new game session 5. If the player selects an opponent,
     * let the player know and get the session ID. 6. Let the server create the
     * game session and retrieve the new session ID.
     */
    private class GameListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (UIHelpers.isConnected(game)) {
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

                        // TODO : Implement actual gameflow start
                    }
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
            if (UIHelpers.isConnected(game)) {
                if (Statics.isLoggedIn) {
                    Statics.isLoggedIn = false;
                    try {
                        game.logout(me.getName());
                    } catch (RemoteException ex) {
                        Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // TODO : Move this entire bullcrap to the login dialog and implement observer.
                    loginDialog = LoginDialog.getInstance(UI.getInstance());
                    loginDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                    loginDialog.setVisible(true);
                }
            }
        }
    }

    /**
     * Listener for Deploy Button
     */
    private class DeployListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            if (UIHelpers.confirmDialog("Are you sure you would like to deploy your ships?", "Deploy Ships?") == 0) {
                try {
                    System.out.println("The player to deploy : " + me.getName());
                    gamestate = UIHelpers.WAITING;
                    handleState();
                    game.deployShips(UI.getInstance(), lobbyID, me);
                } catch (RemoteException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /* Listener for Options menu */
    public class OptionsListener implements ActionListener {

        private final WeakReference<UI> weakReference;

        public OptionsListener(final UI ui) {
            weakReference = new WeakReference<>(ui);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            UIHelpers.messageDialog("This feature has been disabled until further notice.", "Options");

//            final UI ui = weakReference.get();
//            if (ui != null) {
//                if (Options.opts == null) {
//                    options.setup(ui);
//                } else {
//                    options.setVisible(true);
//                }
//            }
        }
    }

    /* RMI callback methods */
    @Override
    public void shotFired(int x, int y, boolean hit) throws RemoteException {
        ownButtons[x][y].setBackground(hit ? Color.YELLOW : Color.CYAN);
    }

    @Override
    public void gameOver(boolean won) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append("You have ").append(won ? "won the game over " : "lost the game to ").append(other.getName());
        UIHelpers.messageDialog(sb.toString(), UIHelpers.MSG_GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void canPlay(boolean canPlay) throws RemoteException {
//        UI.boards[0].setEnabled(canPlay);
//        UI.boards[1].setEnabled(canPlay);
//        deploy.setEnabled(canPlay);
    }

    @Override
    public void showMessage(String title, String message, int modal) throws RemoteException {
        UIHelpers.messageDialog(title, message, modal);
    }

    @Override
    public void opponentQuit() throws RemoteException {
        UIHelpers.messageDialog(other.getName() + " has left the game.", "Game is terminated", JOptionPane.ERROR_MESSAGE);
        UIHelpers.messageDialog("You have gained 0 points, but don't worry as " + other.getName() + " has lost points.", "Game is terminated", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void updateOpponent(String player) throws RemoteException {
        other.setName(player);
    }

    @Override
    public void updateOpponentBoard(int[][] board) throws RemoteException {
        other.setBoard(board);
        drawBoard(board, 1);
    }

    @Override
    public void updateBoard(int[][] board) throws RemoteException {
        me.setBoard(board);
        drawBoard(board, 0);
    }

    @Override
    public void playerList(ArrayList<String> players) throws RemoteException {
        String sel = UIHelpers.inputDialog("Current players on server", "Server information", JOptionPane.INFORMATION_MESSAGE, players.toArray(new String[0]));
        if (sel != null) {
            System.out.println("Selected player : " + sel);
        }
    }

    @Override
    public Player getPlayer() throws RemoteException {
        System.out.println("Player sent to server -> " + me.getId() + " : " + me.getName());
        return me;
    }

    @Override
    public void setPlayer(Player player) throws RemoteException {
        me = player;
        System.out.println("Player update from server -> " + me.getId() + " : " + me.getName());
    }

    @Override
    public void loginstatus(boolean wasOkay) throws RemoteException {
        UI.gamestate = (wasOkay) ? UIHelpers.ONLINE : UIHelpers.OFFLINE;
        System.out.println("Login ok..");
    }

    @Override
    public void updateSessionID(String newID) throws RemoteException {
        sessionID = newID;
        System.out.println("New session ID from server : " + sessionID);
    }

    @Override
    public void setOtherPlayer(Player player) throws RemoteException {
        other = player;
        // TODO : Update player information on the UI
        System.out.println("Other player updated from server : (name) " + player.getName() + " (id) " + player.getId());
    }

    @Override
    public void setFreeLobbies(ArrayList<String> lobbies) throws RemoteException {
        if (lobbies.isEmpty()) {
            UIHelpers.messageDialog("No free lobbies found", user);
        } else {
            UIHelpers.inputDialog("Choose looby to join", "Current free lobbies", 0, lobbies.toArray(new String[0]));
        }
    }

    @Override
    public void shipSunk(int shipType, boolean yourShip) throws RemoteException {
        if (shipType == 0) {
            if (yourShip) {
                UIHelpers.messageDialog("Your " + me.getShip(shipType).getShipType() + " has been sunk by " + other.getName(), "Ship sunk!!!!");
                colorShip(me.getShip(shipType).getLocStart().x, me.getShip(shipType).getLocStart().y, me.getShip(shipType), Color.BLACK);
            } else {
                // TODO : Implement
            }
        }
    }

    @Override
    public void ping(long time) throws RemoteException {
        System.out.println("PING: Latency (ms) : " + (System.currentTimeMillis() - time));
    }

    @Override
    public void setLobbyID(int lobbyID) throws RemoteException {
        UI.lobbyID = lobbyID;
        System.out.println("Lobby id updated from server : " + lobbyID);
    }

    @Override
    public void hello() throws RemoteException {
        System.out.println("Server said hello.");
    }

    public static String getCletters(int i) {
        return cletters[i];
    }

    public static String getCnumbers(int i) {
        return cnumbers[i];
    }

    /**
     * Listener for the Ping menu option
     */
    private class PingListener implements ActionListener {

        private final IBattleShip game;

        public PingListener(final IBattleShip game) {
            this.game = game;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Pinging server..");
            try {
                game.pong(UI.getInstance(), System.currentTimeMillis());
            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
