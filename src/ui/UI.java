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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;

import dataobjects.Player;
import dataobjects.Ship;
import interfaces.IBattleShip;
import ui.Listeners.OptionsListener;
import ui.Listeners.PingListener;
import ui.lobbylistener.LobbyLister;
import utility.Statics;

import static javax.swing.JFrame.EXIT_ON_CLOSE;
import interfaces.IClientRMI;

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
public class UI extends UnicastRemoteObject implements IClientRMI {

    /**
     * The remote object
     */
    public IBattleShip game;

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
    private static final AtomicInteger gamestate = new AtomicInteger(UIHelpers.OFFLINE);

    /**
     * Current game lobby ID
     */
    private static final AtomicInteger lobbyID = new AtomicInteger(-1);

    /* UI Stuff */
    private final LoginDialog loginDialog;
    public JFrame mainFrame;
    private Output output;
    public LobbyLister gameSelection;

    /* the text labels shown above the boards while playing. */
    private final JLabel[] myInfo = new JLabel[3];

    /* the panel holding the text labels while playing */
    private final JPanel whoseBoard = new JPanel();

    /* the two playing field button arrays, shown on the UI when playing.. */
    private static final JButton[][] ownButtons = new JButton[10][10];
    private static final JButton[][] oppButtons = new JButton[10][10];

    /**
     * The two boards as panels where the buttons are located inside. 0 = local
     * player 1 = remote player
     */
    private static final JPanel[] boards = new JPanel[2];

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
    private static final String[] ships = {"Aircraft carrier", "Battleship", "Submarine", "Destroyer", "Patrol boat"};
    private static final String[] direction = {"Horizontal", "Vertical"};

    /* ships */
    private final JComboBox<String> combo_ship = new JComboBox<>(ships);
    private static int index_ship;

    /* directions */
    private final JComboBox<String> combo_direction = new JComboBox<>(direction);
    private static int index_direction;

    /* message bar */
    private final JTextField mbar = new JTextField();

    /* deploy button */
    private static final JButton deploy = new JButton("DEPLOY");

    /* the game type */
    private static JMenuItem pvp;

    private static class DebugMenus {

        public JMenu debug;
        public JMenuItem ping;
        public JMenuItem showAllLobbies;
        public JMenuItem add1Klobbies;
    }

    private static class Menus {

        public JMenu menu;
        public JMenuBar menuBar;
        public JMenuItem showFreeLobbies;
        public JMenuItem login;
        public JMenuItem options;
        public JMenuItem exit;
    }

    private final DebugMenus debugMenus = new DebugMenus();
    private final Menus menus = new Menus();

    /* is game ready to receive next action from human player?
     0 = in deployment mode
     1 = game in progress, this players turn
     2 = game in progress, opponents turn
     */
    private static int ready;


    /* and the local action listeners! */
    final DirectListener directionListener = new DirectListener();

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

