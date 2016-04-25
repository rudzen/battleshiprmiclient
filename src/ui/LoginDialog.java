package ui;

import interfaces.IBattleShip;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JProgressBar;
import javax.swing.JPasswordField;
import javax.swing.JLabel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.io.Serializable;

/**
 * Simple login dialog.<br>
 * Communicates with BattleShipRMIServer.
 *
 * @author rudz
 */
public final class LoginDialog extends JDialog implements Serializable {

    private static final long serialVersionUID = 1L;

    private static JPanel contentPanel;
    private static JPasswordField passwordField;
    private static JTextField textField;
    private static JButton okButton;
    private static final JProgressBar progressBar = new JProgressBar();

    private UI ui;
    
    
    public static JDialog login(final IBattleShip game, final UI ui) {
        try {
            LoginDialog dialog = new LoginDialog(game, ui);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
            return dialog;
        } catch (Exception e) {
        }
        return null;
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public LoginDialog(final IBattleShip game, final UI ui) {
        super();
        setAlwaysOnTop(true);
        setType(Type.POPUP);
        setFocusableWindowState(true);
        setTitle("Battleship login");
        setModal(true);
        setIconImage(Toolkit.getDefaultToolkit().getImage(LoginDialog.class.getResource("/com/sun/java/swing/plaf/windows/icons/Warn.gif")));
        setBounds(100, 100, 241, 167);
        getContentPane().setLayout(new BorderLayout());
        contentPanel = new JPanel();
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);

        passwordField = new JPasswordField();
        passwordField.setBounds(66, 42, 149, 20);
        contentPanel.add(passwordField);

        textField = new JTextField();
        textField.setBounds(66, 11, 149, 20);
        contentPanel.add(textField);
        textField.setColumns(10);
        textField.addKeyListener(new KeyTypeListener());

        JLabel lblUser = new JLabel("User");
        lblUser.setBounds(10, 14, 46, 14);
        contentPanel.add(lblUser);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(10, 45, 46, 14);
        contentPanel.add(lblPassword);

        progressBar.setIndeterminate(true);
        progressBar.setBounds(10, 73, 205, 14);
        progressBar.setVisible(false);

        contentPanel.add(progressBar);
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);
        okButton = new JButton("OK");
        okButton.setActionCommand("OK");
        okButton.setEnabled(false);
        okButton.addActionListener(new OkActionImpl(ui));
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((final ActionEvent e) -> {
            closeThis(this);
        });
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
        setLocationRelativeTo(null);
    }

    public static void closeThis(JDialog dialog) {
        dialog.setVisible(false);
        dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * This class will listen for key strokes.<br>
     * It will set the OK button to enabled if valid input is detected.
     */
    private static class KeyTypeListener implements KeyListener {

        @Override
        public void keyTyped(KeyEvent e) {
            okButton.setEnabled(!textField.getText().trim().isEmpty());
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }

    private static class OkActionImpl implements ActionListener {

        private final UI ui;
        
        public OkActionImpl(final UI ui) {
            this.ui = ui;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* this is where the dialog will send the information to the server. */
            boolean lastState = okButton.isEnabled();
            okButton.setEnabled(false);

            progressBar.setVisible(true);
            
            //Statics.isLoggedIn = game.login(user, pw);

            ui.updateUser(textField.getText(), new String(passwordField.getPassword()));

            progressBar.setVisible(true);

            
            
            
            /* the text is concatted and BASE64 encoded before, as it will be decoded on the server side */
//            if (Login.attempt_login(textField.getText().trim(), new String(passwordField.getPassword()))) {
//                System.out.println("Du er logget ind som : " + LoginData.b.getBrugernavn());
//                HangManClient.runGame();
//                closeThis();
//            } else {
//                passwordField.setText(null);
//                progressBar.setVisible(false);
//            }
            okButton.setEnabled(lastState);
        }
    }
}
