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
package org.joeffice.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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

/**
 * The data object to deal with .h2.db (H2 database) files.
 *
 * @author Anthony Goubard - Japplis
 */
@Messages({
    "LBL_H2_LOADER=H2 Database"
})
@MIMEResolver.Registration(
        displayName = "#LBL_H2_LOADER",
        resource = "/org/joeffice/database/h2-db-resolver-definition.xml",
        position = 170) // Netbeans Platform can't handle .h2.db for ExtensionRegistration
//         showInFileChooser = "#LBL_H2_LOADER", doesn't compile in NetBeans 7.4
@DataObject.Registration(
        mimeType = "application/h2",
        iconBase = "org/joeffice/database/database-16.png",
        displayName = "#LBL_H2_LOADER",
        position = 170)
@ActionReferences({
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/application/h2/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class H2DataObject extends OfficeDataObject {

    public H2DataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
    }

    @Override
    public OfficeTopComponent open(OfficeDataObject dataObject) {
        return new JDBCTopComponent(dataObject);
    }

    @Override
    public void save(File file) throws IOException {
        // TODO show warning if different file
        Connection connection = (Connection) getDocument();
        try {
            connection.commit();
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    public static Connection getConnection(File h2File) throws ClassNotFoundException, SQLException {
        Class.forName("org.h2.Driver");
        String filePath = h2File.getAbsolutePath().replace('\\', '/');
        if (filePath.endsWith(".h2.db")) {
            filePath = filePath.substring(0, filePath.length() - 6);
        }
        String jdbcUrl = "jdbc:h2:" + filePath + ";AUTOCOMMIT=OFF";
        Connection dbConnection = DriverManager.getConnection(jdbcUrl, "sa", "");
        return dbConnection;
    }
}
