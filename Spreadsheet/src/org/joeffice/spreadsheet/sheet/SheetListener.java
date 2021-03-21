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

import java.awt.Dimension;
import java.awt.Rectangle;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;


/**
 * Listens to updates in the sheet to adapt the printable part.
 *
 * @author Anthony Goubard - Japplis
 */
public class SheetListener implements TableModelListener {

    private JTable table;

    public SheetListener(JTable table) {
        this.table = table;
        table.getModel().addTableModelListener(this);
    }

    @Override
    public void tableChanged(TableModelEvent tme) {
        if (tme.getType() == TableModelEvent.UPDATE) {
            SheetTableModel tableModel = (SheetTableModel) tme.getSource();
            int lastRow = tableModel.getLastRowNum();
            int lastColumn = tableModel.getLastColumnNum();
            Rectangle lastDataCellBounds = table.getCellRect(lastRow, lastColumn, true);
            table.putClientProperty("print.size", new Dimension(lastDataCellBounds.x + lastDataCellBounds.width, lastDataCellBounds.y + lastDataCellBounds.height));
        }
    }
}
