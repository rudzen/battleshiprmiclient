/*
 * The MIT License
 *
 * Copyright 2016 rudz.
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
package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * Options window :-)
 *
 * @author rudz
 */
public class Options extends JFrame {

    private static final long serialVersionUID = 5803514402084529738L;

    /* used for options menu */
    static JLabel title;
    public static JPanel opts;
    /* options window close button */
    private static final JButton DONE = new JButton("Done");

    private static final String[] LAYOUT = {"Manual", "Automatic"};
    private static final String[] sCOLOURS = {"Cyan", "Green", "Yellow", "Magenta", "Pink", "Red", "White"};

    /* colours for options */
    public static final Color[] COLOURS = {Color.cyan, Color.green, Color.yellow, Color.magenta, Color.pink, Color.red, Color.white};

    /* options menu */
    static final JComboBox SHIP_LAYOUT = new JComboBox(LAYOUT);
    static final JComboBox SHIP_COLOUR = new JComboBox(sCOLOURS);

    /* keeps track of previous values for : color / first player / layout */
    private static int prevcolor = 0;
    private static int prevLayout = 0;

    public Options(final String title) {
        super(title);
    }

    public void setup(final UI ui) {
        opts = new JPanel(new GridLayout(4, 2));
        title = new JLabel("Ship Layout");
        opts.add(title);
        SHIP_LAYOUT.setSelectedIndex(0);
        opts.add(SHIP_LAYOUT);
        title = new JLabel("Ship Color");
        opts.add(title);
        SHIP_COLOUR.addActionListener(new SColorListener(ui));
        SHIP_COLOUR.setSelectedIndex(0);
        opts.add(SHIP_COLOUR);
        getContentPane().add(opts, BorderLayout.CENTER);
        //options.setSize(600,800);
        setResizable(false);
        DONE.addActionListener(new DoneListener());
        getContentPane().add(DONE, BorderLayout.SOUTH);
        setLocation(200, 200);
        pack();
        setVisible(true);
    }

    private class DoneListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            UIHelpers.messageDialog("Changes will take place at the start of a new game.", "", JOptionPane.PLAIN_MESSAGE);
            if (SHIP_LAYOUT.getSelectedIndex() != prevLayout) {
                prevLayout = SHIP_LAYOUT.getSelectedIndex();
            }
            dispose();
        }
    }

    //Listener for the Colors combo box		
    private class SColorListener implements ActionListener {

        private final WeakReference<UI> weakReference;

        public SColorListener(final UI ui) {
            weakReference = new WeakReference<>(ui);
        }

        @Override
        public void actionPerformed(ActionEvent v) {
            final UI ui = weakReference.get();
            if (ui != null) {
                for (int i = 0; i < 10; i++) {
                    for (int j = 0; j < 10; j++) {
                        if (ui.get[0].getBboard(i, j).getBackground() == COLOURS[prevcolor]) {
                            PLAYERS[0].setBboard(i, j, COLOURS[SHIP_COLOUR.getSelectedIndex()]);
                        }
                        /*
                        if (PLAYERS[enemy].getBboard(i, j).getBackground() == color[prevcolor]) {
                            PLAYERS[enemy].setBboard(i, j, color[shipColor.getSelectedIndex()]);
                        }
                         */
                    }
                }
                prevcolor = SHIP_COLOUR.getSelectedIndex();
            }
        }
    }
}
