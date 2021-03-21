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
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import javax.swing.filechooser.FileSystemView;
import org.joeffice.desktop.file.OfficeDataObject;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

/**
 * Class used to create a new document.
 * This class is temporary until the annotation requireProject=false is supported.
 *
 * @author Anthony Goubard - Japplis
 */
// Wait for http://netbeans.org/bugzilla/show_bug.cgi?id=186943 to be released
@ActionID(
        category = "File",
        id = "org.joeffice.desktop.actions.NewFileAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/add.png",
        displayName = "#CTL_NewFileAction")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 90),
    @ActionReference(path = "Toolbars/File", position = 90),
    @ActionReference(path = "Shortcuts", name = "D-N")
})
@Messages({"CTL_NewFileAction=New File...",
    "MSG_ChooseExtension=Please choose or provide a file type",
    "CTL_Untitled=Untitled"})
public class NewFileAction implements ActionListener {
    private static final String LAST_LOCATION_KEY = "file.location";

    @Override
    public void actionPerformed(ActionEvent ae) {
        //if (0 == 0) throw new IllegalArgumentException("This is a test for the error reporting URL. If you read this you can remove the issue as it's not a bug but a test from another Netbeans platforma app.");
        JFileChooser newFileChooser = createFileChooser();
        int saveResult = newFileChooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
        if (saveResult == JFileChooser.APPROVE_OPTION) {
            List<File> savedFiles = getSelectedFiles(newFileChooser);
            openFiles(savedFiles);
        }
    }

    protected void openFiles(List<File> files) {
        for (File savedFile : files) {
            FileObject template = getFileTemplate(savedFile);
            if (template == null) {
                showChooseExtensionMessage();
                actionPerformed(null);
            } else if (shouldCreateFile(savedFile)) {
                try {
                    FileObject createdFile = createFileFromTemplate(template, savedFile);
                    open(createdFile);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    protected JFileChooser createFileChooser() {
        JFileChooser newFileChooser = new JFileChooser();
        String systemDefaultLocation = FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath();
        String defaultLocation = NbPreferences.forModule(NewFileAction.class)
                .get(LAST_LOCATION_KEY, systemDefaultLocation);
        File currentDir = new File(defaultLocation);
        newFileChooser.setCurrentDirectory(currentDir);
        newFileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
        String defaultName = NbBundle.getMessage(getClass(), "CTL_Untitled");
        newFileChooser.setSelectedFile(new File(currentDir, defaultName + ".docx"));
        addFileFilters(newFileChooser);
        return newFileChooser;
    }

    private void addFileFilters(JFileChooser chooser) {
        List<DataObject> possibleObjects = findDataObject("Templates/Office");
        chooser.setAcceptAllFileFilterUsed(true);
        for (DataObject dataObject : possibleObjects) {
            if (dataObject instanceof OfficeDataObject) {
                FileFilter filter = new OfficeFileFilter(dataObject);
                chooser.addChoosableFileFilter(filter);
            }
        }
    }

    public List<DataObject> findDataObject(String key) {
        List<DataObject> templates = new ArrayList<>();
        FileObject fo = FileUtil.getConfigFile(key);
        if (fo != null && fo.isValid()) {
            addFileObject(fo, templates);
        }
        return templates;
    }

    private void addFileObject(FileObject fileObject, List<DataObject> templates) {
        if (fileObject.isFolder()) {
            for (FileObject child : fileObject.getChildren()) {
                addFileObject(child, templates);
            }
        } else {
            try {
                DataObject dob = DataObject.find(fileObject);
                templates.add(dob);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public List<File> getSelectedFiles(JFileChooser newFileChooser) {
        File[] savedFiles = newFileChooser.getSelectedFiles();
        if (savedFiles.length == 0) savedFiles = new File[]{newFileChooser.getSelectedFile()};
        NbPreferences.forModule(NewFileAction.class).put(LAST_LOCATION_KEY, savedFiles[0].getParentFile().getAbsolutePath());
        FileFilter filter = newFileChooser.getFileFilter();
        List<File> chosenFiles = new ArrayList<>();
        for (File savedFile : savedFiles) {
            if (!savedFile.getName().contains(".") && filter != null && filter.getDescription().contains(".")) {
                String extension = filter.getDescription().substring(filter.getDescription().indexOf('.'));
                chosenFiles.add(new File(savedFile.getAbsolutePath() + extension));
            } else {
                chosenFiles.add(savedFile);
            }
        }
        return chosenFiles;
    }

    @NbBundle.Messages({"MSG_OVERWRITE_FILE=Overwrite existing file?"})
    public boolean shouldCreateFile(File newFile) {
        if (newFile.exists()) {
            String question = NbBundle.getMessage(NewFileAction.class, "MSG_OVERWRITE_FILE");
            int answer = JOptionPane.showConfirmDialog(WindowManager.getDefault().getMainWindow(), question, question, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            return answer == JOptionPane.YES_OPTION;
        }
        return true;
    }

    public FileObject getFileTemplate(File savedFile) {
        List<DataObject> possibleObjects = findDataObject("Templates/Office");
        for (final DataObject dataObject : possibleObjects) {
            if (dataObject instanceof OfficeDataObject && savedFile.getName().endsWith(dataObject.getPrimaryFile().getExt())) {
                return dataObject.getPrimaryFile();
            }
        }
        return null;
    }

    private void showChooseExtensionMessage() {
        String provideExtensionMessage = NbBundle.getMessage(NewFileAction.class, "MSG_ChooseExtension");
        NotifyDescriptor provideExtensionDialog =
                new NotifyDescriptor.Message(provideExtensionMessage, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(provideExtensionDialog);
    }

    public FileObject createFileFromTemplate(FileObject template, File savedFile) throws IOException {
        try (InputStream input = template.getInputStream();
                FileOutputStream output = new FileOutputStream(savedFile);) {
            FileUtil.copy(input, output);
            FileObject savedFileObject = FileUtil.toFileObject(FileUtil.normalizeFile(savedFile));
            return savedFileObject;
        }
    }

    protected void open(FileObject fileToOpen) throws DataObjectNotFoundException {
        DataObject fileDataObject = DataObject.find(fileToOpen);
        OpenCookie openCookie = fileDataObject.getCookie(OpenCookie.class);
        if (openCookie != null) {
            openCookie.open();
        }
    }

    private class OfficeFileFilter extends FileFilter {

        private DataObject dataObject;

        private OfficeFileFilter(DataObject dataObject) {
            this.dataObject = dataObject;
        }

        @Override
        public boolean accept(File file) {
            return file.isDirectory() || file.getName().endsWith(dataObject.getPrimaryFile().getExt());
        }

        @Override
        public String getDescription() {
            return "*." + dataObject.getPrimaryFile().getExt();
        }
    }
}
