package dataobjects;

import javax.swing.*;
import ui.UI;
import ui.UIHelpers;
import utility.Statics;

public class Ship {

    private String name;
    private int dir = 5,
            length,
            x1,
            y1,
            x2,
            y2;
    private int hitsleft;
    private boolean invalid;

    public Ship(String n, int d, int ln, int x, int y) {
        name = n;
        length = ln;
        dir = d;
        x1 = x;
        y1 = y;
        invalid = false;
        hitsleft = ln;
    }

    public Ship(String name, int direction, int length, int x, int y, int ex, int ey) {
        this.name = name;
        this.length = hitsleft = length;
        dir = direction;
        x1 = x;
        y1 = y;
        x2 = ex;
        y2 = ey;
        invalid = false;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getDirect() {
        return dir;
    }

    public int getX() {
        return x1;
    }

    public int getY() {
        return y1;
    }

    //returns the end x-point for this ship 
    public int getEndX() {
        return x2;
    }

    //returns the end y-point for this ship 
    public int getEndY() {
        return y2;
    }

    public void setInvalid(boolean newValue) {
        invalid = newValue;
    }

    public void shipHit() {
        hitsleft--;
    }

    public void setHitsLeft(int newValue) {
        hitsleft = newValue;
    }

    public int getHitsLeft() {
        return hitsleft;
    }

    public void clearship(final Player p) {
        switch (dir) {
            case 0:
                if (!invalid) {
                    for (int j = y1; j < y2; j++) {
                        p.setBboard(x1, j, null);
                        p.setHitOrMiss(x1, j, false);
                        p.setWhatShip(x1, j, " ");
                    }
                }
                break;
            case 1:
                if (!invalid) {
                    for (int i = x1; i < x2; i++) {
                        p.setBboard(i, y1, null);
                        p.setHitOrMiss(i, y1, false);
                        p.setWhatShip(i, y1, " ");
                    }
                }
                break;
        }
    }

    //Method to place the ships	
    public void placeship() {
        switch (dir) {
            case 0:
                if (length + y1 > 10) {
                    UIHelpers.messageDialog("A " + name + " placed in a " + UI.getDirection(dir) + " direction will not fit at position " + UI.getCletters(x1 + 1) + UI.getCnumbers(y1 + 1) + ".", "Invalid Placement", JOptionPane.ERROR_MESSAGE);
                    invalid = true;
                } else {
                    int j = 0;
                    while (j != length && !UI.getPlayer(Statics.you).getHitOrMiss(x1, y1 + j)) {
                        j++;
                    }
                    if (j != length) {
                        UIHelpers.messageDialog("Position " + UI.getCletters(x1 + 1) + UI.getCnumbers(y1 + j + 1) + " is already occupied.", "Invalid Placement", JOptionPane.ERROR_MESSAGE);
                        invalid = true;
                    } else {
                        x2 = x1;
                        y2 = y1 + length;
                        for (j = y1; j < y2; j++) {
                            UI.getPlayer(Statics.you).setBboard(x1, j, UI.getColor());
                            UI.getPlayer(Statics.you).setHitOrMiss(x1, j, true);
                            UI.getPlayer(Statics.you).setWhatShip(x1, j, name);
                        }
                        invalid = false;
                    }
                }
                break;
            case 1:
                if (x1 + length > 10) {
                    UIHelpers.messageDialog("A " + name + " placed in a " + UI.getDirection(dir) + " direction will not fit at position " + UI.getCletters(x1 + 1) + UI.getCnumbers(y1 + 1) + ".", "Invalid Placement", JOptionPane.ERROR_MESSAGE);
                    invalid = true;
                } else {
                    int j = 0;
                    while (j != length && !UI.getPlayer(Statics.you).getHitOrMiss(x1 + j, y1)) {
                        j++;
                    }
                    if (j != length) {
                        UIHelpers.messageDialog("Position " + UI.getCletters(x1 + j + 1) + UI.getCnumbers(y1 + 1) + " is already occupied.", "Invalid Placement", JOptionPane.ERROR_MESSAGE);
                        invalid = true;
                    } else {
                        y2 = y1;
                        x2 = x1 + length;
                        for (int i = x1; i < x2; i++) {
                            UI.getPlayer(Statics.you).setBboard(i, y1, UI.getColor());
                            UI.getPlayer(Statics.you).setHitOrMiss(i, y1, true);
                            UI.getPlayer(Statics.you).setWhatShip(i, y1, name);
                        }
                        invalid = false;
                    }
                }
                break;
        }
        if (UI.getCarrierPlaced() > 0 && UI.getBattleshipPlaced() > 0
                && UI.getSubmarinePlaced() > 0 && UI.getDestroyerPlaced() > 0
                && UI.getPatrolPlaced() > 0 && !invalid) {
            if (!UI.getPlayer(Statics.you).getBoats(0).invalid && !UI.getPlayer(Statics.you).getBoats(1).invalid && !UI.getPlayer(Statics.you).getBoats(2).invalid
                    && !UI.getPlayer(Statics.you).getBoats(3).invalid && !UI.getPlayer(Statics.you).getBoats(4).invalid) {
                UI.setDeploy(true);
            } else {
                UI.setDeploy(false);
            }
        } else {
            UI.setDeploy(false);
        }
    }

    public Ship compinput(int u, int n) {
        Ship boat;

        int i = 0;
        int j = 0;
        int x;
        int y;
        int shipl = 0;
        int dir;

        switch (u) {
            case 0:
                shipl = 5;
                break;
            case 1:
                shipl = 4;
                break;
            case 2:
            case 3:
                shipl = 3;
                break;
            case 4:
                shipl = 2;
                break;
        }

        do {
            x = (int) (Math.random() * 10);
            y = (int) (Math.random() * 10);
            dir = (int) (Math.random() * 2);//generates random direction within range			
            boat = new Ship(UI.getShips(u), dir, shipl, x, y);
            switch (dir) {
                case 0:
                    if (boat.length + y > 10 || x == 0 || y == 0) {
                        boat.invalid = true;
                    } else {
                        j = 0;
                        while (j != boat.length && !UI.getPlayer(n).getHitOrMiss(x, y + j)) {
                            j++;
                        }
                        if (j != boat.length) {
                            boat.invalid = true;
                        } else {
                            boat.x2 = x;
                            boat.y2 = y + boat.length;
                            for (j = y; j < boat.y2; j++) {
                                UI.getPlayer(n).setHitOrMiss(x, j, true);
                                UI.getPlayer(n).setWhatShip(x, j, UI.getShips(u));
                            }
                            boat.invalid = false;
                        }
                    }
                    break;
                case 1:
                    if (x + boat.length > 10 || x == 0 || y == 0) {
                        boat.invalid = true;
                    } else {
                        j = 0;
                        while (j != boat.length && !UI.getPlayer(n).getHitOrMiss(x + j, y)) {
                            j++;
                        }
                        if (j != boat.length) {
                            boat.invalid = true;
                        } else {
                            boat.y2 = y;
                            boat.x2 = x + boat.length;
                            for (i = x; i < boat.x2; i++) {
                                UI.getPlayer(n).setHitOrMiss(i, y, true);
                                UI.getPlayer(n).setWhatShip(i, y, UI.getShips(u));
                            }
                            boat.invalid = false;
                        }
                    }
                    break;
            }
        } while (boat.invalid);
        return boat;
    }
}
