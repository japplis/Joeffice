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
import javax.swing.AbstractAction;

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
 * Insert a sheet after the selected sheet.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.InsertSheetAction")
@ActionRegistration(
        iconBase = "org/joeffice/spreadsheet/actions/table_add.png",
        displayName = "#CTL_InsertSheetAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Spreadsheet/Toolbar", position = 500),
    @ActionReference(path = "Office/Spreadsheet/Tabs/Popup"),
    @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 200)})
@Messages({"CTL_InsertSheetAction=Insert sheet",
        "MSG_AskSheetName=Enter the sheet name:",
        "MSG_DefaultSheetName=Page"})
public final class InsertSheetAction extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        SpreadsheetComponent spreadsheet = SpreadsheetComponent.getSelectedInstance();
        if (spreadsheet != null) {
            String question = NbBundle.getMessage(getClass(), "MSG_AskSheetName");
            String defaultName = NbBundle.getMessage(getClass(), "MSG_DefaultSheetName");
            NotifyDescriptor.InputLine askName = new NotifyDescriptor.InputLine(question, question, NotifyDescriptor.OK_CANCEL_OPTION, NotifyDescriptor.QUESTION_MESSAGE);
            askName.setInputText(defaultName);
            Object dialogResult = DialogDisplayer.getDefault().notify(askName);
            if (dialogResult == NotifyDescriptor.OK_OPTION) {
                String sheetName = askName.getInputText();
                spreadsheet.insertSheet(sheetName);
            }
        }
    }
}
