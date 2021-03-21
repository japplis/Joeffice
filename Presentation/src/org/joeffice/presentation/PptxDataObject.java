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
package org.joeffice.presentation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xslf.usermodel.XMLSlideShow;

import org.joeffice.desktop.file.OfficeDataObject;
import org.joeffice.desktop.ui.OfficeTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.CookieSet;
import org.openide.util.NbBundle.Messages;

/**
 * The data object that deals with .pptx files.
 *
 * @author Anthony Goubard - Japplis
 */
@Messages({
    "LBL_Pptx_LOADER=Powerpoint 2007 / 2010"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Pptx_LOADER",
        mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        extension = {"pptx"},
        showInFileChooser = "#LBL_Pptx_LOADER",
        position = 140)
@DataObject.Registration(
        mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        iconBase = "org/joeffice/presentation/presentation-16.png",
        displayName = "#LBL_Pptx_LOADER",
        position = 140)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.netbeans.core.ui.sysopen.SystemOpenAction"),
            position = 150,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.presentationml.presentation/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class PptxDataObject extends OfficeDataObject implements CookieSet.Factory {

    public PptxDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
    }

    @Override
    public OfficeTopComponent open(OfficeDataObject dataObject) {
        return new SlidesTopComponent(dataObject);
    }

    @Override
    public void save(File file) throws IOException {
        XMLSlideShow presentation = (XMLSlideShow) getDocument();
        try (FileOutputStream pptxOutputStream = new FileOutputStream(file)) {
            presentation.write(pptxOutputStream);
        }
    }
}
