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

import static javax.swing.ScrollPaneConstants.UPPER_LEFT_CORNER;
import java.awt.BorderLayout;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.joeffice.database.tablemodel.TableMetaDataModel;
import org.joeffice.spreadsheet.rows.RowTable;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.util.Exceptions;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays the table structure.
 */
@ConvertAsProperties(
        dtd = "-//org.joeffice.database//ManageTable//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "ManageTableTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@Messages({
    "CTL_ManageTableTopComponent=ManageTable Window",
    "HINT_ManageTableTopComponent=This is a ManageTable window"
})
public final class ManageTableTopComponent extends TopComponent {

    private JTable metaDataTable;
    private File h2File;
    private String tableName;

    public ManageTableTopComponent() {
        initComponents();
        setName(Bundle.CTL_ManageTableTopComponent());
        setToolTipText(Bundle.HINT_ManageTableTopComponent());
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        metaDataTable = new JTable();
        JScrollPane scrolling = new JScrollPane(metaDataTable);
        JTable rowHeaders = new RowTable(metaDataTable);
        final List<String> rowText = Arrays.asList("Column name", "Column type", "Can be empty", "Is key");
        rowHeaders.setModel(new DefaultTableModel(4, 1) {

            @Override
            public Object getValueAt(int row, int column) {
                return rowText.get(row);
            }
        });
        scrolling.setRowHeaderView(rowHeaders);
        scrolling.setCorner(UPPER_LEFT_CORNER, rowHeaders.getTableHeader());
        add(scrolling);
    }

    public void initData(File h2File, String tableName) {
        this.h2File = h2File;
        this.tableName = tableName;
        try {
            Connection connection = H2DataObject.getConnection(h2File);
            TableMetaDataModel metaDataModel = new TableMetaDataModel(connection, tableName);
            metaDataTable.setModel(metaDataModel);
        } catch (ClassNotFoundException | SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    void writeProperties(Properties properties) {
        properties.setProperty("version", "1.0");
        properties.put("filePath", h2File.getAbsolutePath());
        properties.put("tableName", tableName);
    }

    void readProperties(Properties properties) {
        String version = properties.getProperty("version");
        String filePath = properties.getProperty("filePath");
        String tableName = properties.getProperty("tableName");
        initData(new File(filePath), tableName);

    }
}
