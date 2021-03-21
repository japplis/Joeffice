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
package org.joeffice.database.actions;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.joeffice.database.JDBCTopComponent;
import org.joeffice.database.tablemodel.JDBCSheet;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action that will show the previous results.
 * If the first results are displayed, this action does nothing but beep.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "View/Office/Database",
        id = "org.joeffice.database.actions.PreviousResultsAction")
@ActionRegistration(
        iconBase = "org/joeffice/database/actions/arrow_left.png",
        displayName = "#CTL_PreviousResultsAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Database/Toolbar", position = 100)})
@Messages("CTL_PreviousResultsAction=Previous Results")
public final class PreviousResultsAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        JDBCTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(JDBCTopComponent.class);
        if (currentTopComponent != null) {
            JDBCSheet currentSheet = currentTopComponent.getSelectedTableComponent().getSheet();
            long offset = currentSheet.getOffset();
            if (offset < JDBCSheet.MAX_RESULTS) {
                Toolkit.getDefaultToolkit().beep();
                return;
            } else if (offset < JDBCSheet.MAX_RESULTS) {
                offset = 0;
            } else {
                offset -= JDBCSheet.MAX_RESULTS;
            }
            currentSheet.setOffset(0);
        }
    }
}
