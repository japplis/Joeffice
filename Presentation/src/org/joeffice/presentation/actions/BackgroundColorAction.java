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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.presentation.SlideComponent;
import org.joeffice.presentation.SlidesTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * Set the background color of the slide.
 * Do not use as POI throws an exception.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Presentation",
        id = "org.joeffice.presentation.actions.BackgroundColorAction")
@ActionRegistration(
        displayName = "#CTL_BackgroundColorAction")
@Messages("CTL_BackgroundColorAction=Background color")
public final class BackgroundColorAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            String title = NbBundle.getMessage(getClass(), "CTL_BackgroundColorAction");
            Color chosenColor = JColorChooser.showDialog(WindowManager.getDefault().getMainWindow(), title, Color.WHITE);
            if (chosenColor != null) {
                int currentSlide = currentTopComponent.getSelectedSlideIndex();
                XSLFSlide slide = currentTopComponent.getSelectedSlide();
                // This throws java.lang.IllegalStateException: CTShapeProperties was not found.
                slide.getBackground().setFillColor(chosenColor);

                // Also change the background of the panel
                JPanel mainComponent = (JPanel) currentTopComponent.getMainComponent();
                SlideComponent slidePanel = (SlideComponent) mainComponent.getComponent(currentSlide);
                slidePanel.setBackground(chosenColor);
            }
        }
    }
}
