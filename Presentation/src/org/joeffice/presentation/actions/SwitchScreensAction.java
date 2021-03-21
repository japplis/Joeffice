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
package org.joeffice.presentation.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.presentation.FullScreenFrame;
import org.joeffice.presentation.SlidesTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Action to go to the next screen for the full screen presentation.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "View/Office/Presentation",
        id = "org.joeffice.presentation.actions.SwitchScreenAction")
@ActionRegistration(
        iconBase = "org/joeffice/presentation/actions/application_double.png",
        displayName = "#CTL_SwitchScreenAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Presentation/Toolbar", position = 550)})
@Messages("CTL_SwitchScreenAction=Switch Screens")
public final class SwitchScreensAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent ae) {
        //LaunchPresentationAction launchPresentationAction = FileUtil.getConfigObject(
        //    "Actions/View/Office/Presentation/org-joeffice-presentation-actions-LaunchPresentationAction.instance", LaunchPresentationAction.class);
        //LaunchPresentationAction launchPresentationAction = (LaunchPresentationAction) OfficeUIUtils.getAction("View/Office/Presentation", "org-joeffice-presentation-actions-LaunchPresentationAction");
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            FullScreenFrame presentationFrame = currentTopComponent.getFullScreenFrame();
            if (presentationFrame != null) {
                presentationFrame.nextScreen();
            }
        }
    }
}
