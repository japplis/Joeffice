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

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.Element;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.rtf.RTFEditorKit;
import org.openide.util.Exceptions;

/**
 * Creates a transferable for the selected part of the document.
 *
 * @author Anthony Goubard - Japplis
 */
public class TransferableRichText implements Transferable {

    public static final DataFlavor RTF_FLAVOR = new DataFlavor("text/rtf", "RTF Text");
    public static final DataFlavor HTML_FLAVOR = new DataFlavor("text/html", "HTML Text"); // There is not text with html flavor

    private DataFlavor[] flavors = {RTF_FLAVOR, DataFlavor.stringFlavor};

    private Document doc;
    private int start;
    private int length;
    private String cachedPlainText;
    private String cachedRtfText;
    private String cachedHtmlText;

    public TransferableRichText(Document doc, int start, int length) {
        this.doc = doc;
        this.start = start;
        this.length = length;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        for (DataFlavor supportedFlavor : flavors) {
            if (supportedFlavor.isMimeTypeEqual(flavor)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (flavor.equals(DataFlavor.stringFlavor)) {
            if (cachedPlainText != null) {
                return cachedPlainText;
            }
            try {
                cachedPlainText = doc.getText(start, length);
                return cachedPlainText;
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
                return "";
            }
        } else if (flavor.equals(RTF_FLAVOR)) {
            if (cachedRtfText != null) {
                return getTransferableText(cachedRtfText, flavor);
            }
            EditorKit rtfKit = new RTFEditorKit();
            cachedRtfText = convertWithKit(rtfKit);
            return getTransferableText(cachedRtfText, flavor);
        } else if (flavor.equals(HTML_FLAVOR)) {
            if (cachedHtmlText != null) {
                return getTransferableText(cachedHtmlText, flavor);
            }
            EditorKit htmlKit = new HTMLEditorKit();
            cachedHtmlText = convertWithKit(htmlKit);
            return getTransferableText(cachedHtmlText, flavor);
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    private String convertWithKit(EditorKit editorKit) {
        try {
            Document kitDocument = editorKit.createDefaultDocument();
            List<DocumentElement> selectedElements = getSelectedElements(doc.getRootElements()[0]);
            for (int i = selectedElements.size() - 1; i >= 0; i--) {
                DocumentElement elem = selectedElements.get(i);
                kitDocument.insertString(0, elem.text, elem.attributes); // Doesn't work when attributes not null
            }
            ByteArrayOutputStream output = new ByteArrayOutputStream(length * 2);
            editorKit.write(output, kitDocument, 0, kitDocument.getLength());
            String valueAsString = output.toString(Charset.defaultCharset().name());
            return valueAsString;
        } catch (BadLocationException | IOException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    private List<DocumentElement> getSelectedElements(Element... elements) throws BadLocationException {
        List<DocumentElement> selectedElements = new ArrayList<>();
        for (Element elem : elements) {
            if (!elem.isLeaf()) {
                Element[] children = new Element[elem.getElementCount()];
                for (int i = 0; i < children.length; i++) {
                    children[i] = elem.getElement(i);
                }
                List<DocumentElement> childElements = getSelectedElements(children);
                selectedElements.addAll(childElements);
                continue;
            }
            int elemStart = elem.getStartOffset();
            int elemEnd = elem.getEndOffset();

            if (elemStart > start && elemEnd < start + length) { // Element is completly inside the selection
                DocumentElement part = new DocumentElement();
                part.attributes = elem.getAttributes();
                part.text = doc.getText(elemStart, elemEnd - elemStart);
                selectedElements.add(part);
            } else if (elemStart > start && elemStart < start + length) { // Element has the end of the selection
                DocumentElement part = new DocumentElement();
                part.attributes = elem.getAttributes();
                part.text = doc.getText(elemStart, start + length - elemStart);
                selectedElements.add(part);
                return selectedElements;
            } else if (elemEnd > start && elemEnd < start + length) { // Element has the start of the selection
                DocumentElement part = new DocumentElement();
                part.attributes = elem.getAttributes();
                part.text = doc.getText(start, elemEnd - start);
                selectedElements.add(part);
            } else if (start >= elemStart && start + length <= elemEnd) { // Element has the whole selection
                DocumentElement part = new DocumentElement();
                part.attributes = elem.getAttributes();
                part.text = doc.getText(start, length);
                selectedElements.add(part);
                return selectedElements;
            }
        }
        return selectedElements;
    }

    private Object getTransferableText(String text, DataFlavor flavor) {
        if (flavor.getDefaultRepresentationClass() == InputStream.class) {
            String charsetName = flavor.getParameter("charset") == null ? "UTF-8" : flavor.getParameter("charset");
            return new ByteArrayInputStream(text.getBytes(Charset.forName(charsetName)));
        } else if (flavor.getDefaultRepresentationClass() == Reader.class) {
            return new StringReader(text);
        } else {
            return text;
        }
    }

    private class DocumentElement {

        private AttributeSet attributes;
        private String text;
    }
}
