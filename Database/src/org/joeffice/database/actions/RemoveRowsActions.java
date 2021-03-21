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
import javax.swing.JTable;

import org.joeffice.database.JDBCTopComponent;
import org.joeffice.database.TableComponent;
import org.joeffice.database.tablemodel.JDBCSheet;
import org.joeffice.desktop.ui.OfficeTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Removes selected rows from the database.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Database",
        id = "org.joeffice.database.actions.RemoveRowsActions")
@ActionRegistration(
        iconBase = "org/joeffice/database/actions/table_row_delete.png",
        displayName = "#CTL_RemoveRowsActions")
@ActionReferences(value = {
    @ActionReference(path = "Office/Database/Toolbar", position = 400),
    @ActionReference(path = "Office/Database/Rows/Popup")})
@Messages("CTL_RemoveRowsActions=Remove rows")
public final class RemoveRowsActions implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JDBCTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(JDBCTopComponent.class);
        if (currentTopComponent != null) {
            TableComponent currentTableComponent = currentTopComponent.getSelectedTableComponent();
            JTable currentTable = currentTableComponent.getDataTable();
            JDBCSheet currentSheet = currentTableComponent.getSheet();

            int[] selectedRows = currentTable.getSelectedRows();
            currentSheet.removeRows(selectedRows);
        }
    }
}