    public UI(final IBattleShip game, final int registryPort) throws RemoteException {
        super();

        //setLookAndFeel(Constants.NIMBUS_LF);
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        java.awt.EventQueue.invokeLater(() -> {
            output = new Output();
            Output.redirectSystemStreams(true, output);
            output.setVisible(true);
        });

        loginDialog = LoginDialog.getInstance();

        try {
            gameSelection = LobbyLister.getInstance();
        } catch (Exception ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        //gameSelection.setVisibility(true);

        this.game = game;

        me = new Player("User" + Double.toString(Math.random() * 10)); // temporary Player object as player hasnt logged in yet
        me.initShips();

        game.registerClient(this, me.getName());
        setupUI();

        handleState();

    }

    /**
     * Sets up the window and stuff.
     */
    private void setupUI() {

        mainFrame = new JFrame("Battleship - Not logged in.");

        /* set up the buttons */
        for (int k = 0; k < ownButtons.length; k++) {
            for (int j = 0; j < ownButtons.length; j++) {
                ownButtons[j][k] = new JButton();
                ownButtons[j][k].setBackground(Color.GRAY);
                ownButtons[j][k].addActionListener(new BoardListener(k, j));

                oppButtons[j][k] = new JButton();
                oppButtons[j][k].setBackground(Color.GRAY);
            }
        }

        mainFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        menus.menuBar = createMenuBar();
        mainFrame.setJMenuBar(menus.menuBar);
        mainFrame.setResizable(false);

        b = mainFrame.getContentPane();
        b.add(setBoard(0), BorderLayout.CENTER);
        c = mainFrame.getContentPane();
        d = mainFrame.getContentPane();
        inputpanel = shipinput();
        d.add(inputpanel, BorderLayout.NORTH);

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    /**
     * Creates Game menu and sub menus
     *
     * @return The menu bar which was created.
     */
    private JMenuBar createMenuBar() {

        menus.menu = new JMenu("Game");
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menus.menu);

        /* ********** BEGIN DEBUG MENUS *********************/
        debugMenus.debug = new JMenu("Debug");
        debugMenus.showAllLobbies = new JMenuItem("Show all lobbies");
        debugMenus.showAllLobbies.addActionListener((ActionEvent e) -> {
            try {
                game.requestAllPlayerIDs(UI.getInstance());
            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        debugMenus.ping = new JMenuItem("Get RMI Server Latency");
        debugMenus.ping.addActionListener(new PingListener());

        debugMenus.add1Klobbies = new JMenuItem("Create 1000 random lobbies");
        debugMenus.add1Klobbies.addActionListener((ActionEvent e) -> {
            try {
                if (UIHelpers.confirmDialog("Sure to add 1k lobbies? This might cause issues :)", "DEBUG: Add 1k lobbies") == 0) {
                    game.debug_CreateLobbies(UI.getInstance(), 1000);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        debugMenus.debug.add(debugMenus.ping);
        debugMenus.debug.add(debugMenus.showAllLobbies);
        debugMenus.debug.add(debugMenus.add1Klobbies);
        menus.menu.add(debugMenus.debug);
        /* ********** END OF DEBUG MENUS  *******************/

 /* ********** BEGIN REGULAR MENUS *******************/
        menus.showFreeLobbies = new JMenuItem("Get Free Lobbies");
        menus.showFreeLobbies.addActionListener((ActionEvent e) -> {
            try {
                game.requestFreeLobbies(UI.getInstance(), me.getId());
            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        menus.login = new JMenuItem("Login");
        menus.login.addActionListener(new LoginListener());

        menus.options = new JMenuItem("Options");
        menus.options.addActionListener(new OptionsListener());

        menus.exit = new JMenuItem("Exit");
        menus.exit.addActionListener(new ExitListener());

        if (debugMenus.debug != null) {
            menus.menu.addSeparator();
        }
        menus.menu.add(menus.login);
        menus.menu.add(menus.showFreeLobbies);
        menus.menu.add(menus.options);
        menus.menu.addSeparator();
        menus.menu.add(menus.exit);

        return menuBar;
    }

    /**
     * Creates panels that is used to place ships
     *
     * @return The panel which to place ships in
     */
    public JPanel shipinput() {
        input = new JPanel();
        combo_ship.setSelectedIndex(0);
        combo_ship.addActionListener(new ShipsListener());
        TitledBorder titleBorder;//used for titles around combo boxes
        titleBorder = BorderFactory.createTitledBorder("Ships");
        combo_ship.setBorder(titleBorder);
        combo_ship.setToolTipText("Select ship to place.");
        input.add(combo_ship);
        combo_direction.setSelectedIndex(0);
        combo_direction.setToolTipText("Select the direction of the ship.");
        combo_direction.addActionListener(directionListener);
        input.add(combo_direction);
        titleBorder = BorderFactory.createTitledBorder("Direction");
        combo_direction.setBorder(titleBorder);
        deploy.setEnabled(false);
        deploy.addActionListener(new DeployListener());
        deploy.setAlignmentX(JFrame.CENTER_ALIGNMENT);
        input.add(deploy);

//        mbar.setText("Select a ship, its front position and direction.");
//        mbar.setFont(new Font("Ariel", Font.BOLD, 14));
//        mbar.setEditable(false);
//        input.add(mbar, JFrame.TOP_ALIGNMENT);
        return input;
    }

    /**
     * Creates board for manual ship placement
     *
     * @param n The player to place the ship.
     * @return The JPanel which contains the ship placement.
     */
    public static JPanel setBoard(int n) {
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
        gamestate.set(UIHelpers.ONLINE);
    }

    /**
     * configures the UI depending on the current state
     */
    private void handleState() {
        menus.showFreeLobbies.setEnabled(gamestate.get() != UIHelpers.OFFLINE);
        menus.login.setText(gamestate.get() != UIHelpers.OFFLINE ? "Logout" : "Login");

        if (gamestate.get() == UIHelpers.OFFLINE) {
            deploy.setEnabled(false);
        } else if (gamestate.get() == UIHelpers.ONLINE) {
            deploy.setEnabled(false);
        } else if (gamestate.get() == UIHelpers.PLACEMENT) {
            deploy.setEnabled(false);
        } else if (gamestate.get() == UIHelpers.PLACED) {
            deploy.setEnabled(true);
        } else if (gamestate.get() == UIHelpers.PLAYING) {
            setButtonsEnabled(ownButtons, true);
        } else if (gamestate.get() == UIHelpers.WAITING) {
            setButtonsEnabled(ownButtons, false);
        }
    }

    private static void setButtonsEnabled(final JButton[][] buttons, final boolean value) {
        for (int i = 0; i < 10; i++) {
            for (final JButton j : buttons[i]) {
                j.setEnabled(value);
            }
        }
    }

    private static void drawBoard(int[][] board, int fieldIndex) throws RemoteException {
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

    /**
     * Helper function to draw the ship on the buttons
     *
     * @param x The X start coordinate
     * @param y The Y start coordinate
     * @param s The ship to draw
     * @param col The colour to draw it with (null will remove)
     */
    private static void colorShip(final int x, final int y, final Ship s, final Color col, final int fieldIndex) {
        System.out.println("Ship colouring @ : " + x + ", " + y + " @ " + fieldIndex);
        Point[] p = s.getLocation();
        if (p[0].x > -1) {
            if (fieldIndex == 0) {
                for (Point p1 : p) {
                    ownButtons[p1.y][p1.x].setBackground(col);
                }
            } else {
                for (Point p1 : p) {
                    oppButtons[p1.y][p1.x].setBackground(col);
                }
            }
        }
    }

    /**
     * Colours a single block (button) on the playing field
     *
     * @param x The X
     * @param y The Y
     * @param col The colour
     * @param left true = left are, false = right area.
     */
    private static void colorBlock(final int x, final int y, final Color col, final boolean left) {
        if (left) {
            ownButtons[y][x].setBackground(col);
        } else {
            oppButtons[y][x].setBackground(col);
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
        colorShip(x, y, s, Color.GRAY, fieldIndex);

        System.out.println("Ship placement at : [" + x + ", " + y + "] lenght = " + s.getLength());

        /* if the ship is to be added */
        if (place == UIHelpers.SHIP_PLACE.ADD) {

            /* update the ship with the new coordinates */
            s.setHorizontal(UIHelpers.getSelectedDirection(index_direction));
            s.setStartX(x);
            s.setStartY(y);
            if (s.isHorizontal()) {
                s.setEndX(x + s.getLength());
                s.setEndY(y);
            } else {
                s.setEndX(x);
                s.setEndY(y + s.getLength());
            }

            Point[] newLoc = new Point[s.getLength()];
            if (s.isHorizontal()) {
                for (int i = 0; i < newLoc.length; i++) {
                    newLoc[i] = new Point(x + i, y);
                }
            } else {
                for (int i = 0; i < newLoc.length; i++) {
                    newLoc[i] = new Point(x, y + i);
                }
            }
            s.setLocation(newLoc);

            /* re-draw the ship with the new coordinated */
            colorShip(x, y, s, Color.YELLOW, 0);

            s.setIsPlaced(true);

            /* update the ship in the player */
            me.setShip(index_ship, s);

        } else if (place == UIHelpers.SHIP_PLACE.REMOVE) {
            /* if the ship is to be removed */
            //s.setIsPlaced(false);
        }

    }

    /**
     * Update the username, this is called from the login dialog.
     *
     * @param name The name of the user that was entered.
     * @param pw The password of the user which was entered
     */
    public void updateUser(final String name, final String pw) {
        if (name != null && !name.isEmpty()) {
            try {
                if (me != null) {
                    user = name;
                    me.setName(user);
                } else {
                    user = name;
                    me = new Player(name);
                    me.initShips();
                }
                LoginDialog.closeThis(loginDialog);
                game.registerClient(UI.getInstance(), name);
                game.login(UI.getInstance(), name, pw);
                //game.requestFreeLobbies(this);

            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /* RMI callback methods */
    @Override
    public void shotFired(int x, int y, boolean hit) throws RemoteException {
        /* when this is called, it means that the player has received the shot from the opponent */
        gamestate.set(UIHelpers.PLAYING);
        handleState();
        oppButtons[x][y].setBackground(hit ? Color.RED : Color.CYAN);
    }

    @Override
    public void gameOver(boolean won) throws RemoteException {
        UIHelpers.messageDialog("You have " + (won ? "won the game over " : "lost the game to ") + other.getName(), UIHelpers.MSG_GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void canPlay(boolean canPlay) throws RemoteException {
        /* stands apart because its atomic */
        gamestate.set(canPlay ? UIHelpers.PLAYING : UIHelpers.WAITING);
        handleState();
        if (canPlay) {
            UIHelpers.messageDialog("Wake up!.. it's your turn!", "Make your move");
        } else {
            game.wait(UI.getInstance(), lobbyID.get(), me.getId());
        }
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
        if (players != null && !players.isEmpty()) {
            LobbyLister.setVisibility(true);
            LobbyLister.clearAll();
            players.stream().forEach(s -> {
                LobbyLister.addToList(s);
            });
        } else {
            UIHelpers.messageDialog("No players from server.", "playerList()", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public Player getPlayer() throws RemoteException {
        System.out.println("Player sent to server -> " + me.getId() + " : " + me.getName());
        return me;
    }

    @Override
    public void setPlayer(final Player player, final boolean opponent) throws RemoteException {
        if (opponent) {
            other = player;
            System.out.println("Opponent updated from server -> " + other.getId() + " : " + other.getName());
        } else {
            me = player;
            System.out.println("Player updated from server -> " + me.getId() + " : " + me.getName());
            gamestate.set(UIHelpers.PLACEMENT);
            handleState();
            setTitle();
        }
    }

    @Override
    public void loginstatus(boolean wasOkay) throws RemoteException {
        Statics.isLoggedIn = wasOkay;
        if (wasOkay) {
            UI.gamestate.set(UIHelpers.ONLINE);
            System.out.println("Logged in ok.");
        } else {
            UI.gamestate.set(UIHelpers.OFFLINE);
            System.out.println("Login failed.");
        }
    }

    @Override
    public void updateSessionID(String newID) throws RemoteException {
        sessionID = newID;
        System.out.println("New session ID from server : " + sessionID);
    }

    @Override
    public void setFreeLobbies(ArrayList<String> lobbies) throws RemoteException {
        LobbyLister.setVisibility(true);
        LobbyLister.clearAll();
        if (lobbies.isEmpty()) {
            UIHelpers.messageDialog("No free lobbies found", user);
        } else {
            lobbies.stream().forEach(s -> {
                LobbyLister.addToList(s);
            });
        }
    }

    @Override
    public void shipSunk(int shipType, boolean yourShip) throws RemoteException {
        if (shipType == 0) {
            if (yourShip) {
                UIHelpers.messageDialog("Your " + me.getShip(shipType).getShipType() + " has been sunk by " + other.getName(), "Ship sunk!!!!");
                colorShip(me.getShip(shipType).getStartX(), me.getShip(shipType).getStartY(), me.getShip(shipType), Color.BLACK, 0);
            } else {
                UIHelpers.messageDialog("You sunk " + me.getShip(shipType).getShipType() + " belonging to " + other.getName() + " - good job!", "Ship sunk!!!!");
            }
        }
    }

    @Override
    public void ping(long time) throws RemoteException {
        System.out.println("PING: Latency (ms) : " + (System.currentTimeMillis() - time));
    }

    @Override
    public void setLobbyID(int lobbyID) throws RemoteException {
        UI.lobbyID.set(lobbyID);
        setTitle();
        System.out.println("Lobby id updated from server : " + lobbyID);
    }

    @Override
    public void hello() throws RemoteException {
        System.out.println("Server said hello.");
    }

    @Override
    public void deployed(boolean succes, boolean ready, String opponent) throws RemoteException {
        if (succes) {
            UIHelpers.messageDialog("Board deployment ok!", "Board deployment");
        } else {
            UIHelpers.messageDialog("Board deployment failed!", "Board deployment", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void createLobby() {
        try {
            UI.getInstance().game.requestLobbyID(UI.getInstance(), me.getId());
        } catch (RemoteException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void joinLobby(final int lobbyID) {
        try {
            UI.getInstance().game.joinLobby(UI.getInstance(), lobbyID, me.getId());
        } catch (RemoteException ex) {
            System.err.println("Error while joining lobby.");
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Configure the UI for game start...
     */
    public void initGame() {

        gamestate.set(UIHelpers.PLAYING);

        b.removeAll();
        c.removeAll();
        d.removeAll();

        if (other == null) {
            other = new Player("mufmuf");
            other.initShips();
        }

        b = mainFrame.getContentPane();
        b.add(setBoard(0), BorderLayout.WEST);
        c = mainFrame.getContentPane();
        c.add(setBoard(1), BorderLayout.EAST);
        d = mainFrame.getContentPane();
        d.add(UIHelpers.whoseBoard(lobbyID.get(), me, other), BorderLayout.NORTH);

        mainFrame.pack();

        /* draw your ships on the RIGHT side */
    }

    private void setTitle() {
        mainFrame.setTitle("Battleship: " + me.getName() + " (" + Integer.toString(me.getId()) + ") : " + Integer.toString(lobbyID.get()));
    }

    private void fire(final int x, final int y) {
        colorBlock(x, y, Color.BLACK, true);
        try {
            canPlay(false);
            /* fire shot, the server will set the playable state of the client once opponent has fired as well */
            game.fireShot(UI.getInstance(), lobbyID.get(), me.getId(), x, y);
        } catch (RemoteException ex) {
            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            UIHelpers.messageDialog("Fatal error", "Exiting");
            System.exit(0);
        }
    }

    public static String getCletters(int i) {
        return cletters[i];
    }

    public static String getCnumbers(int i) {
        return cnumbers[i];
    }

    public static Player getMe() {
        return me;
    }

    /**
     * The listener for the buttons on the board. Purpose : Ship placement
     */
    private class BoardListener implements ActionListener {

        private final int x;
        private final int y;

        public BoardListener(final int x, final int y) {
            super();
            this.x = x;
            this.y = y;
        }

        @Override
        public void actionPerformed(ActionEvent v) {

            if (gamestate.get() == UIHelpers.PLAYING) {
                fire(x, y);
            } else if (gamestate.get() != UIHelpers.PLACED && gamestate.get() != UIHelpers.PLAYING && gamestate.get() != UIHelpers.WAITING) {

                // we are in construction faze
                Ship s = me.getShip(index_ship);

                if (UIHelpers.isValidPos(x, y, s, me, UIHelpers.getSelectedDirection(index_direction))) {
                    //System.out.println("Ship placement for -> " + s);
                    if (s.isPlaced()) {
                        /* since the ship appears to be placed, just remove it if user clicked another button */
                        handleShip(s.getStartX(), s.getStartY(), 0, s, UIHelpers.SHIP_PLACE.REMOVE);
                    }
                    handleShip(x, y, 0, s, UIHelpers.SHIP_PLACE.ADD);

                    boolean ok = true;
                    for (int i = 0; i < me.getShips().size(); i++) {
                        if (!me.getShip(i).isPlaced()) {
                            ok = false;
                            break;
                        }
                    }
                    deploy.setEnabled(ok);
                } else {
                    UIHelpers.messageDialog("Unable to place the selected ship at this location.", "Error");
                }
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

                /* for testing ONLY */
                initGame();

                if (!Statics.isLoggedIn) {
                    UIHelpers.messageDialog("You are not logged in.", "Log in required to play.");
                } else if (Statics.isLoggedIn && Statics.gameInProgress) {
                    int q = UIHelpers.confirmDialog("Are you sure you would like to start a new game?\nYou will loose this game!", "New Game?");
                    if (q == 0) {

                        //resets variables
                        b.removeAll();
                        c.removeAll();
                        d.removeAll();

                        gamestate.set(UIHelpers.ONLINE);

                        Statics.yourTurn = false;
                        Statics.gameInProgress = false;

                        ready = 0;

                        LobbyLister.setVisibility(true);
                        try {
                            game.requestFreeLobbies(UI.getInstance(), me.getId());
                        } catch (RemoteException ex) {
                            Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
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

            Ship s = me.getShip(index_ship);

            if (s.isPlaced() && combo_direction.getSelectedIndex() != UIHelpers.getIndexDirection(s.isHorizontal())) {
                System.out.println("Direction combobox changed because ship has different direction.");
                combo_direction.setSelectedIndex(UIHelpers.getIndexDirection(s.isHorizontal()));
            }
        }
    }

    /**
     * Direction combobox listener. Purpose : Alters what direction the ship
     * should be placed in.
     */
    private class DirectListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            int old_index = index_direction;
            index_direction = combo_direction.getSelectedIndex();

            Ship s = me.getShip(index_ship);
            System.out.println(s.getShipType());

            if (s.isPlaced()) {
                if (UIHelpers.isValidPos(s.getStartX(), s.getStartY(), s, me, UIHelpers.getSelectedDirection(index_direction))) {
                    //handleShip(s.getLocStart().x, s.getLocStart().y, 0, s, UIHelpers.SHIP_PLACE.REMOVE);

                    //s.setDirection(UIHelpers.getSelectedDirection(index_direction));
                    handleShip(s.getStartX(), s.getStartY(), 0, s, UIHelpers.SHIP_PLACE.ADD);

                } else {
                    UIHelpers.messageDialog("You can not turn the ship direction based on it's location.\nMove the ship first", "Error");
                    combo_direction.setSelectedIndex(old_index);
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
                    gamestate.set(UIHelpers.WAITING);
                    handleState();
                    System.out.println("Deploying : " + me.getShips());
                    game.deployShips(UI.getInstance(), lobbyID.get(), me);
                } catch (RemoteException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
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
            if (UIHelpers.isConnected(UI.getInstance().game)) {
                if (Statics.isLoggedIn) {
                    Statics.isLoggedIn = false;
                    try {
                        UI.getInstance().game.logout(me.getName());
                    } catch (RemoteException ex) {
                        Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // TODO : Move this entire bullcrap to the login dialog and implement observer.
                    java.awt.EventQueue.invokeLater(() -> {
                        if (!loginDialog.isVisible()) {
                            loginDialog.setVisible(true);
                        }
                    });
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
            if (UIHelpers.confirmDialog("Are you sure you would like to exit Battleship?", "Exit?") == 0) {
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
                }
                System.exit(0);
            }
        }
    }

}
