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

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Remove the selected sheet.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.RemoveSheetAction")
@ActionRegistration(
        iconBase = "org/joeffice/spreadsheet/actions/table_delete.png",
        displayName = "#CTL_RemoveSheetAction")
@ActionReferences(value = {
    /* @ActionReference(path = "Office/Spreadsheet/Toolbar", position = 600), */
    @ActionReference(path = "Office/Spreadsheet/Tabs/Popup")})
@Messages("CTL_RemoveSheetAction=Remove sheet")
public final class RemoveSheetAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        SpreadsheetComponent spreadsheet = SpreadsheetComponent.getSelectedInstance();
        if (spreadsheet != null) {
            spreadsheet.removeCurrentSheet();
        }
    }
}
