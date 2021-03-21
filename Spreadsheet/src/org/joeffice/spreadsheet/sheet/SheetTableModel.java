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
package org.joeffice.spreadsheet.sheet;

import javax.swing.table.AbstractTableModel;

import org.apache.poi.ss.usermodel.*;
import org.joeffice.spreadsheet.cell.CellUtils;

/**
 * The POI sheet table model.
 *
 * @author Anthony Goubard - Japplis
 */
public class SheetTableModel extends AbstractTableModel {

    private Sheet sheet;

    public SheetTableModel(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public int getRowCount() {
        int lastRowNum = getLastRowNum();
        if (lastRowNum < 100) {
            return lastRowNum + 100;
        } else {
            return lastRowNum + 30;
        }
    }
    public int getLastRowNum() {
        return sheet.getLastRowNum();
    }

    public int getLastColumnNum() {
        int lastRowNum = sheet.getLastRowNum();
        int lastColumn = 0;
        for (int i = 0; i < lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                int lastCell = row.getLastCellNum() - 1;
                if (lastColumn < lastCell) {
                    lastColumn = lastCell;
                }
            }
        }
        return lastColumn;
    }

    @Override
    public int getColumnCount() {
        int lastColumn = getLastColumnNum();
        if (lastColumn < 20) {
            return lastColumn + 26;
        } else {
            return lastColumn + 10;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            Cell cell = row.getCell(columnIndex);
            return cell;
        } else {
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(Object newValue, int rowIndex, int columnIndex) {
        Cell cell = CellUtils.getCell(true, sheet, rowIndex, columnIndex);

        if (newValue instanceof Boolean) {
            cell.setCellValue((Boolean) newValue);
        } else {
            try {
                double numericValue = Double.parseDouble((String) newValue);
                cell.setCellValue(numericValue);
            } catch (NumberFormatException ex) {
                cell.setCellValue((String) newValue);
            }
        }
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    /**
     * Inserts a number of rows in the table.
     *
     * @param numberOfRows the number of rows to insert
     * @param rowBefore the index of the row before the row inserted, -1 to insert rows at the top of the sheet.
     */
    public void insertRows(int numberOfRows, int... rowsBefore) {
        if (numberOfRows <= 0) {
            return;
        }
        for (int i = rowsBefore.length - 1; i >= 0; i--) {
            int rowBefore = rowsBefore[i];
            if (rowBefore < sheet.getLastRowNum()) {
                sheet.shiftRows(rowBefore + 1, sheet.getLastRowNum(), numberOfRows);
                fireTableRowsInserted(rowBefore + 1, rowBefore + 1 + numberOfRows);
            }
        }
    }

    public void removeRows(int... rows) {
        for (int i = rows.length - 1; i >= 0; i--) { // Remove from last to first to keep the row indexes correct
            int rowIndex = rows[i];
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                // sheet.removeRow(row); // This only clear the row
                sheet.shiftRows(rowIndex + 1, sheet.getLastRowNum(), -1);
            } else if (rowIndex <= sheet.getLastRowNum()) {
                sheet.shiftRows(rowIndex + 1, sheet.getLastRowNum(), -1);
            }
            fireTableRowsDeleted(rowIndex, rowIndex);
        }
    }

    public void removeColumns(int... columns) {
        for (int rowIndex = 0; rowIndex < sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                short lastColumn = row.getLastCellNum();
                for (int i = columns.length; i >= 0; i--) {
                    int columnIndex = columns[i];
                    if (columnIndex <= lastColumn) {
                        Cell cell = row.getCell(columnIndex);
                        // I'm afraid that this only clear the cell and doesn't shift
                        // Also shiting columns is not supported in POI, so nothing happens for empty cells
                        if (cell != null) {
                            row.removeCell(cell);
                        }
                    }
                }
            }
        }
        fireTableStructureChanged();
    }

    public void deleteCell(int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            Cell cell = row.getCell(columnIndex);
            if (cell != null) {
                row.removeCell(cell);
            }
        }
    }

    public void insertColumn(int columnIndex) {
        for (int i = sheet.getFirstRowNum(); i < sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = row.getLastCellNum(); j > columnIndex; j--) {
                    CellUtils.copyCellToColumn(row, row.getCell(j), j + 1);
                }
                row.createCell(columnIndex);
            }
        }
        fireTableStructureChanged();
    }

    public void removeColumn(int columnIndex) {
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex < sheet.getLastRowNum(); rowIndex++) {
            deleteCell(rowIndex, columnIndex);
        }
        fireTableStructureChanged();
    }

    public Sheet getSheet() {
        return sheet;
    }
}
