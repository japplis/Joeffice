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
package org.joeffice.spreadsheet.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JTable;

import org.apache.poi.ss.usermodel.Cell;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.cell.CellUtils;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.joeffice.spreadsheet.sheet.SheetTableModel;

import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action that deletes the content of the selected cells.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.DeleteCellsAction")
@ActionRegistration(
        displayName = "#CTL_DeleteCellsAction")
@Messages("CTL_DeleteCellsAction=Delete Cells")
public final class DeleteCellsAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable currentTable = currentTopComponent.getSelectedTable();
            SheetTableModel tableModel = (SheetTableModel) currentTable.getModel();
            List<Cell> selectedCells = CellUtils.getSelectedCells(currentTable);
            for (Cell cell : selectedCells) {
                cell.setCellValue("");
                tableModel.fireTableCellUpdated(cell.getRowIndex(), cell.getColumnIndex());
            }
        }
    }
}
