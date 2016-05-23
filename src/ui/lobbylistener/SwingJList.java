package ui.lobbylistener;

import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

/**
 * @author ashraf
 * @param <T>
 *
 */
@SuppressWarnings("serial")
public class SwingJList<T> extends JList<T> {

    public SwingJList(List<T> listData) {

        // Create a JList data model
        super(new DefaultListModel<>());

        listData.forEach(this::addElement);

        // Set selection mode
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    public SwingJList() {
        // Create a JList data model
        super(new DefaultListModel<>());

        // Set selection mode
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    }

    public void addElement(T element) {
        ((DefaultListModel<T>) getModel()).add(Constants.NEW_ELEMENT_IDX, element);
    }

    public void removeElement(Object element) {
        ((DefaultListModel<T>) getModel()).removeElement(element);
    }

    
    
    
}
