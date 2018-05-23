/*
  The MIT License

  Copyright 2016, 2017, 2018 Rudy Alex Kohn.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/

package ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Old / Temporary / Static listener classes for UI
 *
 * @author Rudy Alex Kohn s133235@student.dtu.dk
 */
public class Listeners {



    /**
     * Listener for the Ping menu option
     */
    public static class PingListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Pinging server..");
            try {
                UI.getInstance().game.ping(UI.getInstance(), System.currentTimeMillis());
            } catch (RemoteException ex) {
                Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /* Listener for Options menu */
    public static class OptionsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            UIHelpers.messageDialog("This feature has been disabled until further notice.", "Options");

//                if (Options.opts == null) {
//                    options.setup(ui);
//                } else {
//                    options.setVisible(true);
//                }
        }
    }

}
