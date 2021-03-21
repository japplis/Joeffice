/*
 * Copyright 2021.
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
package org.joeffice.desktop.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "File",
        id = "org.joeffice.desktop.actions.OpenRecentAction"
)
@ActionRegistration(
        displayName = "#CTL_OpenRecentAction",
        lazy = true
)
@ActionReference(path = "Menu/File", position = 750)
@Messages("CTL_OpenRecentAction=Open Recent...")
public final class OpenRecentAction implements ActionListener, Presenter.Menu {

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu recentFileMenu = new JMenu();
        recentFileMenu.setText(NbBundle.getMessage(OpenRecentAction.class, "CTL_OpenRecentAction"));
        return recentFileMenu;
    }
}
