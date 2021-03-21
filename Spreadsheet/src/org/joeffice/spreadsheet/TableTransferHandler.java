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
package org.joeffice.spreadsheet;

import org.joeffice.spreadsheet.cell.CellUtils;
import static javax.swing.TransferHandler.MOVE;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.util.ArrayUtil;

import org.joeffice.desktop.ui.OfficeTransferHandler;


/**
 * Transfer handler that support rich text.
 * Supported MIME types are plain text, HTML and RTF.
 *
 * @author Anthony Goubard - Japplis
 */
public class TableTransferHandler extends OfficeTransferHandler {

    /**
     * The paste method used for DnD and clipboard.
     */
    @Override
    public boolean importData(JComponent c, Transferable t) {
        if (canImport(new TransferHandler.TransferSupport(c, t))) {
            JTable table = (JTable) c;
            String plainText = getTextFromTransferable(t, DataFlavor.stringFlavor);
            if (plainText != null) {
                paste(plainText, table);
            }
        }
        return false;
    }

    protected void paste(String text, JTable table) {
        int firstRow = 0;
        int firstColumn = 0;
        JTable.DropLocation dropLocation = table.getDropLocation();
        if (dropLocation != null) {
            firstRow = dropLocation.getRow();
            firstColumn = dropLocation.getColumn();
        } else if (table.getSelectedRow() != -1) {
            firstRow = table.getSelectedRow();
            if (table.getSelectedColumn() != -1) {
                firstColumn = table.getSelectedColumn();
            }
        }

        int row = firstRow;
        String[] lines = text.split("\n");
        for (String line : lines) {
            int column = firstColumn;
            String[] values = line.split("\t");
            for (String value : values) {
                table.setValueAt(value, row, column);
                column++;
            }
            row++;
        }
    }

    @Override
    protected Transferable createTransferable(JComponent c) {
        JTable table = (JTable) c;
        String text = getSelectedText(table);
        return new StringSelection(text);
    }

    protected String getSelectedText(JTable table) {
        int[] rows = table.getSelectedRows();
        int[] columns = CellUtils.getSelectedColumns(table, rows);
        if (rows.length ==0 || columns.length == 0) {
            return "";
        }
        StringBuilder text = new StringBuilder();
        for (int row : rows) {
            for (int column : columns) {
                Cell cell = (Cell) table.getValueAt(row, column);
                String cellText = CellUtils.getFormattedText(cell);
                text.append(cellText);
                text.append('\t');
            }
            text.deleteCharAt(text.length() - 1);
            text.append(System.getProperty("line.separator"));
        }
        return text.toString();
    }

    @Override
    public void exportDone(JComponent comp, Transferable transferable, int action) {
        if (action == MOVE) {
            JTable table = (JTable) comp;
        }
    }
}
