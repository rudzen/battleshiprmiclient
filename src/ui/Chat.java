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

import interfaces.IChatClient;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCode;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import ui.components.SwingJList;
import utility.Statics;

/**
 * Simple chat system to chat with other RMI clients.
 *
 * @author Rudy Alex Kohn <s133235@student.dtu.dk>
 */
@SuppressWarnings("serial")
public final class Chat extends UnicastRemoteObject implements IChatClient {

    JLabel ip, name;
    JTextField tf;
    SwingJList lstUsers, lstChat;
    JFrame frame;
    JButton bt;
    
    static Chat instance;

    public static Chat getInstance() throws RemoteException {
        if (instance == null) {
            instance = new Chat();
        }
        return instance;
    }

    private final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
    private DefaultListModel<String> userModel = new DefaultListModel();
    private DefaultListModel<String> chatModel = new DefaultListModel();

    private void putMessage(final String name, final String message) {
        chatModel.addElement(getTimeString() + " > [" + name + "] >" + message);
    }

    private String getTimeString() {
        Calendar cal = Calendar.getInstance();
        return SDF.format(cal.getTime());
    }

    public Chat() throws RemoteException {

        frame = new JFrame("Battleship Chat v0.1");
        JPanel main = new JPanel();
        JPanel top = new JPanel();
        JPanel cn = new JPanel();
        JPanel bottom = new JPanel();
        ip = new JLabel();
        tf = new JTextField();
        name = new JLabel();
        bt = new JButton("Send");
        bt.setEnabled(false);
        lstUsers = new SwingJList();
        lstChat = new SwingJList();
        lstUsers.setModel(userModel);
        lstChat.setModel(chatModel);
        main.setLayout(new BorderLayout(5, 5));
        top.setLayout(new GridLayout(1, 0, 5, 5));
        cn.setLayout(new BorderLayout(5, 5));
        bottom.setLayout(new BorderLayout(5, 5));
        top.add(new JLabel("User: "));
        name.setText(UI.getInstance().getUser());
        top.add(name);
        top.add(new JLabel("Host Address: "));
        ip.setText(Statics.lastRegistry);
        top.add(ip);
        //top.add(connect);
        cn.add(new JScrollPane(lstChat), BorderLayout.CENTER);
        cn.add(new JScrollPane(lstUsers), BorderLayout.EAST);
        bottom.add(tf, BorderLayout.CENTER);
        bottom.add(bt, BorderLayout.EAST);
        main.add(top, BorderLayout.NORTH);
        main.add(cn, BorderLayout.CENTER);
        main.add(bottom, BorderLayout.SOUTH);
        main.setBorder(new EmptyBorder(10, 10, 10, 10));

        tf.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                bt.setEnabled(tf.getText().trim().length() > 0);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    bt.doClick();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        bt.addActionListener((ActionEvent e) -> {
            try {
                UI.getInstance().game.sendMessage(this, UI.getInstance().getUser(), tf.getText());
                tf.setText("");
                bt.setEnabled(false);
                tf.requestFocus();
            } catch (RemoteException ex) {
                Logger.getLogger(Chat.class.getName()).log(Level.SEVERE, null, ex);
            }
        });

        frame.setContentPane(main);
        frame.setSize(600, 600);
        //frame.setVisible(true);

    }

    public void setVisibility(boolean visible) {
        frame.setVisible(visible);
    }

    public boolean isVisible() {
        return frame.isVisible();
    }

    @Override
    public void newMessage(String name, String message) throws RemoteException {
        putMessage(name, message);
    }

    @Override
    public void getAllUsers(ArrayList<String> users) throws RemoteException {
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
