package org.joeffice.desktop.actions;

import java.awt.event.ActionEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import javax.swing.AbstractAction;

import org.joeffice.desktop.ui.Styleable;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Align to the left.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office",
        id = "org.joeffice.desktop.actions.LeftAlignAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/text_align_left.png",
        displayName = "#CTL_LeftAlignAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Desktop/Alignment", position = 100)})
@Messages("CTL_LeftAlignAction=Left Align")
public final class LeftAlignAction extends AbstractAction {

    private Styleable styleable;

    public LeftAlignAction(Styleable styleable) {
        this.styleable = styleable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttributedString attributes = new AttributedString("Left align");
        attributes.addAttribute(ExtraTextAttribute.ALIGNMENT, ExtraTextAttribute.ALIGNMENT_LEFT);
        styleable.setFontAttributes(attributes);
    }
}
