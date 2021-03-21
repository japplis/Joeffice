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
 * Justified the paragraph.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office",
        id = "org.joeffice.desktop.actions.JustifyAlignAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/text_align_justify.png",
        displayName = "#CTL_JustifyAlignAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Desktop/Alignment", position = 400)})
@Messages("CTL_JustifyAlignAction=Justify Align")
public final class JustifyAlignAction extends AbstractAction {

    private Styleable styleable;

    public JustifyAlignAction(Styleable styleable) {
        this.styleable = styleable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttributedString attributes = new AttributedString("Justify align");
        attributes.addAttribute(TextAttribute.JUSTIFICATION, TextAttribute.JUSTIFICATION_FULL);
        styleable.setFontAttributes(attributes);
    }
}
