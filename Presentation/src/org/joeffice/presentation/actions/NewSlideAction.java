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
import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.apache.poi.xslf.usermodel.*;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.desktop.ui.OfficeUIUtils;
import org.joeffice.presentation.SlideComponent;
import org.joeffice.presentation.SlidesTopComponent;
import org.openide.DialogDescriptor;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * Adds a new slide.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office/Presentation",
        id = "org.joeffice.presentation.actions.NewSlideAction")
@ActionRegistration(
        iconBase = "org/joeffice/presentation/actions/application_add.png",
        displayName = "#CTL_NewSlideAction")
@ActionReferences(value = {
    @ActionReference(path = "Office/Presentation/Toolbar", position = 400),
    @ActionReference(path = "Shortcuts", name = "D-Plus")
})
@Messages({"CTL_NewSlideAction=Add Slide",
    "MSG_ChooseLayout=Choose Layout",
    "MSG_EnterTextHere=Enter Text Here"})
public final class NewSlideAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        SlidesTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (currentTopComponent != null) {
            XMLSlideShow presentation = currentTopComponent.getPresentation();
            XSLFSlideLayout slideLayout = getSlideLayout(presentation);
            if (slideLayout != null) {
                XSLFSlide newSlide = presentation.createSlide(slideLayout);
                fillWithText(newSlide);
                int selectedSlide = currentTopComponent.getSelectedSlideIndex();
                presentation.setSlideOrder(newSlide, selectedSlide + 1);

                SlideComponent slideComp = new SlideComponent(newSlide, currentTopComponent);
                addSlideToPanel(slideComp, (JPanel) currentTopComponent.getMainComponent(), selectedSlide + 1);
                currentTopComponent.setSelectedSlideIndex(selectedSlide + 1);
                currentTopComponent.getDataObject().setModified(true);
            }
        }
    }

    private XSLFSlideLayout getSlideLayout(XMLSlideShow presentation) {
        XSLFSlideMaster defaultMaster = presentation.getSlideMasters().get(0);
        String askLayout = NbBundle.getMessage(getClass(), "MSG_ChooseLayout");
        JComboBox<String> layoutsCombo = new JComboBox<>();
        for (XSLFSlideLayout layout : defaultMaster.getSlideLayouts()) {
            layoutsCombo.addItem(layout.getName());
        }

        Object dialogAnswer = OfficeUIUtils.ask(askLayout, DialogDescriptor.OK_CANCEL_OPTION, askLayout, layoutsCombo);
        if (dialogAnswer == DialogDescriptor.OK_OPTION) {
            int selectedIndex = layoutsCombo.getSelectedIndex();
            XSLFSlideLayout slideLayout = defaultMaster.getSlideLayouts()[selectedIndex];
            return slideLayout;
        }
        return null;
    }

    private void addSlideToPanel(SlideComponent slideComp, JPanel panel, int indexNewSlide) {
        Component[] slides = panel.getComponents();
        Component[] nextSlides = new Component[slides.length - indexNewSlide];
        System.arraycopy(slides, indexNewSlide, nextSlides, 0, slides.length - indexNewSlide);
        for (int i = indexNewSlide; i < slides.length; i++) {
            panel.remove(i);
        }
        panel.add(slideComp, String.valueOf(indexNewSlide));
        for (int i = 0; i < nextSlides.length; i++) {
            Component component = nextSlides[i];
            String indexInCard = String.valueOf(indexNewSlide + 1 + i);
            panel.add(component, indexInCard);
        }
    }

    private void fillWithText(XSLFSheet slide) {
        String message = NbBundle.getMessage(getClass(), "MSG_EnterTextHere");
        XSLFTextShape[] textShapes = slide.getPlaceholders();
        for (XSLFTextShape textShape : textShapes) {
            textShape.setText(message);
        }
    }
}
