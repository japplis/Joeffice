/*
 * Copyright 2023 Japplis.
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

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.joeffice.desktop.actions.EditorStyleable;

/**
 * The word processor component.
 * This component doesn't depend on NetBeans framework classes.
 *
 * @author Anthony Goubard - Japplis
 */
public class WordProcessorComponent extends JTextPane {

    private EditorStyleable styleable;
    private XWPFDocument poiDocument;

    public WordProcessorComponent() {
        createComponent();
    }

    private void createComponent() {
        setEditorKit(new DocxEditorKit());
        styleable = new EditorStyleable(this);
        setTransferHandler(new RichTextTransferHandler());
        putClientProperty("print.printable", Boolean.TRUE);

        // Doesn't work
        setSize(new Dimension(545, Integer.MAX_VALUE));
        setPreferredSize(new Dimension(545, Integer.MAX_VALUE));
        setMaximumSize(new Dimension(545, Integer.MAX_VALUE));
    }

    public XWPFDocument loadDocument(final File docxFile) throws Exception {
        try (FileInputStream docxIS = new FileInputStream(docxFile)) {
            setText("");
            getEditorKit().read(docxIS, getDocument(), 0);
            poiDocument = (XWPFDocument) getDocument().getProperty("XWPFDocument");
            return poiDocument;
        } catch (IOException | BadLocationException ex) {
            throw ex;
        }
    }

    public XWPFDocument getPOIDocument() {
        return (XWPFDocument) poiDocument;
    }

    public EditorStyleable getStyleable() {
        return styleable;
    }
}
