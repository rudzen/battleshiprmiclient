package dataobjects;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import ui.UI;
import ui.UIHelpers;
import utility.Statics;

public class Player {

    private int hits;
    private final Ship[] boats = new Ship[5];
    private String user;//user name
    //private JPanel board;//panel to store game board
    private int shipsleft;
    private int shots;// shots taken
    private final boolean[][] hitormiss = new boolean[10][10];
    private final JButton[][] bboard = new JButton[10][10];
    //gbutton=new JButton [10][10];
    //, hit(1), or sunk(2); default is (3)
    private JPanel gboard, myboard;
    //private Vector rows = new Vector();
    //private Vector cols = new Vector();
    CopyOnWriteArrayList<Integer> rows = new CopyOnWriteArrayList<>();
    CopyOnWriteArrayList<Integer> cols = new CopyOnWriteArrayList<>();
    private Timer timeleft;//
    private final String[][] whatship = new String[10][10];//stores name of ships or " "
    private int lastship;//length of the last ship left
    private final NumberFormat nf = NumberFormat.getPercentInstance();
    //private Board games

    private UI ui;
    
    public Player(final String name, final UI ui) {
        this.ui = ui;
        user = name;
        shipsleft = 5;
        lastship = 0;

        if (UI.isAutoSet() || UI.isLocal()) {
            for (int i = 0; i < 5; i++) {
                boats[i] = new Ship(UI.getShips(i), 0, 0, 0, 0);
            }
        }
        timeleft = new Timer(10000, new AttackListener(ui));
        
        shots = 0;
        hits = 0;
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                bboard[i][j] = new JButton();
                bboard[i][j].setBackground(null);
                hitormiss[i][j] = false;
                whatship[i][j] = " ";
            }
        }
    }

    public void setUser(String m) {
        user = m;
    }

    //returns player's game board with ap
    public JPanel getMyBoard() {
        return myboard;
    }

    //returns player's game board with ap
    public JPanel getGBoard() {
        return gboard;
    }

    public void setMyBoard(JPanel r) {
        myboard = r;
    }

    public void setGBoard(JPanel r) {
        gboard = r;
    }

    public void setBoats(int i, Ship r) {
        boats[i] = r;
    }

    /*public void setGames(Board k)
	{
		this.games=k;
	}	
	
	public Board getGames()
	{
		return this.games;
	}*/
    public Ship getBoats(int x) {
        return boats[x];
    }

    public void setShots() {
        shots--;
    }

    public void setHits() {
        hits++;
    }

    public int getShots() {
        return shots;
    }

    public int getHits() {
        return hits;
    }

    public String getAcc() {
        if (shots > 0) {
            return nf.format((double) hits / (double) shots);
        } else {
            return "";
        }
    }

    public Timer getTimer() {
        return timeleft;
    }

    public JButton getBboard(int i, int j) {
        return bboard[i][j];
    }

    public void setBboard(int i, int j, Color k) {
        bboard[i][j].setBackground(k);
    }

    //returns user name
    public String getUser() {
        return user;
    }

