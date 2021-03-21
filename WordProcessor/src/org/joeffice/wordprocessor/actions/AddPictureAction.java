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
package org.joeffice.wordprocessor.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

import org.joeffice.wordprocessor.DocxDocument;
import org.joeffice.wordprocessor.WordProcessorTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Action that adds an image to the editor.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Word Processor",
        id = "org.joeffice.wordprocessor.actions.AddPictureAction")
@ActionRegistration(
        iconBase = "org/joeffice/wordprocessor/actions/picture_add.png",
        displayName = "#CTL_AddPictureAction")
@ActionReferences({
    @ActionReference(path = "Office/Word Processor/Toolbar", position = 200)})
@Messages("CTL_AddPictureAction=Add Picture")
public final class AddPictureAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        String defaultDirName = NbPreferences.forModule(getClass()).get("picture.directory", System.getProperty("user.home"));
        File defaultDir = new File(defaultDirName);
        if (!defaultDir.exists()) {
            defaultDir = new File(System.getProperty("user.home"));
        }
        JFileChooser choosePicture = new JFileChooser(defaultDir);
        int chooseResult = choosePicture.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (chooseResult == JFileChooser.APPROVE_OPTION) {
            File chosenPicture = choosePicture.getSelectedFile();
            String chosenDirName = chosenPicture.getParentFile().getAbsolutePath();
            NbPreferences.forModule(getClass()).put("picture.directory", chosenDirName);
            ImageIcon icon = new ImageIcon(choosePicture.getSelectedFile().getPath());
            JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
            DocxDocument doc = (DocxDocument) edit.getDocument();
            doc.insertPicture(icon, edit.getCaretPosition());
        }
    }
}
