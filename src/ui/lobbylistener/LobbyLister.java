package ui.lobbylistener;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import ui.UI;
import ui.UIHelpers;

/**
 * Original
 * @author ashraf_sarhan
 * 
 * Heavy modified to fit for consimption :)
 * @author Rudy Alex Kohn s133235@student.dtu.dk
 */
@SuppressWarnings("serial")
public class LobbyLister {

    private static LobbyLister instance;
    private static JFrame frame;
    private static SwingJList<String> swingJList;
    private static JTextField nameField;
    private static JButton createButton;
    private static JButton joinButton;
    private static JButton cancelButton;
    private static JScrollPane listScrollPane;
    private static JPanel buttonPane;
    private static JSplitPane splitPane;

    public static LobbyLister getInstance() {
        if (instance == null) {
            instance = new LobbyLister();
        }
        return instance;
    }

    public LobbyLister() {
        SwingUtilities.invokeLater(() -> {
            try {
                createAndShowGUI();
            } catch (Exception e) {
                System.err.println("Error while opening LobbyListener");
                System.err.println(e.getMessage());
            }
        });
    }

    private static void createAndShowGUI() throws Exception {

        frame = new JFrame("Battleship Lobby Lister");
        frame.setSize(new Dimension(500, 400));
        frame.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage(LobbyLister.class.getResource("/com/sun/java/swing/plaf/windows/icons/Question.gif")));
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setResizable(false);

        // Create JList with a List of String names
        swingJList = new SwingJList<>();

        nameField = new JTextField(10);

        swingJList.addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                String selectedName = swingJList.getSelectedValue();
                nameField.setText(selectedName);
            }
        });

        createButton = new JButton("Create");
        createButton.addActionListener(new CreateLobby());

        // Create an action listener to add a new item to the List
        joinButton = new JButton("Join");
        joinButton.addActionListener(new JoinLobby());
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent e) -> {
            frame.setVisible(false);
        });

        // Put the JList in a JScrollPane to handle scrolling
        listScrollPane = new JScrollPane(swingJList);
        listScrollPane.setPreferredSize(new Dimension(250, 180));

        listScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Current lobbies", TitledBorder.CENTER, TitledBorder.TOP));

        // Create a control panel
        buttonPane = new JPanel();
        buttonPane.add(createButton);
        buttonPane.add(nameField);
        buttonPane.add(joinButton);
        buttonPane.add(cancelButton);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, listScrollPane, buttonPane);
        splitPane.setDividerLocation(180);
        splitPane.setEnabled(false);

        frame.add(splitPane);
        frame.pack();
        frame.setLocationRelativeTo(UI.getInstance().mainFrame);
        //frame.setVisible(true);

    }

    public static void setLookAndFeel(String lf) throws Exception {
        // Set Nimbus as L&F
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if (lf.equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            // If Nimbus is not available, you can set the GUI the system default L&F.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
    }

    /**
     * Adds a string to the list located inside the frame.
     *
     * @param text The text of the lobby entry, format is ID:PlayerName
     */
    public static void addToList(final String text) {
        if (text.trim() != null && !text.trim().isEmpty()) {
            try {
                swingJList.addElement(text);
            } catch (final OutOfMemoryError e) {
                /* This is just a extra security for potential exception.
                  Should actually never happend (the server would be insane if it did!) */
                clearAll();
            }
//            int lastIndex = swingJList.get .getSize() - 1;
//            if (lastIndex >= 0) {
//                /* Keeps the selection at the last selected index */
//                swingJList.setSelectedIndex(lastIndex);
//                swingJList.ensureIndexIsVisible(lastIndex);
//            }
        }
    }

    /**
     * ActionListener for Create lobby button
     */
    private static class CreateLobby implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent arg0) {
            UI.createLobby();
            frame.setVisible(false);
        }
    }

    private static class JoinLobby implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            int lobbyID;
            try {
                lobbyID = Integer.valueOf(swingJList.getSelectedValue().trim().split(":")[0].trim());
            } catch (final NumberFormatException nfe) {
                lobbyID = 0;
            }
            System.out.println("Lobby ID selected : " + lobbyID);
            if (lobbyID > 0) {
                UI.joinLobby(lobbyID);
            } else {
                UIHelpers.messageDialog("Error while parsing lobby ID", "Error in GameSelection", JOptionPane.ERROR_MESSAGE);
            }
            frame.setVisible(false);
        }
    }

    public static void clearAll() {
        swingJList.removeAll();
    }

    public static void setVisibility(boolean value) {
        frame.setVisible(value);
    }

    public static void setTitle(final String text) {
        frame.setTitle(text);
    }

}
