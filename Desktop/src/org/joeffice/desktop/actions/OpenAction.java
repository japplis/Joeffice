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
package org.joeffice.desktop.actions;

import java.awt.event.ActionEvent;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JFileChooser;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * Open in the OS after asking the user. Similar to Actions/System/org-openide-actions-FileSystemAction.instance
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "File",
        id = "org.joeffice.desktop.actions.OpenAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/folder_table.png",
        displayName = "#CTL_OpenAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 100),
    @ActionReference(path = "Toolbars/File", position = 100),
    @ActionReference(path = "Shortcuts", name = "D-O")
})
@Messages({"CTL_OpenAction=Open..."})
public class OpenAction extends NewFileAction {

    @Override
    public void actionPerformed(ActionEvent ev) {
        JFileChooser newFileChooser = createFileChooser();
        int saveResult = newFileChooser.showOpenDialog(WindowManager.getDefault().getMainWindow());
        if (saveResult == JFileChooser.APPROVE_OPTION) {
            List<File> savedFiles = getSelectedFiles(newFileChooser);
            openFiles(savedFiles);
        }
    }

    @Override
    protected JFileChooser createFileChooser() {
        JFileChooser docFileChooser = super.createFileChooser();
        docFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        docFileChooser.setSelectedFiles(new File[0]);
        docFileChooser.setSelectedFile(null);
        docFileChooser.setMultiSelectionEnabled(true);
        return docFileChooser;
    }

    @Override
    protected void openFiles(List<File> files) {
        for (File savedFile : files) {
            try {
                open(FileUtil.toFileObject(savedFile));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
