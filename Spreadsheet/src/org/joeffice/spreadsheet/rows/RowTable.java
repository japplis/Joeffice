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
package org.joeffice.spreadsheet.rows;

import java.awt.Dimension;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.LookAndFeel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;

/**
 * Table used to displays the row numbers next to the data table.
 *
 * @author Anthony Goubard - Japplis
 */
public class RowTable extends JTable {

    private JTable dataTable;

    public RowTable(JTable dataTable) {
        this.dataTable = dataTable;
        TableModel rowModel = createRowTableModel();
        setModel(rowModel);
        LookAndFeel.installColorsAndFont(this, "TableHeader.background", "TableHeader.foreground", "TableHeader.font");

        getColumnModel().getColumn(0).setHeaderValue("");
        getColumnModel().getColumn(0).setPreferredWidth(40);
        Dimension d = getPreferredScrollableViewportSize();
        d.width = getPreferredSize().width;
        setPreferredScrollableViewportSize(d);
        setRowHeight(dataTable.getRowHeight());
        RowHeadersRenderer rowRenderer = new RowHeadersRenderer();
        setDefaultRenderer(String.class, rowRenderer); // This doesn't work!
        getColumnModel().getColumn(0).setCellRenderer(rowRenderer);
        setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JTableHeader corner = getTableHeader();
        corner.setReorderingAllowed(false);
        corner.setResizingAllowed(false);

        // Add listener for setRowHeight from the data table
        int rowCount = dataTable.getRowCount();
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            int currentRowHeight = dataTable.getRowHeight(rowIndex);
            if (currentRowHeight != getRowHeight(rowIndex)) {
                setRowHeight(rowIndex, currentRowHeight);
            }
        }

        // Listeners
        RowEventsListeners rowListeners = new RowEventsListeners(this);
        dataTable.addPropertyChangeListener("rowHeight", rowListeners);
        dataTable.addPropertyChangeListener("singleRowHeight", rowListeners);
        new TableRowResizer(this);
        getSelectionModel().addListSelectionListener(rowListeners);
        dataTable.getModel().addTableModelListener(rowListeners);
        addMouseListener(rowListeners);
    }

    private TableModel createRowTableModel() {
        DefaultTableModel rowTableModel = new DefaultTableModel(dataTable.getRowCount(), 1) {
            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return "" + (rowIndex + 1);
            }

            @Override
            public String getColumnName(int column) {
                return "";
            }

            @Override
            public int getRowCount() {
                return dataTable.getRowCount();
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        return rowTableModel;
    }

    @Override
    public void setRowHeight(int row, int rowHeight) {
        super.setRowHeight(row, rowHeight);
        dataTable.setRowHeight(row, rowHeight);
    }

    public JTable getDataTable() {
        return dataTable;
    }
}
