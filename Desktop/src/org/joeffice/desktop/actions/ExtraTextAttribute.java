package org.joeffice.desktop.actions;

import java.awt.font.TextAttribute;
import java.text.AttributedCharacterIterator;
import javax.swing.text.StyleConstants;

/**
 * Contants to define any extra attributes that don't exist in TextAttribute.
 *
 * @see TextAttribute
 * @author Anthony Goubard - Japplis
 */
public class ExtraTextAttribute extends AttributedCharacterIterator.Attribute {

    public final static ExtraTextAttribute ALIGNMENT = new ExtraTextAttribute("Alignment");
    public final static int ALIGNMENT_LEFT = StyleConstants.ALIGN_LEFT;
    public final static int ALIGNMENT_RIGHT = StyleConstants.ALIGN_RIGHT;
    public final static int ALIGNMENT_CENTER = StyleConstants.ALIGN_CENTER;

    public final static ExtraTextAttribute INCREASE_FONT_SIZE = new ExtraTextAttribute("FontSizeIncrement");

    public final static ExtraTextAttribute INDENTATION = new ExtraTextAttribute("Indentation");

    public final static ExtraTextAttribute TEXT_TRANSFORM = new ExtraTextAttribute("TextTransform");

    public final static ExtraTextAttribute UI_ONLY = new ExtraTextAttribute("UIOnly");

    ExtraTextAttribute(String name) {
        super(name);
    }
}
