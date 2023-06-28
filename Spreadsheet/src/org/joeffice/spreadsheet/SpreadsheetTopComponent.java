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

import java.io.File;
import java.util.Properties;
import javax.swing.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.joeffice.desktop.file.OfficeDataObject;
import org.joeffice.desktop.ui.OfficeTopComponent;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays the toolbar and the sheets tab panel.
 */
@ConvertAsProperties(
        dtd = "-//org.joeffice.spreadsheet//Spreadsheet//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "SpreadsheetTopComponent",
        iconBase = "org/joeffice/spreadsheet/spreadsheet-16.png",
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.joeffice.spreadsheet.SpreadsheetTopComponent")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SpreadsheetAction",
        preferredID = "SpreadsheetTopComponent")
@Messages({
    "CTL_SpreadsheetAction=Spreadsheet",
    "CTL_SpreadsheetTopComponent=Spreadsheet Window",
    "HINT_SpreadsheetTopComponent=This is a Spreadsheet window"
})
public final class SpreadsheetTopComponent extends OfficeTopComponent {

    private boolean loaded;

    public SpreadsheetTopComponent() {
    }

    public SpreadsheetTopComponent(OfficeDataObject dataObject) {
        init(dataObject);
    }

    @Override
    public String getShortName() {
        return "Spreadsheet";
    }

    @Override
    protected JComponent createMainComponent() {
        SpreadsheetComponent spreadsheet = new SpreadsheetComponent();
        spreadsheet.setSpreadsheetAndToolbar(this);
        return spreadsheet;
    }

    public SpreadsheetComponent getSpreadsheetComponent() {
        return (SpreadsheetComponent) getMainComponent();
    }

    public JTable getSelectedTable() {
        return getSpreadsheetComponent().getSelectedSheet().getTable();
    }

    public Sheet getCurentSheet() {
        return getSpreadsheetComponent().getSelectedSheet().getSheet();
    }

    @Override
    public Object loadDocument(File xslxFile) throws Exception {
        Workbook workbook = JoefficeWorkbookFactory.create(xslxFile);
        return workbook;
    }

    @Override
    public void documentLoaded() {
        ((SpreadsheetComponent) getMainComponent()).load(getWorkbook());
        loaded = true;
        getSpreadsheetComponent().registerActions();
    }

    public Workbook getWorkbook() {
        return (Workbook) getDataObject().getDocument();
    }

    @Override
    protected void componentActivated() {
        if (loaded) {
            getSpreadsheetComponent().registerActions();
        }
        super.componentActivated();
    }

    @Override
    protected void componentDeactivated() {
        getSpreadsheetComponent().unregisterActions();
        super.componentDeactivated();
    }

    @Override
    public void writeProperties(Properties properties) {
        super.writeProperties(properties);
    }

    @Override
    public void readProperties(Properties properties) {
        super.readProperties(properties);
    }
}
