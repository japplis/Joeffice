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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.presentation.SlidesTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Removes the currently selected slide.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Presentation",
        id = "org.joeffice.presentation.actions.RemoveSlideAction")
@ActionRegistration(
        iconBase = "org/joeffice/presentation/actions/application_delete.png",
        displayName = "#CTL_RemoveSlideAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Presentation/Toolbar", position = 450)
})
@Messages("CTL_RemoveSlideAction=Remove Slide")
public final class RemoveSlideAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            XMLSlideShow presentation = currentTopComponent.getPresentation();
            int slideIndex = currentTopComponent.getSelectedSlideIndex();
            presentation.removeSlide(slideIndex);
            removeSlideFromPanel(slideIndex, (JPanel) currentTopComponent.getMainComponent());
            currentTopComponent.getDataObject().setModified(true);
            if (slideIndex >= presentation.getSlides().size()) {
                currentTopComponent.setSelectedSlideIndex(slideIndex - 1);
            } else {
                currentTopComponent.setSelectedSlideIndex(slideIndex);
            }
        }
    }

    private void removeSlideFromPanel(int slideIndex, JPanel panel) {
        Component[] slides = panel.getComponents();
        Component[] nextSlides = new Component[slides.length - slideIndex - 1];
        System.arraycopy(slides, slideIndex + 1, nextSlides, 0, nextSlides.length);
        for (int i = slideIndex; i < slides.length; i++) {
            panel.remove(slideIndex);
        }
        for (int i = 0; i < nextSlides.length; i++) {
            Component component = nextSlides[i];
            String indexInCard = String.valueOf(slideIndex + i);
            panel.add(component, indexInCard);
        }
    }
}
