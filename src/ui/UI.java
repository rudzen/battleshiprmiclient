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

import dataobjects.PPoint;
import dataobjects.Player;
import dataobjects.Ship;
import interfaces.IBattleShip;
import interfaces.IClientListener;
import interfaces.IShip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static java.awt.image.ImageObserver.WIDTH;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
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
 *
 * @author rudz
 */
public class UI extends JFrame implements IClientListener {

    private static final long serialVersionUID = 6911438361535703573L;

    private IBattleShip game;

    /* myself */
    IClientListener clientListener;

    private final String registry;

    /* UI Stuff */
    private JDialog loginDialog;


    /* the two playing field button arrays, shown on the UI when playing..
       0 = this player
       1 = remote player
     */
    private static JButton[][] ownButtons = new JButton[10][10];
    private static JButton[][] oppButtons = new JButton[10][10];

    /**
     * The two boards as panels where the buttons are located inside. 0 = local
     * player 1 = remote player
     */
    private static JPanel[] boards = new JPanel[2];

    /* options window frame */
    private static Options options = new Options("Options");

    /* for manually inputting ships */
    private static JPanel inputpanel;

    /* status bar */
    private static JPanel statusBar;

    /* board and input panel */
    private static Container b, c, d;

    /* input bar */
    private JPanel input;

    /* menu items */
    private static JMenuItem m, pvp;

