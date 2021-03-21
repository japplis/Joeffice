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

import static org.joeffice.desktop.actions.AlignmentAction.EXTENSION_POINT;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 * Down down to choose the formatter of the selected cell.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Spreadsheet",
        id = "org.joeffice.spreadsheet.actions.FormatActionDropDown")
@ActionRegistration(
        iconBase = "org/joeffice/spreadsheet/actions/style_edit.png",
        displayName = "#CTL_FormatActionDropDown",
        lazy = true)
@ActionReference(path = "Office/Spreadsheet/Toolbar", position = 800)
@Messages("CTL_FormatActionDropDown=Format")
public final class FormatActionDropDown extends AbstractAction implements Presenter.Toolbar {

    public final static String EXTENSION_POINT = "Office/Spreadsheet/Format";

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        JPopupMenu popup = new JPopupMenu();
        for (Action a : Utilities.actionsForPath(EXTENSION_POINT)) {
            popup.add(a);
        }
        ImageIcon topIcon = ImageUtilities.loadImageIcon("org/joeffice/spreadsheet/actions/style_edit.png", false);
        return DropDownButtonFactory.createDropDownButton(topIcon, popup);
    }
}
