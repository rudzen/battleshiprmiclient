package dataobjects;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import ui.UI;
import utility.Statics;

/**
 * Listener for the attack clicks on the board.
 * @author rudz
 */
public class AttackListener implements ActionListener {

    private final UI ui;
    
    public AttackListener(final UI ui) {
        this.ui = ui;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UI.getPlayer(Statics.you).humanAttack(e);
    }
}