    /* arrays for combo boxes */
    private static final String[] cletters = {" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J"};
    private static final String[] cnumbers = {" ", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
    private static final String[] ships = {"Carrier", "Cruiser", "Destroyer", "Submarine", "Patrol Boat"};
    private static final String[] direction = {"Horizontal", "Vertical"};

    /* ships */
    private final JComboBox cshi = new JComboBox(ships);

    /* directions */
    private final JComboBox cdir = new JComboBox(direction);

    /* message bar */
    private JTextField mbar = new JTextField();

    /* deploy button */
    private static final JButton deploy = new JButton("DEPLOY");

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
    //BoardListener boardListener = new BoardListener();
    DirectListener directionListener = new DirectListener();

    private static Player me;
    private static Player other;

    /* this is just to save time! */
    // TODO : move to GameState
    protected static String user, user2;
    protected static boolean gameover;

    
    
    
    /* RMI callback methods */
    @Override
    public void shotFired(int x, int y, boolean hit) throws RemoteException {
        ownButtons[x][y].setBackground(hit ? Color.YELLOW : Color.CYAN);
    }

    @Override
    public void shipSunk(int x, int y, int direction, int len) throws RemoteException {
        // TODO : Implement.
    }

    @Override
    public void setBoard(int[][] board) throws RemoteException {
        me.setBoard(board);
        // TODO : Something else here?
    }

    @Override
    public void gameOver(boolean won) throws RemoteException {
        StringBuilder sb = new StringBuilder();
        sb.append("You have ").append(won ? "won the game over " : "lost the game to ").append(other.getName());
        UIHelpers.messageDialog(sb.toString(), UIHelpers.MSG_GAME_OVER, JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void canPlay(boolean canPlay) throws RemoteException {
        deploy.setEnabled(canPlay);
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
    public void updateOpponent(Player player) throws RemoteException {
        other = player;
    }

    @Override
    public void updateOpponentBoard(int[][] board) throws RemoteException {
        other.setBoard(board);
        // TODO : Something else here?
    }

    @Override
    public void updateBoard(int[][] board) throws RemoteException {
        setBoard(board);
        // TODO : Re-draw the bastard..
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                switch (board[i][j]) {
                    case 1:

                    case 2:

                    case 3:

                    case 4:

                    case 5:

                }
            }
        }
    }

    @Override
    public void ping() throws RemoteException {
        game.pong(this, me);
    }

    @Override
    public void isLoggedOut(boolean status) throws RemoteException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private enum SHIP_PLACE {
        REMOVE, ADD, SUNK
    }

    private static class RunnableImpl implements Runnable {

        private final String registry;

        public RunnableImpl(final String registry) {
            this.registry = registry;
        }

        @Override
        public void run() {
            UI ui = new UI(registry);

            try {

                // Registration format
                //registry_hostname :port/service
                // Note the :port field is optional
                String registration = "rmi://" + registry + "/Battleship";
                /* Lookup the service in the registry, and obtain a remote service */
                Remote remoteService = Naming.lookup(registration);
                ui.setGame((IBattleShip) remoteService);

                ui.getGame().fireShot(3, 5, ui, me);
                ui.getGame().registerClient(ui, UI.me);
            } catch (final RemoteException re) {
                UIHelpers.messageDialog("RMI Error - RemoteException()", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, re);
            } catch (final NotBoundException ex) {
                UIHelpers.messageDialog("No game server available - NotBountException()", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            } catch (final MalformedURLException ex) {
                UIHelpers.messageDialog("No game server available - MalformedURLException()", "Error", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static void runGame(final String registry) {
        EventQueue.invokeLater(new RunnableImpl(registry));
    }

    public UI(final String registry) {
        super();

        this.registry = registry;
        me = new Player("");

        System.out.println("Registry : " + registry);

        me.initShips();

        setupUI();
    }

    public void connect() {

    }

    /**
     * Sets up the window and stuff.
     */
    private void setupUI() {

        /* set up the buttons */
        for (int k = 0; k < ownButtons.length; k++) {
            for (int j = 0; j < ownButtons.length; j++) {
                ownButtons[j][k] = new JButton();
                ownButtons[j][k].setBackground(null);
                ownButtons[j][k].addActionListener(new BoardListener(game, this, j, k));
            }
        }

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

        //statusBar = new JPanel();
        //d.add(statusBar);
        pack();
        setVisible(true);
    }

    /**
     * Creates Game menu and sub menus
     *
     * @return The menu bar which was created.
     */
    private JMenuBar createMenuBar() {
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
        m = new JMenuItem(Statics.isLoggedIn ? "Logout" : "Login");
        m.addActionListener(new LoginListener(this));
        menu.add(m);
        m = new JMenuItem("Options");
        m.addActionListener(new OptionsListener(this));
        menu.add(m);
        m = new JMenuItem("Exit");
        m.addActionListener(new ExitListener(this));
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
        deploy.addActionListener(new DeployListener(this));
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
     * Helper function to determin if the location is valid..
     *
     * @param x the X location clicked
     * @param y the Y location clicked
     * @param s the Ship
     * @return true if possible, otherwise false
     */
    private static boolean isValidPos(final int x, final int y, final Ship s) {
        boolean val = ((s.getDirection() == IShip.DIRECTION.HORIZONTAL ? x : y) + s.getLength() <= ownButtons.length);
        System.out.println("Position is valid : " + val);
        return val;
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
        if (s.getDirection() == IShip.DIRECTION.HORIZONTAL) {
            for (int i = x; i < s.getLocEnd().getX(); i++) {
                ownButtons[i][s.getLocStart().getY() + 1].setBackground(col);
            }
        } else if (s.getDirection() == IShip.DIRECTION.VERTICAL) {
            for (int i = y; i < s.getLocEnd().getY(); i++) {
                ownButtons[s.getLocStart().getX()][i].setBackground(col);
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
    private static void handleShip(final int x, final int y, final int fieldIndex, final Ship s, final SHIP_PLACE place) {
        Color col;
        PPoint locStart = s.getLocStart(); // the location start
        final boolean isPlaced = s.isPlaced(); // is it placed ?
        IShip.DIRECTION dir = s.getDirection(); // direction of the ship

        if (place == SHIP_PLACE.ADD) {
            /* if the ship is to be added */

 /* remove the old ship on the board before re-drawing the new one. */
            if (isPlaced) {
                colorShip(x, y, s, null);
            }

            /* update the ship with the new coordinated */
            s.setLocStart(new PPoint(x, y));
            s.setLocEnd(Ship.setEnd(locStart, s.getLength(), dir));

            /* re-draw the ship with the new coordinated */
            colorShip(x, y, s, Color.YELLOW);

            s.setIsPlaced(true);

            /* update the ship in the player */
            me.setShip(sindex, s);

        } else if (place == SHIP_PLACE.REMOVE) {
            /* if the ship is to be removed */
            s.setIsPlaced(false);

            colorShip(x, y, s, null);
        }

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
     * Update the username, this is called from the login dialog.
     *
     * @param name The name of the user that was entered.
     * @param pw The password of the user which was entered
     * @param game The server game logic object.
     */
    public static void updateUser(final String name, final String pw, final IBattleShip game) {
        if (name != null && !"".equals(name)) {
            if (me != null) {
                user = name;
                me.setName(user);
            } else {
                user = name;
                me = new Player(name);
            }
            try {
                Statics.isLoggedIn = game.login(user, pw);
            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /**
     * Convert selected combobox direction to internat data structure.
     *
     * @return The direction selected by the user as defined by the IShip
     * interface.
     */
    private static IShip.DIRECTION getSelectedDirection() {
        return dindex == 0 ? IShip.DIRECTION.HORIZONTAL : IShip.DIRECTION.VERTICAL;
    }

    private static int getIndexDirection(final IShip.DIRECTION dir) {
        return dir == IShip.DIRECTION.HORIZONTAL ? 0 : 1;
    }

    /**
     * The listener for the buttons on the board. Purpose : Ship placement
     */
    private static class BoardListener implements ActionListener {

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
                
                Ship s = me.getShip(sindex);
                
                if (isValidPos(x, y, s)) {
                    System.out.println("Ship placement for -> " + s);
                    if (s.isPlaced()) {

                        /* since the ship appears to be placed, just remove it if user clicked another button */
                        handleShip(s.getLocStart().getX(), s.getLocStart().getY(), 0, s, SHIP_PLACE.REMOVE);
                    }
                    
                    handleShip(x, y, 0, s, SHIP_PLACE.ADD);
                    
                } else {
                    UIHelpers.messageDialog("Unable to place the selected ship at this location.", "Error");
                }
                

            } else {
                try {
                    /* user is fireing at the opponent!!! */
                    game.fireShot(x, y, ui, me);
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
            dindex = cdir.getSelectedIndex();

            System.out.println("Direction combobox used");

            final Ship s = me.getShip(sindex);

            if (isValidPos(s.getLocStart().getX(), s.getLocStart().getY(), s)) {
                s.setDirection(getSelectedDirection());

                if (s.isPlaced()) {
                    handleShip(s.getLocStart().getX(), s.getLocStart().getY(), 0, s, SHIP_PLACE.REMOVE);
                }

                handleShip(s.getLocStart().getX(), s.getLocStart().getY(), 0, s, SHIP_PLACE.ADD);

                me.setShip(sindex, s);
            } else {
                UIHelpers.messageDialog("You can not turn the ship direction based on it's location.\nMove the ship first", "Error");
            }

        }
    }

    /**
     * Exit menu item listener. Purpose : Handles the users request to exit the
     * program.
     */
    private class ExitListener implements ActionListener {

        private final UI ui;

        public ExitListener(final UI ui) {
            this.ui = ui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (UIHelpers.confirmDialog("Are you sure you would like to exit Battleship?\nYou will loose points", "Exit?") == 0) {
                try {
                    // TODO : Add forefeit command in game object

                    UIHelpers.messageDialog(game.logout(ui, me) ? "You have been logged out." : "Failed to log out, server could be unresponsive.", "Logged out");
                } catch (RemoteException ex) {
                    Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    ui.dispose();
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
            dindex = cshi.getSelectedIndex();
            System.out.println("Ship used");

            if (me.getShip(sindex).isPlaced() && cdir.getSelectedIndex() != getIndexDirection(me.getShip(sindex).getDirection())) {
                System.out.println("Direction combobox changed because ship has different direction.");
                cdir.setSelectedIndex(getIndexDirection(me.getShip(sindex).getDirection()));
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

                        gametype = (JMenuItem) e.getSource();

                        if (gametype == pvp) {

                            // TODO : Get playerlist from server *OR* Input name of opponent!
                            // TODO : Make better abstraction!!!!!
                            me = new Player(user);

                            // TODO : Ask server for opponent here !
                            //ready=1;
                        }
                        pack();
                        repaint();
                    }
                }
            }
        }
    }

    /**
     * Listener for Login
     */
    private class LoginListener implements ActionListener {

        private final UI ui;

        public LoginListener(final UI ui) {
            this.ui = ui;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (UIHelpers.isConnected(game)) {
                if (Statics.isLoggedIn) {
                    Statics.isLoggedIn = false;

                    try {
                        // TODO : Add RMI interface to actually let the server know about it.
                        game.logout(ui, me);
                    } catch (RemoteException ex) {
                        Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    // TODO : Move this entire bullcrap to the login dialog and implement observer.
                    ui.setLoginDialog(LoginDialog.login(ui.game));
                }
            }
        }
    }

    /* Listener for Deploy Button */
    private class DeployListener implements ActionListener {

        private final UI ui;

        public DeployListener(final UI ui) {
            this.ui = ui;
        }

        @Override
        public void actionPerformed(ActionEvent v) {
            if (UIHelpers.confirmDialog("Are you sure you would like to deploy your ships?", "Deploy Ships?") == 0) {
                try {
                    System.out.println("The player to deploy : " + me);
                    game.deployShips(ui, me);
                    pack();
                    repaint();
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

    /**
     * Returns ship colour, as selected by the user
     *
     * @return
     */
    public static Color getColor() {
        return Options.COLOURS[Options.SHIP_COLOUR.getSelectedIndex()];
    }

    public void setGame(final IBattleShip game) {
        this.game = game;
    }

    public IBattleShip getGame() {
        return game;
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

    public JDialog getLoginDialog() {
        return loginDialog;
    }

    public void setLoginDialog(JDialog loginDialog) {
        this.loginDialog = loginDialog;
    }

    public static void setDeploy(boolean k) {
        deploy.setEnabled(k);
    }

    public static Player getMe() {
        return me;
    }

    public static void setMe(final Player me) {
        UI.me = me;
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

}
