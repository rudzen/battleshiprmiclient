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
package battleshiprmiclient;

import com.google.gson.Gson;
import dataobjects.Player;
import interfaces.IBattleShip;
import interfaces.IClientListener;
import interfaces.IShip;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import ui.UI;
import ui.UIHelpers;

/**
 *
 * @author rudz
 */
public class Battleship {

    public static void main(String args[]) {

        // Only required for dynamic class loading
        //System.setSecurityManager(new RMISecurityManager());
        // Check to see if a registry was specified
        String registry;
        if (args.length >= 1) {
            registry = args[0];
        } else {
            registry = "localhost";
        }

        try {
            new Battleship(registry);
        } catch (RemoteException ex) {
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static final long serialVersionUID = 1L;

    private String username;

    private String registry;

    //private UI ui;
    private IBattleShip game;

    public Battleship() throws RemoteException {
    }

    public Battleship(final String registry) throws RemoteException {
        this.registry = registry;

        try {

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
                System.out.println("SecurityManager created.");
            }

            // Registration format
            //registry_hostname :port/service
            // Note the :port field is optional
            String registration = "rmi://" + registry + "/Battleship";
            /* Lookup the service in the registry, and obtain a remote service */
            Remote remoteService = Naming.lookup(registration);

            //UI.runGame(registry);
            UI ui = new UI(registry);

            game = (IBattleShip) remoteService;

            System.out.println("RMI SEEMS OKAY!");

            //game.login(ui.toString(), "password");

            Player p = new Player("Palle");
            p.initShips();

            Gson g = new Gson();
            game.registerClient(ui, g.toJson(p, Player.class));

            //game.fireShot(3, 5, new Player("abe"));
        } catch (final RemoteException re) {
            UIHelpers.messageDialog("RMI Error - RemoteException()\n" + re.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, re);
        } catch (final NotBoundException ex) {
            UIHelpers.messageDialog("No game server available - NotBountException()", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final MalformedURLException ex) {
            UIHelpers.messageDialog("No game server available - MalformedURLException()", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }

        /* Create a new game window and register it as a listener with remote game */
        //UI.runGame(registry);
    }

//    private void register() {
//        Player p = new Player("Palle");
//        p.initShips();
//        Gson g = new Gson();
//        
//        try {
//            game.registerClient(this, g.toJson(p, Player.class));
//        } catch (RemoteException ex) {
//            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
}
