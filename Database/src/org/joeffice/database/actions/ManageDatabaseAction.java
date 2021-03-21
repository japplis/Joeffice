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
package org.joeffice.database.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;
import org.h2.tools.Console;
import org.joeffice.database.JDBCTopComponent;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.openide.loaders.DataObject;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

/**
 * An action that launches the H2 console to manage the opened database.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Tools",
        id = "org.joeffice.database.ManageDatabaseAction")
@ActionRegistration(
        iconBase = "org/joeffice/database/actions/table_gear.png",
        displayName = "#CTL_ManageDatabaseAction")
@ActionReferences({
    @ActionReference(path = "Office/Database/Toolbar", position = 1450),
    @ActionReference(path = "Loaders/application/h2/Actions", position = 150)
})
@Messages("CTL_ManageDatabaseAction=Manage Database in Browser")
public final class ManageDatabaseAction implements ActionListener, Runnable {

    private boolean fromContext;
    private DataObject context;

    public ManageDatabaseAction(DataObject context) {
        this.context = context;
        fromContext = true;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        JDBCTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(JDBCTopComponent.class);
        if (currentTopComponent != null && !fromContext) {
            context = currentTopComponent.getDataObject();

            // TODO use Task that can be stopped ?
            Thread consoleThread = new Thread(this);
            consoleThread.start();
        }
    }

    @Override
    public void run() {
        File databaseFile = FileUtil.toFile(context.getPrimaryFile());
        try {
            String url = "jdbc:h2:" + databaseFile.getAbsolutePath();
            if (url.endsWith(".h2.db")) {
                url = url.substring(0, url.length() - 6);
            }
            Console.main("-url", url, "-user", "sa");
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }
}
