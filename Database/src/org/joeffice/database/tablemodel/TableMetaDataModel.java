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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * Table model to see the table structure.
 *
 * @author Anthony Goubard - Japplis
 */
public class TableMetaDataModel extends AbstractTableModel {

    private List<String> columnNames = new ArrayList<>();
    private List<String> columnTypes = new ArrayList<>();
    private List<Boolean> columnNullable = new ArrayList<>();
    private List<String> primaryKeys = new ArrayList<>();

    public TableMetaDataModel(Connection dbConnection, String tableName) throws SQLException {
        ResultSet primaryKeysSet = dbConnection.getMetaData().getPrimaryKeys(null, null, tableName);
        while (primaryKeysSet.next()) {
            String columnName = primaryKeysSet.getString("COLUMN_NAME");
            primaryKeys.add(columnName);
        }
        ResultSet columns = dbConnection.getMetaData().getColumns(null, null, tableName, null);
        while (columns.next()) {
            String columnName = columns.getString("COLUMN_NAME");
            String columnType = columns.getString("TYPE_NAME");
            String nullable = columns.getString("IS_NULLABLE");
            columnNames.add(columnName);
            columnTypes.add(columnType);
            columnNullable.add("YES".equals(nullable));
        }
    }

    @Override
    public int getRowCount() {
        return 4;
    }

    @Override
    public int getColumnCount() {
        return columnNames.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (rowIndex) {
            case 0:
                return columnNames.get(columnIndex);
            case 1:
                return columnTypes.get(columnIndex);
            case 2:
                return columnNullable.get(columnIndex);
            case 3:
                return primaryKeys.contains(columnNames.get(columnIndex));
        }
        return "";
    }

}
