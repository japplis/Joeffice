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
package org.joeffice.spreadsheet.cell;

import java.awt.Color;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JTable;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.joeffice.spreadsheet.sheet.SheetTableModel;

/**
 * Utility methods for Cell and Row manipulation.
 *
 * @author Anthony Goubard - Japplis
 */
public class CellUtils {

    private final static NumberFormat NUMBER_FORMATTER = DecimalFormat.getInstance();
    private final static DateFormat DATE_FORMATTER = DateFormat.getDateInstance();
    private final static DateFormat TIME_FORMATTER = DateFormat.getTimeInstance();
    private final static NumberFormat CURRENCY_FORMATTER = DecimalFormat.getCurrencyInstance();

    /**
     * Converts a POI color to an AWT color.
     */
    public static Color shortToColor(short xlsColorIndex) {
        if (xlsColorIndex > 0) {
            HSSFColor xlsColor = HSSFColor.getIndexHash().get(new Integer(xlsColorIndex));
            if (xlsColor != null) {
                short[] rgb = xlsColor.getTriplet();
                return new Color(rgb[0], rgb[1], rgb[2]);
                //return Color.decode(xlsColor.getHexString());
            }
        }
        return null;
    }

    public static Color getFontColor(Font font, Workbook workbook) {
        org.apache.poi.ss.usermodel.Color poiColor = null;
        if (font instanceof HSSFFont) {
            poiColor = ((HSSFFont) font).getHSSFColor((HSSFWorkbook) workbook);
        } else if (font instanceof XSSFFont) {
            poiColor = ((XSSFFont) font).getXSSFColor();
        }
        return poiToAwtColor(poiColor);
    }

    /**
     * Converts a POI color to an AWT color.
     */
    public static Color poiToAwtColor(org.apache.poi.ss.usermodel.Color poiColor) {
        if (poiColor == null) return null;
        if (poiColor instanceof XSSFColor) {
            XSSFColor xssfColor = XSSFColor.toXSSFColor(poiColor);
            return Color.decode("0x" + xssfColor.getARGBHex().substring(2));
            /*byte[] rgb = ((XSSFColor) poiColor).getARGB();
            if (rgb[0] < 0) return null;
            if (rgb != null) {
                return new Color((rgb[0] < 0) ? (rgb[0] + 256) : rgb[0],
                        (rgb[1] < 0) ? (rgb[1] + 256) : rgb[1],
                        (rgb[2] < 0) ? (rgb[2] + 256) : rgb[2]);
            }*/
        } else if (poiColor instanceof HSSFColor) { // && !(poiColor instanceof HSSFColor.AUTOMATIC)) {
            short[] rgb = ((HSSFColor) poiColor).getTriplet();
            if (rgb[0] < 0) return null;
            return new Color(rgb[0], rgb[1], rgb[2]);
        }
        return null;
    }

    /**
     * Converts a POI color to an AWT color.
     */
    public static short colorToShort(Color awtColor, Cell cell) {
        return -1;
    }

    public static Cell getCell(boolean createIfAbsent, Sheet sheet, int rowIndex, int columnIndex) {
        Cell cell = null;
        Row row = sheet.getRow(rowIndex);
        if (row != null) {
            cell = row.getCell(columnIndex);
            if (cell == null && createIfAbsent) {
                cell = row.createCell(columnIndex);
            }
        } else if (createIfAbsent) {
            row = sheet.createRow(rowIndex);
            cell = row.createCell(columnIndex);
        }
        return cell;
    }

    /**
     * Copy a cell to another column in the same row
     *
     * @param input
     * @param column
     */
    public static void copyCellToColumn(Row row, Cell input, int column) {
        if (input == null) {
            Cell destCell = row.getCell(column);
            if (destCell != null) {
                row.removeCell(destCell);
            }
        } else {
            Cell destCell = row.getCell(column);
            if (destCell == null) {
                destCell = row.createCell(column, input.getCellType());
            }
            copyCell(input, destCell);
        }
    }

