package org.joeffice.desktop.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import org.openide.awt.*;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 * Drop down action to choose the alignment.
 *
 * @author Anthony Goubard - Japplis
 */
// Icons from http://famfamfam.com/
@ActionID(
        category = "Edit/Office",
        id = "org.joeffice.desktop.actions.AlignmentAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/text_align_center.png",
        displayName = "#CTL_Alignement",
        lazy = false)
@ActionReference(path = "Toolbars/Font", position = 3500)
@NbBundle.Messages("CTL_Alignement=Align")
public class AlignmentAction extends AbstractAction implements Presenter.Toolbar {

    public final static String EXTENSION_POINT = "Office/Desktop/Alignment";

    public AlignmentAction() {
        super(NbBundle.getMessage(AlignmentAction.class, "CTL_Alignement"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO click detect what to align
    }

    @Override
    public Component getToolbarPresenter() {
        JPopupMenu popup = new JPopupMenu();
        for (Action a : Utilities.actionsForPath(EXTENSION_POINT)) {
            popup.add(a);
        }
        ImageIcon topIcon = ImageUtilities.loadImageIcon("org/joeffice/desktop/actions/text_align_center.png", false);
        return DropDownButtonFactory.createDropDownButton(topIcon, popup);
    }
}
