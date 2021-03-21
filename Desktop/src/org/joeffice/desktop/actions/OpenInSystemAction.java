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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.AbstractAction;

import org.joeffice.desktop.ui.OfficeUIUtils;

import org.openide.DialogDescriptor;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Open in the OS after asking the user.
 * Similar to Actions/System/org-openide-actions-FileSystemAction.instance
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "File",
        id = "org.joeffice.desktop.actions.OpenInSystemAction")
@ActionRegistration(
        displayName = "#CTL_OpenInSystemAction")
@ActionReferences({
    @ActionReference(path = "Loaders/content/unknown/Actions", position = 100),
})
@Messages({"CTL_OpenInSystemAction=Open in System",
        "MSG_NotRecognizedDoc=Only Word 2007 - 2013 Documents (.docx) are supported.",
        "MSG_NotRecognizedPpt=Only Powerpoint 2007 - 2013 presentations (.pptx) are supported.",
        "MSG_NotRecognizedUnsupported=This file format is not supported by Joeffice.",
        "MSG_NotRecognizedOpenExternal=Do you want to open the file in an external editor/viewer?",
        "MSG_NotRecognizedTitle=Unknown file format"})
public class OpenInSystemAction extends AbstractAction {

    private final DataObject context;

    public OpenInSystemAction(DataObject context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Object anwser = askOpenInSystem(context.getName());
        if (anwser == DialogDescriptor.YES_OPTION) {
            Desktop desktop = Desktop.getDesktop();
            File file = FileUtil.toFile(context.getPrimaryFile());
            try {
                desktop.open(file);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    protected Object askOpenInSystem(String fileName) {
        String title = NbBundle.getMessage(getClass(), "MSG_NotRecognizedTitle");
        String unsupportedText = NbBundle.getMessage(getClass(), "MSG_NotRecognizedUnsupported");
        String extension = fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")).toLowerCase() : "";
        if (extension.equals(".doc") || extension.equals("odt")) {
            unsupportedText = NbBundle.getMessage(getClass(), "MSG_NotRecognizedDoc");
        }
        if (extension.equals(".ppt") || extension.equals("odp")) {
            unsupportedText = NbBundle.getMessage(getClass(), "MSG_NotRecognizedPpt");
        }
        String openExternalText = NbBundle.getMessage(getClass(), "MSG_NotRecognizedOpenExternal");
        Object answer = OfficeUIUtils.ask(title, DialogDescriptor.YES_NO_OPTION, unsupportedText, openExternalText);
        return answer;
    }
}