    // From http://stackoverflow.com/questions/5785724/how-to-insert-a-row-between-two-rows-in-an-existing-excel-with-hssf-apache-poi
    public static void copyCell(Cell oldCell, Cell newCell) {
        newCell.setCellStyle(oldCell.getCellStyle());

        if (newCell.getCellComment() != null) {
            newCell.setCellComment(oldCell.getCellComment());
        }

        if (oldCell.getHyperlink() != null) {
            newCell.setHyperlink(oldCell.getHyperlink());
        }

        newCell.setCellType(oldCell.getCellType());

        // Set the cell data value
        switch (oldCell.getCellType()) {
            case _NONE:
            case BLANK:
                break;
            case BOOLEAN:
                newCell.setCellValue(oldCell.getBooleanCellValue());
                break;
            case ERROR:
                newCell.setCellErrorValue(oldCell.getErrorCellValue());
                break;
            case FORMULA:
                newCell.setCellFormula(oldCell.getCellFormula());
                break;
            case NUMERIC:
                newCell.setCellValue(oldCell.getNumericCellValue());
                break;
            case STRING:
                newCell.setCellValue(oldCell.getRichStringCellValue());
                break;
        }
    }

    public static String getFormattedText(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType type = cell.getCellType();
        if (type == CellType.STRING) {
            return cell.getStringCellValue();
        } else if (type == CellType.NUMERIC) {
            if (DateUtil.isCellDateFormatted(cell)) {
                return DATE_FORMATTER.format(cell.getDateCellValue());
            } else {
                return NUMBER_FORMATTER.format(cell.getNumericCellValue());
            }
        } else if (type == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return "";
        }
    }

    public static int[] getSelectedColumns(JTable table, int[] rows) {
        int firstWithValue = Integer.MAX_VALUE;
        int lastWithValue = 0;
        Set<Integer> selectedColumns = new TreeSet<>();
        for (int row : rows) {
            for (int i = 0; i < table.getColumnCount(); i++) {
                if (table.isCellSelected(row, i)) {
                    selectedColumns.add(i);
                    String cellText = getFormattedText((Cell) table.getValueAt(row, i));
                    if (!cellText.equals("") && i < firstWithValue) {
                        firstWithValue = i;
                    }
                    if (!cellText.equals("") && i > lastWithValue) {
                        lastWithValue = i;
                    }
                }
            }
        }
        Set<Integer> outOfBoundColumns = new TreeSet<>();
        for (int column : selectedColumns) {
            if (column < firstWithValue || column > lastWithValue) {
                outOfBoundColumns.add(column);
            }
        }
        selectedColumns.removeAll(outOfBoundColumns);
        int[] selected = new int[selectedColumns.size()];
        int index = 0;
        for (int selectedColumn : selectedColumns) {
            selected[index] = selectedColumn;
            index++;
        }
        return selected;
    }

    public static List<Cell> getSelectedCells() {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable currentTable = currentTopComponent.getSelectedTable();
            List<Cell> selectedCells = CellUtils.getSelectedCells(currentTable);
            return selectedCells;
        }
        return Collections.EMPTY_LIST;
    }

    public static List<Cell> getSelectedCells(JTable table) {
        return getSelectedCells(table, false);
    }

    public static List<Cell> getSelectedCells(JTable table, boolean createIfAbsent) {
        List<Cell> cells = new ArrayList<>();
        Sheet sheet = ((SheetTableModel) table.getModel()).getSheet();
        int rowIndexStart = table.getSelectedRow();
        if (rowIndexStart < 0) {
            return cells;
        }
        int rowIndexEnd = table.getSelectionModel().getMaxSelectionIndex();
        int[] selectedColumns = CellUtils.getSelectedColumns(table, table.getSelectedRows());
        if (selectedColumns.length == 0 || selectedColumns[0] < 0) {
            return cells;
        }
        int colIndexStart = selectedColumns[0];
        int colIndexEnd = selectedColumns[selectedColumns.length - 1];

        // Go through all the selected cells and all the attributes
        for (int i = rowIndexStart; i <= rowIndexEnd; i++) {
            for (int j = colIndexStart; j <= colIndexEnd; j++) {
                if (table.isCellSelected(i, j)) {
                    Cell cell = getCell(createIfAbsent, sheet, i, j);
                    if (cell != null) {
                        cells.add(cell);
                    }
                }
            }
        }
        return cells;
    }
}
