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

import com.css.rmi.ClientTwoWaySocketFactory;
import interfaces.IBattleShip;
import interfaces.IClientListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import ui.UI;
import ui.UIHelpers;

/**
 * The main launcher class for the Battleship RMI client
 *
 * @author Rudy Alex Kohn s133235@student.dtu.dk
 */
public class Battleship {

    public static void main(String args[]) {

        String registry;
        if (args.length >= 1) {
            registry = args[0];
        } else {
            //registry = "212.60.120.4";
            registry = "localhost";
        }

        try {
            new Battleship(registry);
        } catch (RemoteException ex) {
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //private UI ui;
    public static IBattleShip game;

    public Battleship() throws RemoteException {
    }

    public Battleship(final String registry) throws RemoteException {
        ClientTwoWaySocketFactory fac = null;
        try {

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            /* configure custom socket interface for RMI */
            fac = new ClientTwoWaySocketFactory();
            RMISocketFactory.setSocketFactory(fac);
            fac.establishSignallingChannel(registry, 6769);

            /* Lookup the service in the registry, and obtain a remote service */
            Remote server = Naming.lookup("rmi://" + registry + ":6769/Battleship");
            game = (IBattleShip) server;

            IClientListener ui = new UI(registry, game);

        } catch (final RemoteException re) {
            UIHelpers.messageDialog("RMI Error - RemoteException()\n" + re.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, re);
        } catch (final NotBoundException ex) {
            UIHelpers.messageDialog("No game server available - NotBountException()", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final MalformedURLException ex) {
            UIHelpers.messageDialog("No game server available - MalformedURLException()", "Error", JOptionPane.ERROR_MESSAGE);
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
