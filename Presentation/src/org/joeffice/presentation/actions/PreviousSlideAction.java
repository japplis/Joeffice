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

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.presentation.SlidesTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Moves to the previous slides.
 * Beep if the current slides is the first slide.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "View/Office/Presentation",
        id = "org.joeffice.presentation.actions.PreviousSlideAction")
@ActionRegistration(
        iconBase = "org/joeffice/presentation/actions/arrow_left.png",
        displayName = "#CTL_PreviousSlideAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Presentation/Toolbar", position = 100),
    @ActionReference(path = "Shortcuts", name = "Page_Up")
})
@Messages("CTL_PreviousSlideAction=Previous Slide")
public class PreviousSlideAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        previousSlide();
    }

    public void previousSlide() {
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            int currentSlide = currentTopComponent.getSelectedSlideIndex();
            if (currentSlide > 0) {
                currentTopComponent.setSelectedSlideIndex(currentSlide - 1);
            } else {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }
}
