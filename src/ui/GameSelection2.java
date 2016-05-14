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

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Locale;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
public class GameSelection2 {

    private final DefaultListModel listModel;
    private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);

    private final JFrame frame = new JFrame();
    private final JPanel panel = new JPanel();
    private final JScrollPane listScrollPane = new JScrollPane();
    private final JList<String> rowList = new JList<>();

    /**
     * Create lobby button
     */
    private final JButton btnCreateLobby;

    /**
     * Cancel button
     */
    private final JButton btnCancel;

    /**
     * Join lobby button
     */
    private final JButton btnJoinLobby;

    public GameSelection2() {

        /* configure the buttons */
        btnCreateLobby = new JButton("Create Lobby");
        btnCreateLobby.addActionListener(new CreateLobby());
        btnCreateLobby.setToolTipText("Create a new lobby");

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new Cancel());
        btnCancel.setToolTipText("Cancel process of selecting");

        btnJoinLobby = new JButton("Join");
        btnJoinLobby.addActionListener(new JoinLobby());
        btnJoinLobby.setToolTipText("Join selected lobby");

        /* configure the listmodel */
        listModel = new DefaultListModel();
        //listModel.addElement(String.format("%sStart.", getTimeString()));
        rowList.setModel(listModel);

        /* configure listrow */
        rowList.setFont(new java.awt.Font("Courier New", 0, 10));
        rowList.setVisibleRowCount(10); // default set to 10!
        listScrollPane.setViewportView(rowList);

        /* configure panel */
        panel.setLayout(new BorderLayout());
        panel.add(listScrollPane, BorderLayout.NORTH);
        panel.add(btnCreateLobby, BorderLayout.SOUTH);
        

        /* configure frame (window) */
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.setLocationByPlatform(true);
        frame.setTitle("Lobby selection :: by Rudy Alex Kohn (s133235)");
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(GameSelection.class.getResource("/com/sun/java/swing/plaf/windows/icons/Question.gif")));
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * Change the visibility of the classes frame
     *
     * @param value If true, frame will be set to visible, otherwise it will be
     * hidden
     */
    public void setVisible(final boolean value) {
        frame.setVisible(value);
    }

    /**
     * Adds a string to the output display, if needed, just cast the output to
     * String
     *
     * @param text The string to add to output
     */
    public void addToList(final String text) {
        if (!"".equals(text.trim())) {
            try {
                listModel.addElement(text);
                int lastIndex = listModel.getSize() - 1;
                if (lastIndex >= 0) {
                    rowList.setSelectedIndex(lastIndex);
                    rowList.ensureIndexIsVisible(lastIndex);
                }
            } catch (final OutOfMemoryError e) {
                listModel.clear();
            }
        }
    }

    /**
     * Clears the list of lobbys and hides the window
     */
    public void closeit() {
        rowList.clearSelection();
        listModel.clear();
        setVisible(false);
    }

    /**
     * ActionListener for Create lobby button
     */
    private class CreateLobby implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            UI.createLobby();
            closeit();
        }
    }

    private class Cancel implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            closeit();
        }
    }

    private class JoinLobby implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int lobbyID;
            try {
                lobbyID = Integer.valueOf(rowList.getSelectedValue().trim().split(":")[0].trim());
            } catch (final NumberFormatException nfe) {
                lobbyID = 0;
            }
            System.out.println("Lobby ID selected : " + lobbyID);
            if (lobbyID > 0) {
                UI.joinLobby(lobbyID);
            } else {
                UIHelpers.messageDialog("Error while parsing lobby ID", "Error in GameSelection", JOptionPane.ERROR_MESSAGE);
            }
            closeit();
        }
    }
}
