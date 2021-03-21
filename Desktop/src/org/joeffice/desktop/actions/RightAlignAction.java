package org.joeffice.desktop.actions;

import java.awt.event.ActionEvent;
import java.text.AttributedString;
import javax.swing.AbstractAction;

import org.joeffice.desktop.ui.Styleable;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Align to the right.
 * 
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office",
        id = "org.joeffice.desktop.actions.RightAlignAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/text_align_right.png",
        displayName = "#CTL_RightAlignAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Desktop/Alignment", position = 300)})
@Messages("CTL_RightAlignAction=Right Align")
public final class RightAlignAction extends AbstractAction {

    private Styleable styleable;

    public RightAlignAction(Styleable styleable) {
        this.styleable = styleable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttributedString attributes = new AttributedString("Right align");
        attributes.addAttribute(ExtraTextAttribute.ALIGNMENT, ExtraTextAttribute.ALIGNMENT_RIGHT);
        styleable.setFontAttributes(attributes);
    }
}
