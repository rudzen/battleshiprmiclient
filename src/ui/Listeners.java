/*
 * Copyright 2016 Rudy Alex Kohn <s133235@student.dtu.dk>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
