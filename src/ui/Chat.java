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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import interfaces.IChatClient;
import ui.components.SwingJList;
import utility.Statics;

/**
 * Simple chat system to chat with other RMI clients.
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
@SuppressWarnings("serial")
public final class Chat extends UnicastRemoteObject implements IChatClient {

    private JLabel ip;
    private JLabel name;
    private JTextField tf;
    private SwingJList<String> listUsers;
    private SwingJList<String> listChat;
    private JFrame frame;
    private JButton bt;

    static Chat instance;

    public static Chat getInstance() throws RemoteException {
        if (instance == null) {
            instance = new Chat();
        }
        return instance;
    }

    private final DefaultListModel<String> userModel = new DefaultListModel<>();
    private final DefaultListModel<String> chatModel = new DefaultListModel<>();

    private void putMessage(final String name, final String message) {
        chatModel.addElement(Statics.getTimeString() + " > [" + name + "] >" + message);
        final int lastIndex = chatModel.getSize() - 1;
        if (lastIndex >= 0) {
            listChat.setSelectedIndex(lastIndex);
            listChat.ensureIndexIsVisible(lastIndex);
        }
        bt.requestFocus();
    }

    private Chat() throws RemoteException {
        super();

        frame = new JFrame("Battleship Chat v0.1");
        final JPanel main = new JPanel();
        final JPanel top = new JPanel();
        final JPanel cn = new JPanel();
        final JPanel bottom = new JPanel();
        ip = new JLabel();
        tf = new JTextField();
        name = new JLabel();
        bt = new JButton("Send");
        bt.setEnabled(false);
        listUsers = new SwingJList<>();
        listChat = new SwingJList<>();
        listUsers.setModel(userModel);
        listChat.setModel(chatModel);
        main.setLayout(new BorderLayout(5, 5));
        top.setLayout(new GridLayout(1, 0, 5, 5));
        cn.setLayout(new BorderLayout(5, 5));
        bottom.setLayout(new BorderLayout(5, 5));
        top.add(new JLabel("User: "));
        name.setText(UI.getUser());
        top.add(name);
        top.add(new JLabel("Host Address: "));
        ip.setText(Statics.lastRegistry);
        top.add(ip);
        //top.add(connect);
        cn.add(new JScrollPane(listChat), BorderLayout.CENTER);
        cn.add(new JScrollPane(listUsers), BorderLayout.EAST);
        bottom.add(tf, BorderLayout.CENTER);
        bottom.add(bt, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);
        main.add(cn, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(final KeyEvent e) {
                bt.setEnabled(!tf.getText().trim().isEmpty());
            }

            @Override
            public void keyPressed(final KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    bt.doClick();
                }
            }

            @Override
            public void keyReleased(final KeyEvent e) {

            }
        });

        bt.addActionListener((ActionEvent e) -> {
            try {
                UI.getInstance().game.sendMessage(this, UI.getUser(), tf.getText());
                tf.setText("");
                bt.setEnabled(false);
            } catch (final RemoteException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        frame.setContentPane(main);
        frame.pack();
        frame.setSize(800, 600);
    }

    public void setVisibility(final boolean visible) {
        frame.setVisible(visible);
    }

    public boolean isVisible() {
        return frame.isVisible();
    }

    @Override
    public void newMessage(final String name, final String message) throws RemoteException {
        putMessage(name, message);
    }

    @Override
    public void getAllUsers(final ArrayList<String> users) throws RemoteException {
        if (users != null) {
            userModel.removeAllElements();
            users.stream().forEach((s) -> {
                userModel.addElement(s);
            });
        }
    }

    @Override
    public void clearAll() throws RemoteException {
        chatModel.removeAllElements();
        userModel.removeAllElements();
    }

}
