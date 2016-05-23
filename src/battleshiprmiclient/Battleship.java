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

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.RMISocketFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import interfaces.IBattleShip;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import ui.UI;
import utility.Statics;

/**
 * The main launcher class for the Battleship RMI client
 *
 * @author Rudy Alex Kohn s133235@student.dtu.dk
 */
public class Battleship {

    public static void main(String[] args) {

        if (args.length >= 1) {
            Statics.lastRegistry = args[0];
        } else {
            Statics.lastRegistry = "localhost";
        }

        try {
            new Battleship(Statics.lastRegistry);
        } catch (RemoteException ex) {
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Load the properties.
     */
    private void loadProperties() {

        Properties props = new Properties();
        InputStream is;

        try {
            File f = new File(Statics.PROPERTIES);
            is = new FileInputStream(f);
        } catch (final Exception e) {
            is = null;
        }

        try {
            if (is == null) {
                is = getClass().getResourceAsStream(Statics.PROPERTIES);
            }
            props.load(is);
        } catch (final Exception e) { }

        Statics.lastUser = props.getProperty("lastUser", "");
        Statics.lastPassword = props.getProperty("lastPassword", "");
        Statics.lastRegistry = props.getProperty("lastRegistry", "localhost");

        //registry = "212.60.120.4"; // own ip // port - 2158
        //registry = "130.226.195.22"; // studentermaskine //registry = "ubuntu4.javabog.dk";
    }

    public static IBattleShip game;

    public Battleship() throws RemoteException {
        super();
    }

    public Battleship(final String registry) throws RemoteException {
        super();
        ClientTwoWaySocketFactory fac;
        try {
            loadProperties();
            int port = 6769; // default port

            if (System.getSecurityManager() == null) {
                System.setSecurityManager(new SecurityManager());
            }

            /* configure custom socket interface for RMI */
            fac = new ClientTwoWaySocketFactory();
            RMISocketFactory.setSocketFactory(fac);
            fac.establishSignallingChannel(registry, port);

            /* Lookup the service in the registry, and obtain a remote service */
            Remote server = Naming.lookup("rmi://" + registry + ':' + Integer.toString(port) + "/Battleship");
            game = (IBattleShip) server;

            UI.setInstance(new UI(game, port));

        } catch (final RemoteException re) {
            System.err.println("RMI Error - RemoteException()\n" + re.getMessage());
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, re);
        } catch (final NotBoundException ex) {
            System.err.println("No game server available - NotBountException()");
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        } catch (final MalformedURLException ex) {
            System.err.println("No game server available - MalformedURLException()");
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.err.println("I/O error - IOException()");
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            System.err.println("General error - Exception()");
            Logger.getLogger(Battleship.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
