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

import org.joeffice.spreadsheet.SpreadsheetComponent;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Ask Rename the selected sheet.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.RenameSheetAction")
@ActionRegistration(
        iconBase = "org/joeffice/spreadsheet/actions/textfield_rename.png",
        displayName = "#CTL_RenameSheetAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Spreadsheet/Tabs/Popup")})
@Messages("CTL_RenameSheetAction=Rename Sheet")
public final class RenameSheetAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        SpreadsheetComponent spreadsheet = SpreadsheetComponent.getSelectedInstance();
        if (spreadsheet != null) {
            String question = NbBundle.getMessage(getClass(), "MSG_AskSheetName");
            NotifyDescriptor.InputLine askName = new NotifyDescriptor.InputLine(question, question, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE);
            String currentSheetName = spreadsheet.getSelectedSheet().getSheet().getSheetName();
            askName.setInputText(currentSheetName);
            Object dialogResult = DialogDisplayer.getDefault().notify(askName);
            if (dialogResult == NotifyDescriptor.OK_OPTION) {
                String sheetName = askName.getInputText();
                spreadsheet.renameCurrentSheet(sheetName);
            }
        }
    }
}
