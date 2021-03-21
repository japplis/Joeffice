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
package org.joeffice.spreadsheet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.joeffice.desktop.file.OfficeDataObject;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.csv.CSVWorkbook;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

/**
 * Data object that handles the ooxml Excel format (.xslx).
 *
 * @author Anthony Goubard - Japplis
 */
@Messages({
    "LBL_Xlsx_LOADER=Microsoft Excel 2007 / 2010"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Xlsx_LOADER",
        mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        extension = {"xlsx"},
        showInFileChooser = "#LBL_Xlsx_LOADER",
        position = 120)
@DataObject.Registration(
        mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        iconBase = "org/joeffice/spreadsheet/spreadsheet-16.png",
        displayName = "#LBL_Xlsx_LOADER",
        position = 120)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "Edit", id = "org.netbeans.core.ui.sysopen.SystemOpenAction"),
            position = 150,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class XlsxDataObject extends OfficeDataObject {

    public XlsxDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
    }

    @Override
    public OfficeTopComponent open(OfficeDataObject dataObject) {
        return new SpreadsheetTopComponent(dataObject);
    }

    @Override
    public synchronized void save() throws IOException {
        super.save();

        // bug in Apache POI https://issues.apache.org/bugzilla/show_bug.cgi?id=49940
        if (getDocument() instanceof XSSFWorkbook && !(getDocument() instanceof CSVWorkbook)) {
            try {
                Workbook workbook = JoefficeWorkbookFactory.create(FileUtil.toFile(getPrimaryFile()));
                setDocument(workbook);
            } catch (InvalidFormatException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @Override
    public synchronized void save(File file) throws IOException {
        Workbook workbook = (Workbook) getDocument();
        try (FileOutputStream xslxOutputStream = new FileOutputStream(file)) {
            if (workbook instanceof CSVWorkbook) {
                ((CSVWorkbook) workbook).write2(xslxOutputStream);
            } else {
                workbook.write(xslxOutputStream);
            }
        }
    }
}
