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
 * Align to the center.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office",
        id = "org.joeffice.desktop.actions.CenterAlignAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/text_align_center.png",
        displayName = "#CTL_CenterAlignAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Desktop/Alignment", position = 200)})
@Messages("CTL_CenterAlignAction=Center Align")
public final class CenterAlignAction extends AbstractAction {

    private Styleable styleable;

    public CenterAlignAction(Styleable styleable) {
        this.styleable = styleable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttributedString attributes = new AttributedString("Center align");
        attributes.addAttribute(ExtraTextAttribute.ALIGNMENT, ExtraTextAttribute.ALIGNMENT_CENTER);
        styleable.setFontAttributes(attributes);
    }
}