//    //checks if Statistics frame is open
//    public static void isStatsOpen() {
//        if (Statistics.isShowing) {
//            Battleship.getStats().removeAll();
//            Battleship.getStats().setLayout(new GridLayout(6, 3));
//            Battleship.setData(new JLabel(""));
//            Battleship.getStats().add(Battleship.getData());
//            Battleship.setData(new JLabel("Player 1", SwingConstants.CENTER));
//            Battleship.getStats().add(Battleship.getData());
//            Battleship.setData(new JLabel("Player 2", SwingConstants.CENTER));
//            Battleship.getStats().add(Battleship.getData());
//            Battleship.setData(new JLabel("Names"));
//            Battleship.getStats().add(Battleship.getData());
//            if (Statics.you == 0) {
//                resetStats(Statics.you, Statics.enemy);
//            } else {
//                resetStats(Statics.enemy, Statics.you);
//            }
//            Battleship.getStatistics().getContentPane().add(Battleship.getStats());
//            Battleship.getStatistics().pack();
//            Battleship.getStatistics().repaint();
//        }
//    }
//
//    public static void resetStats(int x, int y) {
//        Battleship.setData(new JLabel(UI.getPlayer(x).getUser(), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(UI.getPlayer(y).getUser(), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel("Shots Taken"));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(Integer.toString(UI.getPlayer(x).getShots()), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(Integer.toString(UI.getPlayer(y).getShots()), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel("Hits"));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(Integer.toString(UI.getPlayer(x).getHits()), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(Integer.toString(UI.getPlayer(y).getHits()), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel("Shot Accuracy"));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(UI.getPlayer(x).getAcc(), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(UI.getPlayer(y).getAcc(), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel("Ships Left"));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(Integer.toString(UI.getPlayer(x).getShipsLeft()), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//        Battleship.setData(new JLabel(Integer.toString(UI.getPlayer(y).getShipsLeft()), SwingConstants.CENTER));
//        Battleship.getStats().add(Battleship.getData());
//    }
    public String getWhatShip(int x, int y) {
        return whatship[x][y];
    }

    public void setLastShip(int x) {
        lastship = x;
    }

    public int getLastShip() {
        return lastship;
    }

    public int getShipsLeft() {
        return shipsleft;
    }

    public void setShipsLeft() {
        shipsleft -= 1;
    }

    public void setWhatShip(int x, int y, String u) {
        whatship[x][y] = u;
    }

    //method that determines if hit ship is sunk or not
    public boolean isSunk(int x, int y) {
        
        // TODO : Move to server, and re-code to support generic player parameters
        
        int f = 0;

        //finds which ship was sunk
        while (!boats[f].getName().equals(getWhatShip(x, y))) {
            f++;
        }
        boats[f].shipHit();

        if (boats[f].getHitsLeft() != 0) {
            return false;
        }

        UI.getPlayer(1).setShipsLeft();
        if (!Statics.yourTurn) {
            for (int k = 0; k < 10; k++) {
                for (int m = 0; m < 10; m++) {
                    if (boats[f].getName().equals(getWhatShip(k, m))) {
                        setBboard(k, m, Color.black);
                    }
                }
            }
            UIHelpers.messageDialog("You just lost your " + boats[f].getName() + "!", "Ship Destroyed", JOptionPane.WARNING_MESSAGE);
        } else {
            UIHelpers.messageDialog("You sank the " + boats[f].getName() + "!", "Good Job!", JOptionPane.INFORMATION_MESSAGE);
            for (int k = 0; k < 10; k++) {
                for (int m = 0; m < 10; m++) {
                    if (boats[f].getName().equals(getWhatShip(k, m))) {
                        setBboard(k, m, Color.black);
                    }
                }
            }
        }
        return true;
    }

    //method that determines if hit ship is sunk or not
    public boolean issSunk(int x, int y, String z) {
        int f = 0;

        while (!z.equals(boats[f++].getName())) {
        }

        boats[f].shipHit();

        System.out.println(z + ":  " + boats[f].getHitsLeft());

        if (boats[f].getHitsLeft() != 0) {
            return false;
        }

        setShipsLeft();
        UIHelpers.messageDialog("You just lost your " + boats[f].getName() + "!", "Ship Destroyed", JOptionPane.WARNING_MESSAGE);
        for (int k = 0; k < 10; k++) {
            for (int m = 0; m < 10; m++) {
                if (z.equals(getWhatShip(k, m))) {
                    setBboard(k, m, Color.black);
                }
            }
        }
        return true;
    }

    //sets hitormiss[x][y] to k 				
    public void setHitOrMiss(int x, int y, boolean k) {
        hitormiss[x][y] = k;
    }

    public boolean getHitOrMiss(int x, int y) {
        return hitormiss[x][y];
    }

    //checks if selected position is a plausible location for the remaining	ships
    public boolean rshipsv(int x, int y) {
        int u;
        int g;

        if (isValid(x + 1, y) && (UI.getPlayer(0).getMHS(x + 1, y) == 3
                || UI.getPlayer(0).getMHS(x + 1, y) == 1)
                || isValid(x - 1, y) && (UI.getPlayer(0).getMHS(x - 1, y) == 3
                || UI.getPlayer(0).getMHS(x - 1, y) == 1)) {
            u = 0;
        } else {
            u = 5;
        }
        while (u < 5) {
            g = 0;
            if (boats[u].getHitsLeft() != 0) {
                daloop:
                for (int i = x - boats[u].getLength(); i < x + boats[u].getLength(); i++) {
                    if (isValid(i, y) && (UI.getPlayer(0).getMHS(i, y) == 3
                            || UI.getPlayer(0).getMHS(i, y) == 1)) {
                        if (++g == boats[u].getLength()) {
                            break;
                        }
                    } else {
                        g = 0;
                    }
                }
                if (g == boats[u].getLength()) {
                    break;
                } else if (++u == 5) {
                    g = 0;
                }
            } else {
                u++;
            }
        }
        return u != 5;
    }

    //checks if selected position is a plausible location for the remaining	ships
    public boolean rshipsh(int x, int y) {
        int u;
        int g;

        if (isValid(x, y + 1) && (UI.getPlayer(0).getMHS(x, y + 1) == 3
                || UI.getPlayer(0).getMHS(x, y + 1) == 1)
                || isValid(x, y - 1) && (UI.getPlayer(0).getMHS(x, y - 1) == 3
                || UI.getPlayer(0).getMHS(x, y - 1) == 1)) {
            u = 0;
        } else {
            u = 5;
        }
        while (u < 5) {
            g = 0;
            if (boats[u].getHitsLeft() != 0) {
                daloop:
                for (int i = y - boats[u].getLength(); i < y + boats[u].getLength(); i++) {
                    if (isValid(x, i) && (UI.getPlayer(0).getMHS(x, i) == 3 || UI.getPlayer(0).getMHS(x, i) == 1)) {
                        g += 1;
                        if (g == boats[u].getLength()) {
                            break daloop;
                        }
                    } else {
                        g = 0;
                    }
                }
                if (g == boats[u].getLength()) {
                    break;
                } else if (++u == 5) {
                    g = 0;
                }
            } else {
                u++;
            }
        }
        return u != 5;
    }

    //checks if point (x,y) is valid		
    public boolean isValid(int x, int y) {
        return x >= 0 && y >= 0 && x <= 9 && y <= 9;
    }

    public void takeShot(int x, int y) {
        setShots();
        if (UI.getPlayer(1).getHitOrMiss(x, y)) {
            setHits();
            if (!UI.getPlayer(1).isSunk(x, y)) {
                UI.getPlayer(1).setBboard(x, y, Color.orange);

            }
        } else {
            UI.getPlayer(1).setBboard(x, y, Color.blue);
        }
    }

    public void humanAttack(ActionEvent v) {
        if (Statics.yourTurn) {
            Object source = v.getSource();
            int i, j = 0;
            outer:
            for (i = 0; i < 10; i++) {
                for (j = 0; j < 10; j++) {
                    if (source == UI.getPlayer(1).getBboard(i, j)) {
                        if (UI.getPlayer(1).getBboard(i, j).getBackground() == Color.black
                                || UI.getPlayer(1).getBboard(i, j).getBackground() == Color.orange
                                || UI.getPlayer(1).getBboard(i, j).getBackground() == Color.blue) {
                            UIHelpers.messageDialog("You tried that spot already.", "Wasted Shot", JOptionPane.ERROR_MESSAGE);
                        } else {
                            takeShot(i, j);
                        }
                        break outer;
                    } else if (source == getBboard(i, j)) {
                        UIHelpers.messageDialog("You are not supposed to fire on your own board!", "Lost Turn", JOptionPane.WARNING_MESSAGE);
                        break outer;
                    }
                }
            }

            if (i == 10 && j == 10) {
                UIHelpers.messageDialog("You took too long!", "Lost Turn");
            }
//            Player.isStatsOpen();
            Statics.yourTurn = false;
            timeleft.stop();
            if (UI.getPlayer(1).shipsleft > 0) {
                // 
                UI.getPlayer(1).timeleft.start();
                Statics.yourTurn = false;
            } else if ("OPP".equals(UI.getPlayer(1).user)) {
                //change once menu options work
                UIHelpers.messageDialog("YOU WON!", "It's A Celebration!");
            } else {
                UIHelpers.messageDialog(user + " won!!!", "It's A Celebration!");
            }
        }
    }

    
}
