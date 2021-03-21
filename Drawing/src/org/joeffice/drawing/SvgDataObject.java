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
package org.joeffice.drawing;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.batik.dom.util.DOMUtilities;
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
import org.openide.util.NbBundle.Messages;

import org.w3c.dom.Document;

/**
 * Data object for .svg files.
 *
 * @author Anthony Goubard - Japplis
 */
@Messages({
    "LBL_Svg_LOADER=SVG Drawing"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Svg_LOADER",
        mimeType = "image/svg+xml",
        extension = {"svg"},
        showInFileChooser = "#LBL_Svg_LOADER",
        position = 160)
@DataObject.Registration(
        mimeType = "image/svg+xml",
        iconBase = "org/joeffice/drawing/drawing-16.png",
        displayName = "#LBL_Svg_LOADER",
        position = 160)
@ActionReferences({
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
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
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/image/svg+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class SvgDataObject extends OfficeDataObject {

    public SvgDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
    }

    @Override
    public OfficeTopComponent open(OfficeDataObject dataObject) {
        return new DrawingTopComponent(dataObject);
    }

    @Override
    public void save(File file) throws IOException {
        Document svgRoot = (Document) getDocument();
        try (FileWriter svgWriter  = new FileWriter(file)) {
            DOMUtilities.writeDocument(svgRoot, svgWriter);
        }
    }
}
