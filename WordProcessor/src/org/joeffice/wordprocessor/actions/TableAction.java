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
package org.joeffice.wordprocessor.actions;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import org.openide.awt.*;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 * Drop down for the Word processor table actions.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Word Processor",
        id = "org.joeffice.wordprocessor.actions.TableAction")
@ActionRegistration(
        iconBase = "org/joeffice/wordprocessor/actions/table.png",
        displayName = "#CTL_TableAction",
        lazy = true)
@ActionReference(path = "Office/Word Processor/Toolbar", position = 400, separatorBefore = 390)
@Messages("CTL_TableAction=Table")
public final class TableAction implements ActionListener, Presenter.Toolbar {

    public final static String EXTENSION_POINT = "Menu/Edit/Gimme More/Word Processor/Table";

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("test");
    }

    @Override
    public Component getToolbarPresenter() {
        JPopupMenu popup = new JPopupMenu();
        for (Action a : Utilities.actionsForPath(EXTENSION_POINT)) {
            popup.add(a);
        }
        ImageIcon topIcon = ImageUtilities.loadImageIcon("org/joeffice/wordprocessor/actions/table.png", false);
        return DropDownButtonFactory.createDropDownButton(topIcon, popup);
    }
}

