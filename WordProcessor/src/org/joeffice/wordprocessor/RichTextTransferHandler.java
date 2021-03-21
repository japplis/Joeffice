/*
 * Copyright 2013 Japplis.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joeffice.wordprocessor;

import static javax.swing.TransferHandler.MOVE;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;

import org.joeffice.desktop.ui.OfficeTransferHandler;

import org.openide.util.Exceptions;

/**
 * Transfer handler that support rich text.
 * Supported MIME types are plain text, HTML and RTF.
 *
 * @author Anthony Goubard - Japplis
 */
public class RichTextTransferHandler extends OfficeTransferHandler {

    /**
     * Indicator for copy as text or paste as text.
     */
    private boolean asTextOnly;

    /**
     * The paste method used for DnD and clipboard.
     */
    @Override
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(new TransferSupport(c, t))) {
            JTextComponent textField = (JTextComponent) c;
            String rtfText = getTextFromTransferable(t, TransferableRichText.RTF_FLAVOR);
            if (rtfText != null && !asTextOnly) {
                addRichtText(rtfText, textField, new RTFEditorKit());
                return true;
            }
            String htmlText = getTextFromTransferable(t, TransferableRichText.HTML_FLAVOR);
            if (htmlText != null && !asTextOnly) {
                addRichtText(rtfText, textField, new HTMLEditorKit());
                return true;
            }
            String plainText = getTextFromTransferable(t, DataFlavor.stringFlavor);
            if (plainText != null) {
                try {
                    textField.getDocument().insertString(textField.getSelectionStart(), plainText, null);
                    return true;
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            ImageIcon image = getImageFromTransferable(t);
            if (image != null) {
                ((DocxDocument) textField.getDocument()).insertPicture(image, textField.getSelectionStart());
                return true;
            }
        }
        return false;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTextComponent textField = (JTextComponent) c;
        if (asTextOnly) {
            String text = textField.getSelectedText();
            return new StringSelection(text);
        } else {
            int selectionStart = Math.min(textField.getSelectionStart(), textField.getSelectionEnd());
            int selectionLength = Math.abs(textField.getSelectionEnd() - selectionStart);
            return new TransferableRichText(textField.getDocument(), selectionStart, selectionLength);
        }
    }

    // Doesn't seem to be called
    @Override
    public void exportDone(JComponent comp, Transferable transferable, int action) {
        if (action == MOVE) {
            JTextComponent textField = (JTextComponent) comp;
            try {
                int selectionStart = Math.min(textField.getSelectionStart(), textField.getSelectionEnd());
                int selectionLength = Math.abs(textField.getSelectionEnd() - selectionStart);
                textField.getDocument().remove(selectionStart, selectionLength);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private void addRichtText(String richText, JTextComponent textField, EditorKit richEditor) {
        try {
            StringReader reader = new StringReader(richText);
            Document doc = richEditor.createDefaultDocument();
            richEditor.read(reader, doc, 0);

            addRichText(textField, doc.getRootElements()[0]);
        } catch (IOException | BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    private void addRichText(JTextComponent textField, Element... elements) throws BadLocationException {
        for (int i = 0; i < elements.length; i++) {
            Element element = elements[i];
            if (element.isLeaf()) {
                String text = element.getDocument().getText(element.getStartOffset(), element.getEndOffset() - element.getStartOffset());
                textField.getDocument().insertString(textField.getSelectionStart(), text, element.getAttributes());
            } else {
                Element[] children = new Element[element.getElementCount()];
                for (int j = 0; j < children.length; j++) {
                    children[j] = element.getElement(j);
                }
                addRichText(textField, children);
            }
        }
    }

    public boolean isAsTextOnly() {
        return asTextOnly;
    }

    public void setAsTextOnly(boolean asTextOnly) {
        this.asTextOnly = asTextOnly;
    }
}
