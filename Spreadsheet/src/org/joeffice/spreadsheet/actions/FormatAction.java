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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JTable;

import org.apache.poi.ss.usermodel.*;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.desktop.ui.OfficeUIUtils;
import org.joeffice.spreadsheet.cell.CellUtils;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.joeffice.spreadsheet.sheet.SheetTableModel;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 * Action to apply a specific format on the cell.
 *
 * @author Anthony Goubard - Japplis
 */
public class FormatAction extends AbstractAction {

    private String pattern;
    private boolean choosePattern;
    private boolean definePattern;

    public FormatAction(String pattern) {
        this.pattern = pattern;
        choosePattern = pattern.equals("choose");
        definePattern = pattern.equals("define");
        if (choosePattern || definePattern) {
            this.pattern = "#,###.##";
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable currentTable = currentTopComponent.getSelectedTable();
            SheetTableModel tableModel = (SheetTableModel) currentTable.getModel();
            List<Cell> selectedCells = CellUtils.getSelectedCells(currentTable);
            if (selectedCells.isEmpty()) {
                return;
            }
            if (choosePattern) {
                pattern = askFromList();
            } else if (definePattern) {
                pattern = askFromInputField();
            }
            if (pattern == null) {
                return;
            }
            Workbook workbook = selectedCells.get(0).getSheet().getWorkbook();
            DataFormat format = workbook.createDataFormat();
            short formatIndex = format.getFormat(pattern);
            for (Cell cell : selectedCells) {
                cell.getCellStyle().setDataFormat(formatIndex);
                tableModel.fireTableCellUpdated(cell.getRowIndex(), cell.getColumnIndex());
            }
        }
    }

    @NbBundle.Messages("MSG_chooseFormat=Choose Format")
    private String askFromList() {
        String askFormat = NbBundle.getMessage(FormatActionFactory.class, "MSG_chooseFormat");
        Vector<String> formats = new Vector<>();
        for (String format : BuiltinFormats.getAll()) {
            if (!format.startsWith("reserved")) {
                formats.add(format);
            }
        }
        JComboBox<String> formatsCombo = new JComboBox<>(formats);
        formatsCombo.setSelectedItem(pattern);

        Object dialogAnswer = OfficeUIUtils.ask(askFormat, DialogDescriptor.OK_CANCEL_OPTION, askFormat, formatsCombo);
        if (dialogAnswer == DialogDescriptor.OK_OPTION) {
            String selectedFormat = (String) formatsCombo.getSelectedItem();
            return selectedFormat;
        }
        return null;
    }

    @NbBundle.Messages("MSG_defineFormat=Define the cell format")
    private String askFromInputField() {
        String question = NbBundle.getMessage(getClass(), "MSG_defineFormat");
        NotifyDescriptor.InputLine askFormat = new NotifyDescriptor.InputLine(question, question, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE);
        askFormat.setInputText(pattern);
        Object dialogResult = DialogDisplayer.getDefault().notify(askFormat);
        if (dialogResult == NotifyDescriptor.OK_OPTION) {
            String format = askFormat.getInputText();
            return format;
        }
        return null;
    }
}
