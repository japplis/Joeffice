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
package org.joeffice.database.tablemodel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.RowSet;
import javax.sql.rowset.RowSetFactory;
import javax.sql.rowset.RowSetProvider;
import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import org.joeffice.desktop.ui.OfficeUIUtils;

import org.openide.util.Exceptions;

/**
 * TableModel representing one table of the database.
 * Note that the method parameter indexes are 0 based in this class.
 * Most of JDBC method parameter indexes are 1 based.
 *
 * @author Anthony Goubard - Japplis
 */
public class JDBCSheet extends AbstractTableModel {

    public final static String BINARY_DATA_LABEL = "<binary data...>"; // No I18N
    public final static int MAX_RESULTS = 100;

    private Connection conn;
    private String tableName;

    private ResultSetMetaData columnsMetaData;
    private RowSet dataModel;
    private long offset = 0;
    private String whereClause = "";

    public JDBCSheet(Connection conn, String tableName) {
        this.conn = conn;
        this.tableName = tableName;
        init();
    }

    public void init() {
        fillWithQuery("SELECT * FROM " + tableName + " " + whereClause + " LIMIT " + MAX_RESULTS + " OFFSET " + offset);
    }

    public void filter(String whereClause) {
        this.whereClause = whereClause;
        offset = 0;
        init();
    }

    private void fillWithQuery(String query) {
        try {
            RowSetFactory rowSetFactory = RowSetProvider.newFactory();
            dataModel = rowSetFactory.createJdbcRowSet();
            dataModel.setUrl(conn.getMetaData().getURL());
            dataModel.setUsername(conn.getMetaData().getUserName());
            dataModel.setPassword("");

            dataModel.setCommand(query);
            dataModel.execute();

            columnsMetaData = dataModel.getMetaData();

            fireTableDataChanged();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Object getResultSetValueAsObject(ResultSet tableData, int columnIndex) throws SQLException {
        columnIndex++; // JDBC indexes are 1 based
        int columnType = columnsMetaData.getColumnType(columnIndex);
        Object dataValue = null;
        switch (columnType) {
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
            case Types.BIGINT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.ROWID:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.TIME:
            case Types.TIMESTAMP:
                dataValue = tableData.getObject(columnIndex);
                break;
            case Types.BLOB:
            case Types.CLOB:
            case Types.JAVA_OBJECT:
            case Types.NCLOB:
            case Types.VARBINARY:
                dataValue = BINARY_DATA_LABEL; // Do not read
                break;
        }
        return dataValue;
    }

    @Override
    public int getColumnCount() {
        try {
            return columnsMetaData.getColumnCount();
        } catch (SQLException ex) {
            return 0;
        }
    }

    @Override
    public String getColumnName(int column) {
        try {
            String columnName = columnsMetaData.getColumnName(column + 1);
            String displayedName = OfficeUIUtils.toDisplayable(columnName);
            return displayedName;
        } catch (SQLException ex) {
            return super.getColumnName(column);
        }
    }

    @Override
    public int getRowCount() {
        try {
            dataModel.last();
            return dataModel.getRow();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            dataModel.absolute(rowIndex + 1);
            Object value = getResultSetValueAsObject(dataModel, columnIndex);
            return value;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        boolean editable = false;
        try {
            editable = columnsMetaData.isWritable(column + 1)
                    && columnsMetaData.getColumnType(column + 1) != Types.ROWID;
        } catch (SQLException ex) {
            // Not editable
        }
        return editable;
    }

    @Override
    public void setValueAt(Object newValue, int row, int column) {
        try {
            int maxSize = columnsMetaData.getPrecision(column + 1);
            if (newValue instanceof String && newValue.toString().length() > maxSize) {
                JOptionPane.showMessageDialog(null, "The text should be no longer than " + maxSize + " characters"); // NO I18N
                return;
            }
        } catch (SQLException ex) {
        }
        updateDatabase(newValue, row, column);
    }

    private void updateDatabase(Object newValue, int row, int column) {
        try {
            dataModel.absolute(row + 1);
            setColumnValue(column, newValue);
            dataModel.updateRow();
        } catch (SQLException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void setColumnValue(int columnIndex, Object value) throws SQLException {
        int columnType = columnsMetaData.getColumnType(columnIndex + 1);
        switch (columnType) {
            case Types.BOOLEAN:
                dataModel.updateBoolean(columnIndex + 1, (Boolean) value);
                break;
            case Types.CHAR:
            case Types.VARCHAR:
            case Types.LONGVARCHAR:
            case Types.NVARCHAR:
            case Types.LONGNVARCHAR:
                dataModel.updateString(columnIndex + 1, (String) value);
                break;
            case Types.BIGINT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.ROWID:
            case Types.SMALLINT:
            case Types.TINYINT:
                dataModel.updateInt(columnIndex + 1, Integer.parseInt((String) value));
                break;
        }
    }

    public void removeRows(int[] rows) {
        for (int i = 0; i < rows.length; i++) {
            int row = rows[rows.length - i - 1];
            try {
                dataModel.absolute(row + 1);
                dataModel.deleteRow();
            } catch (SQLException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public RowSet getDataModel() {
        return dataModel;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
        init();
    }
}
