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
import javax.swing.JComboBox;
import javax.swing.JTable;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellUtil;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.cell.CellUtils;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;
import org.joeffice.spreadsheet.sheet.SheetTableModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Set borders to the selected cells.
 *
 * Note that the first version only set a plain black border to the first selected cell.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.SetBordersAction")
@ActionRegistration(
        displayName = "#CTL_SetBordersAction")
@ActionReferences(value = {
    @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 600)})
@Messages({"CTL_SetBordersAction=Set borders",
    "MSG_ChooseBorders=Choose Borders",
    "MSG_Thickness=None,Thin,Medium,Thick",
    "MSG_Color=Black,Red,Blue,Green,Purple"})
public class SetBordersAction implements ActionListener {

    private BorderStyle[] thicknessList;
    private short[] colors;

    private JComboBox<String> thicknessChoice;
    private JComboBox<String> colorChoice;

    public SetBordersAction() {
        thicknessList = new BorderStyle[] { BorderStyle.NONE, BorderStyle.THIN, BorderStyle.MEDIUM, BorderStyle.THICK };
        colors = new short[] { IndexedColors.BLACK.getIndex(), IndexedColors.RED.getIndex(), IndexedColors.BLUE.getIndex(),
            IndexedColors.GREEN.getIndex(), IndexedColors.LAVENDER.getIndex() };

        String[] thicknessTexts = NbBundle.getMessage(getClass(), "MSG_Thickness").split(",");
        thicknessChoice = new JComboBox<>(thicknessTexts);
        String[] colorTexts = NbBundle.getMessage(getClass(), "MSG_Color").split(",");
        colorChoice = new JComboBox<>(colorTexts);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable currentTable = currentTopComponent.getSelectedTable();
            String question = NbBundle.getMessage(getClass(), "MSG_ChooseBorders");
            Object[] options = {thicknessChoice, colorChoice};
            NotifyDescriptor askBorder = new NotifyDescriptor(question, question, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.PLAIN_MESSAGE, null, null);
            askBorder.setMessage(options);
            Object dialogAnswer = DialogDisplayer.getDefault().notify(askBorder);
            if (dialogAnswer == NotifyDescriptor.OK_OPTION) {
                BorderStyle thickness = thicknessList[thicknessChoice.getSelectedIndex()];
                short color = colors[colorChoice.getSelectedIndex()];
                setBorder(currentTable, thickness, color);
            }
        }
    }

    public void setBorder(JTable currentTable, BorderStyle thickness, short color) {
        SheetTableModel tableModel = (SheetTableModel) currentTable.getModel();
        List<Cell> selectedCells = CellUtils.getSelectedCells(currentTable, true);
        for (Cell cell : selectedCells) {
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_TOP, thickness);
            CellUtil.setCellStyleProperty(cell, CellUtil.TOP_BORDER_COLOR, color);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_LEFT, thickness);
            CellUtil.setCellStyleProperty(cell, CellUtil.LEFT_BORDER_COLOR, color);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_BOTTOM, thickness);
            CellUtil.setCellStyleProperty(cell, CellUtil.BOTTOM_BORDER_COLOR, color);
            CellUtil.setCellStyleProperty(cell, CellUtil.BORDER_RIGHT, thickness);
            CellUtil.setCellStyleProperty(cell, CellUtil.RIGHT_BORDER_COLOR, color);
            tableModel.fireTableCellUpdated(cell.getRowIndex(), cell.getColumnIndex());
        }
    }
}
