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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JTable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.cell.CellUtils;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.joeffice.spreadsheet.sequence.DateStringSequence;
import org.joeffice.spreadsheet.sequence.IdentitySequence;
import org.joeffice.spreadsheet.sequence.NumberSequence;
import org.joeffice.spreadsheet.sequence.ResourceBundleSequence;
import org.joeffice.spreadsheet.sequence.Sequence;
import org.joeffice.spreadsheet.sheet.SheetTableModel;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Completes the empty cell based on the content of the previous cells.
 *
 * @see org.joeffice.spreadsheet.sequence.Sequence
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.CompleteSequenceAction")
@ActionRegistration(
        iconBase = "org/joeffice/spreadsheet/actions/application_go.png",
        displayName = "#CTL_CompleteSequenceAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Spreadsheet/Toolbar", position = 700),
    @ActionReference(path = "Menu/Edit/Office/Spreadsheet", position = 700)})
@Messages("CTL_CompleteSequenceAction=Complete")
public final class CompleteSequenceAction implements ActionListener {

    private List<Sequence> sequences = new ArrayList<>();

    @Override
    public void actionPerformed(ActionEvent e) {
        if (sequences.isEmpty()) initSequences();
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable currentTable = currentTopComponent.getSelectedTable();
            int[] selectedRows = currentTable.getSelectedRows();
            int[] selectedColumns = currentTable.getSelectedColumns();
            if (selectedRows.length == 0 || selectedColumns.length == 0) {
                return;
            }
            SheetTableModel model = (SheetTableModel) currentTable.getModel();
            completeCells(model, selectedRows, selectedColumns);
        }
    }

    protected void initSequences() {
        sequences.add(new NumberSequence());
        sequences.add(new DateStringSequence());
        sequences.add(new ResourceBundleSequence());
        if (!Locale.getDefault().getLanguage().equals(Locale.US.getLanguage())) {
            sequences.add(new DateStringSequence(Locale.US));
            sequences.add(new ResourceBundleSequence(Locale.US));
        }
        sequences.add(new IdentitySequence());
    }

    protected void completeCells(SheetTableModel model, int[] selectedRows, int[] selectedColumns) {
        Sheet sheet = model.getSheet();
        boolean completeRows = true;
        int firstRow = selectedRows[0];
        int firstColumn = selectedColumns[0];
        int lastRow = selectedRows[selectedRows.length - 1];
        int lastColumn = selectedColumns[selectedColumns.length - 1];
        for (int i = firstRow; i <= lastRow; i++) {
            for (int j = firstColumn; j <= lastColumn; j++) {
                Cell cell = CellUtils.getCell(true, sheet, i, j);
                String text = CellUtils.getFormattedText(cell);
                if (text.trim().equals("")) {
                    if (completeRows && j == firstColumn) {
                        completeRows = false;
                    }
                    List<String> previousValues;
                    if (completeRows) {
                        previousValues = getPreviousValues(sheet, firstColumn, j, completeRows, i);
                    } else {
                        previousValues = getPreviousValues(sheet, firstRow, i, completeRows, j);
                    }
                    String nextValue = getNextValue(previousValues);
                    model.setValueAt(nextValue, i, j);
                }
            }
        }
    }

    protected List<String> getPreviousValues(Sheet sheet, int from, int to, boolean completeRows, int columnOrRow) {
        List<String> previousValues = new ArrayList<>();
        for (int i = from; i < to; i++) {
            Cell cell;
            if (completeRows) {
                cell = CellUtils.getCell(false, sheet, columnOrRow, i);
            } else {
                cell = CellUtils.getCell(false, sheet, i, columnOrRow);
            }
            String text = CellUtils.getFormattedText(cell);
            previousValues.add(text);
        }
        return previousValues;
    }

    protected String getNextValue(List<String> previousValues) {
        for (Sequence sequence : sequences) {
            String nextValue = sequence.getNextValue(previousValues);
            if (nextValue != null) {
                return nextValue;
            }
        }
        return "";
    }
}
