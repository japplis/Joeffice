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

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;

import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.joeffice.desktop.actions.EditorStyleable;
import org.joeffice.desktop.file.OfficeDataObject;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.desktop.ui.TextUndoManager;

import org.netbeans.api.settings.ConvertAsProperties;
// import org.netbeans.modules.spellchecker.api.Spellchecker;

import org.openide.awt.ActionID;
import org.openide.awt.UndoRedo;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Top component which displays the docx documents.
 */
@ConvertAsProperties(
        dtd = "-//org.joeffice.wordprocessor//WordProcessor//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "WordProcessorTopComponent",
        iconBase = "org/joeffice/wordprocessor/wordprocessor-16.png",
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.joeffice.wordprocessor.WordProcessorTopComponent")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_WordProcessorAction",
        preferredID = "WordProcessorTopComponent")
@Messages({
    "CTL_WordProcessorAction=Word Processor",
    "CTL_WordProcessorTopComponent=Word processor Window",
    "HINT_WordProcessorTopComponent=This is a Word processor window"
})
public final class WordProcessorTopComponent extends OfficeTopComponent implements DocumentListener {
    private TextUndoManager undoRedo = new TextUndoManager();

    public WordProcessorTopComponent() {
        this(Utilities.actionsGlobalContext().lookup(DocxDataObject.class));
    }

    public WordProcessorTopComponent(OfficeDataObject dataObject) {
        super(dataObject);
    }

    @Override
    protected JComponent createMainComponent() {
        WordProcessorComponent wordProcessor = new WordProcessorComponent();
        return wordProcessor;
    }

    @Override
    public String getShortName() {
        return "Word Processor";
    }

    @Override
    public Object loadDocument(final File docxFile) throws Exception {
        WordProcessorComponent wordProcessor = (WordProcessorComponent) getMainComponent();
        return wordProcessor.loadDocument(docxFile);
    }

    @Override
    public void documentLoaded() {
        WordProcessorComponent wordProcessor = (WordProcessorComponent) getMainComponent();
        Document document = wordProcessor.getDocument();
        document.addDocumentListener(this);
        document.addUndoableEditListener(undoRedo);
        document.addDocumentListener(new DocumentUpdater(wordProcessor.getPOIDocument()));

        // Doesn't do anything (yet)
        // This require the implementation of a TokenListProvider and of a TokenList
        // Spellchecker.register(editor);
        /*FindAction find = new FindAction();
         getActionMap().put(find.getName(), find);
         ReplaceAction replace = new ReplaceAction();
         getActionMap().put(replace.getName(), replace);*/
    }

    @Override
    protected void componentActivated() {
        WordProcessorComponent wordProcessor = (WordProcessorComponent) getMainComponent();
        ActionMap editorActionMap = wordProcessor.getActionMap();
        getActionMap().put(DefaultEditorKit.cutAction, editorActionMap.get(DefaultEditorKit.cutAction));
        getActionMap().put(DefaultEditorKit.copyAction, editorActionMap.get(DefaultEditorKit.copyAction));
        getActionMap().put(DefaultEditorKit.pasteAction, editorActionMap.get(DefaultEditorKit.pasteAction));
        getServices().add(wordProcessor.getStyleable());
        super.componentActivated();
    }

    @Override
    protected void componentDeactivated() {
        WordProcessorComponent wordProcessor = (WordProcessorComponent) getMainComponent();
        getServices().remove(wordProcessor.getStyleable());
        super.componentDeactivated();
    }

    @Override
    public UndoRedo getUndoRedo() {
        return undoRedo;
    }

    public XWPFDocument getPOIDocument() {
        return (XWPFDocument) getDataObject().getDocument();
    }

    public static JTextPane findCurrentTextPane() {
        WordProcessorTopComponent wordProcessor = OfficeTopComponent.getSelectedComponent(WordProcessorTopComponent.class);
        return (JTextPane) wordProcessor.getMainComponent();
    }

    @Override
    public void writeProperties(java.util.Properties properties) {
        super.writeProperties(properties);
    }

    @Override
    public void readProperties(java.util.Properties properties) {
        super.readProperties(properties);
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        getDataObject().setModified(true);
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        getDataObject().setModified(true);
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        getDataObject().setModified(true);
    }
}
