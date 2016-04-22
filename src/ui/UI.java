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
import dataobjects.PPoint;
import dataobjects.Player;
import dataobjects.Ship;
import interfaces.IBattleShip;
import interfaces.IClientListener;
import interfaces.IShip;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
public class UI extends JFrame implements IClientListener {

    private static final long serialVersionUID = 6911438361535703573L;

    private IBattleShip game;

    private final String registry;

    /* UI Stuff */
 /* the two playing field button arrays, shown on the UI when playing..
       0 = this player
       1 = remote player
     */
    private static ArrayList<JButton[][]> playingFields = new ArrayList<>(2);

    /**
     * The two boards as panels where the buttons are located inside. 0 = local
     * player 1 = remote player
     */
    private static JPanel[] boards = new JPanel[2];

    /* the game state */
    private static GameState gameState = new GameState();

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

    /* server interface through RMI */
    IBattleShip bship;

    /* myself */
    IClientListener clientListener;

    private static Player me;
    private static Player other;

    /* this is just to save time! */
    // TODO : move to GameState
    protected static String user, user2;
    protected static boolean gameover;

    /* RMI callback methods */
    @Override
    public void shotFired(int x, int y, boolean hit) throws RemoteException {
        playingFields.get(0)[x][y].setBackground(hit ? Color.YELLOW : Color.CYAN);
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
        UIHelpers.messageDialog(UIHelpers.MSG_GAME_OVER, sb.toString(), JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void canPlay(boolean canPlay) throws RemoteException {

        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void showMessage(String title, String message, int modal) throws RemoteException {
        UIHelpers.messageDialog(title, message, modal);
    }

    @Override
    public void opponentQuit() throws RemoteException {
        UIHelpers.messageDialog("Game is terminated", other.getName() + " has left the game.", JOptionPane.ERROR_MESSAGE);
        UIHelpers.messageDialog("Game is terminated", "You have gained 0 points, but do not worry as " + other.getName() + " has lost points.", JOptionPane.ERROR_MESSAGE);
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
        // TODO : Call pong on the server!..
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private enum SHIP_PLACE {
        REMOVE, ADD, SUNK
    }

    public UI(final String registry) {
        super();

        this.registry = registry;

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

        statusBar = new JPanel();
        d.add(statusBar);

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
        m = new JMenuItem(gameState.isLoggedIn() ? "Logout" : "Login");
        m.addActionListener(new LoginListener(this));
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
        boards[n] = new JPanel(new GridLayout(11, 11));

        JTextField k;
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 11; j++) {
                if (j != 0 && i != 0) {
                    playingFields.get(n)[i - 1][j - 1].addActionListener(boardListener);
                    boards[n].add(playingFields.get(n)[i - 1][j - 1]);
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
     * Places a ship from the board
     *
     * @param s The ship to add
     */
    private void placeShip(final int fieldIndex, final Ship s) {

    }

    /**
     * Handles ship on the board
     *
     * @param s The ship to remove
     */
    private void handleShip(final int fieldIndex, final Ship s, final SHIP_PLACE place) {
        Color col;
        if (place == SHIP_PLACE.ADD) {
            col = Color.YELLOW;
            s.setIsPlaced(true);
        } else if (place == SHIP_PLACE.REMOVE) {
            s.setIsPlaced(false);
            col = Color.GRAY;
        } else {
            // ship sunk
            col = Color.BLACK;
        }

        if (s.getDirection() == IShip.DIRECTION.HORIZONTAL) {
            for (int i = 0; i < s.getLength(); i++) {
                playingFields.get(fieldIndex)[s.getLocStart().getX() + i][s.getLocStart().getY()].setBackground(col);
            }
        } else {
            for (int i = 0; i < s.getLength(); i++) {
                playingFields.get(fieldIndex)[s.getLocStart().getX()][s.getLocStart().getY() + i].setBackground(col);
            }
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
     * Returns ship colour, as selected by the user
     *
     * @return
     */
    public static Color getColor() {
        return Options.COLOURS[Options.SHIP_COLOUR.getSelectedIndex()];
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

    /**
     * The listener for the buttons on the board. Purpose : Ship placement
     */
    private class BoardListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent v) {
            if (ready == 0) {
                if (me.getShip(sindex).isPlaced()) {
                    handleShip(0, me.getShip(sindex), SHIP_PLACE.REMOVE);
                }

                Object source = v.getSource();
                outer:
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (source == playingFields.get(0)[i][j]) {
                            // TODO : Make ship DTO here
                            Ship s = me.getShip(sindex);
                            s.setIsPlaced(true);
                            s.setDirection(dindex == 0 ? IShip.DIRECTION.HORIZONTAL : IShip.DIRECTION.VERTICAL);
                            s.setLocStart(new PPoint(i, j));
                            s.setLocEnd(Ship.setEnd(s.getLocStart(), s.getLength(), s.getDirection()));

                        }

                        break outer;
                    }
                }
            }
            handleShip(0, me.getShip(sindex), SHIP_PLACE.ADD);
        }
    }

    /**
     * Direction combobox listener. Purpose : Alters which ship that should be
     * placed.
     */
    private class DirectListener implements ActionListener {

        private void updateShips() {
            /* clear the button colours */
            JButton[][] jp = playingFields.get(0);
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    jp[i][j].setBackground(Color.GRAY);
                }
            }

            /* redraw the ships */
            for (int i = 0; i < me.getShips().length; i++) {
                Ship s = me.getShip(i);
                if (s.isPlaced()) {
                    if (s.getDirection() == IShip.DIRECTION.HORIZONTAL) {
                        for (int j = 0; j < s.getLength(); j++) {
                            jp[((int) s.getLocStart().getX()) + j][(int) s.getLocStart().getY()].setBackground(Color.YELLOW);
                        }
                    } else {
                        /* vertical */
                        for (int j = 0; j < s.getLength(); j++) {
                            jp[(int) s.getLocStart().getX()][((int) s.getLocStart().getY()) + j].setBackground(Color.YELLOW);
                        }
                    }
                }
            }

        }

        @Override
        public void actionPerformed(ActionEvent v) {
            dindex = cdir.getSelectedIndex();

            Ship ship = me.getShip(dindex);

            ship.setIsPlaced(true);

            if (!me.getShip(sindex).isPlaced()) {
                handleShip(0, me.getShip(sindex), SHIP_PLACE.ADD);
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
                // TODO : Add sending defeat to server + log out.
                System.exit(0);
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
            sindex = cshi.getSelectedIndex();
            Ship s = me.getShip(sindex);
            if (s.isPlaced()) {
                if (me.getShip(sindex).getDirection() == IShip.DIRECTION.HORIZONTAL) {
                    cdir.setSelectedIndex(0);
                } else {
                    cdir.setSelectedIndex(1);
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
            if (gameState.isLoggedIn()) {
                gameState.setLoggedIn(false);

                // TODO : Add RMI interface to actually let the server know about it.
            } else {
                LoginDialog.login();

                final String name;
                
                // TODO : Get the username from the login dialog here!
                user = UIHelpers.getPlayerName();
                me = new Player(user);

                
                try {
                    game.fireShot(3, 5, ui);
                    game.registerClient(ui);
                } catch (final RemoteException re) {

                }

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
