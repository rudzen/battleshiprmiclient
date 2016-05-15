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

/**
 * UI for selecting lobby to join
 *
 * @author Rudy Alex Kohn s133235@student.dtu.dk
 */
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JList;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.Toolkit;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

/**
 * GameSelection class, does nothing more than showing the lobbies and offers
 * the chance to create a new lobby.
 * <p>
 * If the action requires it, it will call the corresponding method in the main
 * UI, since the main UI holds the needed objects to complete the task.
 *
 * @author Rudy Alex Kohn (s133235@student.dtu.dk)
 */
@SuppressWarnings("serial")
public class GameSelection extends JFrame {

    // TODO : Scrollpanel in case of many loobys
    /**
     * Main content panel
     */
    private final JPanel contentPane;

    /**
     * The list of strings to show the lobby text in
     */
    private final JList<String> list = new JList<>();

    private final JPanel listPanel = new JPanel();
    
    
    /**
     * The model to hold the strings
     */
    private final DefaultListModel listModel = new DefaultListModel();

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

    /**
     * Main constructor of GameSelection class, it will configure the UI bits
     * and bobs.
     */
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public GameSelection() {
        
        setTitle("Current lobbys to join");
        setResizable(false);
        setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
        setIconImage(Toolkit.getDefaultToolkit().getImage(GameSelection.class.getResource("/com/sun/java/swing/plaf/windows/icons/Question.gif")));
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setBounds(100, 100, 380, 290);
        
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        list.setModel(listModel);
        list.setToolTipText("Current lobbys available");
        list.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnCreateLobby = new JButton("Create Lobby");
        btnCreateLobby.addActionListener(new CreateLobby());
        btnCreateLobby.setToolTipText("Create a new lobby");

        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new Cancel());
        btnCancel.setToolTipText("Cancel process of selecting");

        btnJoinLobby = new JButton("Join");
        btnJoinLobby.addActionListener(new JoinLobby());
        btnJoinLobby.setToolTipText("Join selected lobby");
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                .addComponent(list, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 354, GroupLayout.PREFERRED_SIZE)
                                .addGroup(gl_contentPane.createSequentialGroup()
                                        .addComponent(btnCreateLobby)
                                        .addPreferredGap(ComponentPlacement.RELATED, 135, Short.MAX_VALUE)
                                        .addComponent(btnJoinLobby)
                                        .addPreferredGap(ComponentPlacement.RELATED)
                                        .addComponent(btnCancel)))
                        .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                .addGroup(gl_contentPane.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(list, GroupLayout.PREFERRED_SIZE, 212, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(ComponentPlacement.RELATED)
                        .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                .addComponent(btnCreateLobby)
                                .addComponent(btnCancel)
                                .addComponent(btnJoinLobby))
                        .addContainerGap())
        );
        contentPane.setLayout(gl_contentPane);
    }

    /**
     * Adds a string to the list located inside the frame.
     *
     * @param text The text of the lobby entry, format is ID:PlayerName
     */
    public void addToList(final String text) {
        if (!"".equals(text.trim())) {
            try {
                listModel.addElement(text);
            } catch (final OutOfMemoryError e) {
                /* This is just a extra security for potential exception.
                  Should actually never happend (the server would be insane if it did!) */
                listModel.clear();
            }
            int lastIndex = listModel.getSize() - 1;
            if (lastIndex >= 0) {
                /* Keeps the selection at the last selected index */
                list.setSelectedIndex(lastIndex);
                list.ensureIndexIsVisible(lastIndex);
            }
        }
    }

    
    public void clearAll() {
        listModel.clear();
    }
    
    /**
     * Clears the list of lobbys and hides the window
     */
    public void closeit() {
        list.clearSelection();
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
                lobbyID = Integer.valueOf(list.getSelectedValue().trim().split(":")[0].trim());
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
