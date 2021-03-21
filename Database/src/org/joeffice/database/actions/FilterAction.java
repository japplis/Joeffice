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
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.joeffice.database.FieldsPanel;
import org.joeffice.database.JDBCTopComponent;
import org.joeffice.database.tablemodel.JDBCSheet;
import org.joeffice.database.tablemodel.TableMetaDataModel;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Action that filters viewed entries of the database.
 * A dialog is display to the user to fill the data to filter for.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "View/Office/Database",
        id = "org.joeffice.database.actions.FilterAction")
@ActionRegistration(
        iconBase = "org/joeffice/database/actions/table_lightning.png",
        displayName = "#CTL_FilterAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Database/Toolbar", position = 1100)})
@Messages("CTL_FilterAction=Filter...")
public final class FilterAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        JDBCTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(JDBCTopComponent.class);
        if (currentTopComponent != null) {
            try {
                Connection conn = currentTopComponent.getDatabaseConnection();
                TableMetaDataModel metaData = new TableMetaDataModel(conn, currentTopComponent.getSelectedTableName());
                String title = NbBundle.getMessage(getClass(), "CTL_FilterAction");
                Map<String, Object> fieldValues = FieldsPanel.askFields(metaData, title);
                if (!fieldValues.isEmpty()) {
                    JDBCSheet sheet = currentTopComponent.getSelectedTableComponent().getSheet();
                    filterEntries(fieldValues, sheet);
                }
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public void filterEntries(Map<String, Object> fields, JDBCSheet sheet) throws SQLException {
        StringBuilder whereClause = new StringBuilder();
        for (Map.Entry<String, Object> field : fields.entrySet()) {
            Object value = field.getValue();
            String columnName = field.getKey();
            if (value instanceof Boolean && ((Boolean) value).booleanValue()) {
                whereClause.append(" AND ").append(columnName).append(" = TRUE");
            } else if (value instanceof String && !((String) value).isEmpty()) {
                whereClause.append(" AND ").append(columnName).append(" like '%").append(value).append("%'");
            }
        }
        if (whereClause.length() > 0) {
            whereClause.delete(0, 5);
            whereClause.insert(0, "WHERE ");
        }
        sheet.filter(whereClause.toString());
    }
}
