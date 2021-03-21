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

import static org.openide.util.actions.BooleanStateAction.PROP_BOOLEAN_STATE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTable;

import org.apache.poi.ss.usermodel.Sheet;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.BooleanStateAction;

/**
 * Action to show or hide the grid.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "View/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.ShowHideGridAction")
@ActionRegistration(
        iconBase = "org/joeffice/spreadsheet/actions/application_xp.png",
        displayName = "#CTL_ShowHideGridAction",
        lazy = false)
@ActionReference(path = "Office/Spreadsheet/Toolbar", position = 600)
@Messages({"CTL_ShowHideGridAction=Show / Hide grid", "CTL_ShowGrid=Show Grid", "CTL_HideGrid=Hide Grid"})
public final class ShowHideGridAction extends BooleanStateAction implements PropertyChangeListener {

    public ShowHideGridAction() {
        setBooleanState(false);
        addPropertyChangeListener(this);
    }

    @Override
    public String getName() {
        String name = NbBundle.getMessage(getClass(), "CTL_ShowHideGridAction");
        return name;
    }

    @Override
    protected String iconResource() {
        return "org/joeffice/spreadsheet/actions/application_xp.png";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROP_BOOLEAN_STATE.equals(evt.getPropertyName())) {
            SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
            if (currentTopComponent != null) {
                JTable currentTable = currentTopComponent.getSelectedTable();
                boolean enabled = (Boolean) evt.getNewValue();
                currentTable.setShowGrid(!enabled);
                Sheet sheet = currentTopComponent.getSpreadsheetComponent().getSelectedSheet().getSheet();
                sheet.setPrintGridlines(!enabled);
            }
        }
    }
}
