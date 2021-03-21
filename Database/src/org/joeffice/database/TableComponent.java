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

import java.awt.BorderLayout;
import java.sql.Connection;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import org.joeffice.database.tablemodel.JDBCSheet;
import org.joeffice.spreadsheet.sheet.TableColumnAdjuster;
import org.joeffice.spreadsheet.rows.RowTableFactory;

import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.ETableTransferHandler;

/**
 * Component to display one of the database table.
 *
 * @author Anthony Goubard - Japplis
 */
public class TableComponent extends JPanel {

    private JDBCSheet sheet;
    private ETable databaseTable;

    public TableComponent(Connection conn, String tableName) {
        setLayout(new BorderLayout());
        sheet = new JDBCSheet(conn, tableName); // Table model
        databaseTable = new ETable(sheet);
        if (sheet.getColumnCount() > 5) {
            TableColumnAdjuster tca = new TableColumnAdjuster(databaseTable, 20);
            databaseTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tca.setOnlyAdjustLarger(true);
            tca.setLeaveEmptyAsIs(true);
            tca.adjustColumns();
        }

        JScrollPane scrolling = RowTableFactory.attachRows(databaseTable, databaseTable);

        databaseTable.setColumnHidingAllowed(true);
        databaseTable.setTransferHandler(new ETableTransferHandler());
        databaseTable.setDragEnabled(true); // Dragging not working yet
        databaseTable.setPopupUsedFromTheCorner(true);
        HighlightCellRenderer highlightRender = new HighlightCellRenderer();
        for (int i = 0; i < databaseTable.getColumnCount(); i++) {
            TableColumn column = databaseTable.getColumnModel().getColumn(i);
            column.setCellRenderer(highlightRender);
        }
        putClientProperty("print.printable", Boolean.TRUE);

        add(scrolling);
    }

    public JDBCSheet getSheet() {
        return sheet;
    }

    public JTable getDataTable() {
        return databaseTable;
    }
}
