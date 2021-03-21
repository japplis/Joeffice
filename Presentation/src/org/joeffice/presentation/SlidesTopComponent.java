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
package org.joeffice.presentation;

import java.awt.CardLayout;
import java.io.*;
import java.util.List;
import java.util.Properties;
import javax.swing.*;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.joeffice.desktop.file.OfficeDataObject;
import org.joeffice.desktop.ui.OfficeTopComponent;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays the toolbar and the presentation slides in edit mode.
 */
@ConvertAsProperties(
        dtd = "-//org.joeffice.presentation//SlidesTopComponent//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "SlidesTopComponent",
        iconBase = "org/joeffice/presentation/presentation-16.png",
        persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ActionID(category = "Window", id = "org.joeffice.presentation.SlidesTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SlidesAction",
        preferredID = "SlidesTopComponent")
@Messages({
    "CTL_SlidesAction=Slides",
    "CTL_SlidesTopComponent=Slides Window",
    "HINT_SlidesTopComponent=This is a Slides window"
})
public final class SlidesTopComponent extends OfficeTopComponent {

    private int selectedSlide;
    private FullScreenFrame fullScreenFrame;

    public SlidesTopComponent() {
    }

    public SlidesTopComponent(OfficeDataObject pptxDataObject) {
        super(pptxDataObject);
    }

    @Override
    protected JComponent createMainComponent() {
        JPanel slidesHolder = new JPanel();
        slidesHolder.setLayout(new CardLayout());
        return slidesHolder;
    }

    @Override
    public String getShortName() {
        return "Presentation";
    }

    @Override
    public Object loadDocument(File pptxFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(pptxFile)) {
            XMLSlideShow presentation = new XMLSlideShow(fis);
            return presentation;
        } catch (IOException ex) {
            throw ex;
        }
    }

    @Override
    public void documentLoaded() {
        List<XSLFSlide> slides = getPresentation().getSlides();
        int slideNumber = 0;
        for (XSLFSlide slide : slides) {
            SlideComponent slideComp = new SlideComponent(slide, this);
            getMainComponent().add(slideComp, String.valueOf(slideNumber));
            slideNumber++;
        }
        getMainComponent().putClientProperty("print.printable", Boolean.TRUE);
        selectedSlide = 0;
        getMainComponent().revalidate();
    }

    public XMLSlideShow getPresentation() {
        return (XMLSlideShow) getDataObject().getDocument();
    }

    public XSLFSlide getSelectedSlide() {
        int currentSlide = getSelectedSlideIndex();
        XSLFSlide slide = getPresentation().getSlides().get(currentSlide);
        return slide;
    }

    public int getSelectedSlideIndex() {
        return selectedSlide;
    }

    public void setSelectedSlideIndex(int selectedSlide) {
        int oldSlide = this.selectedSlide;
        this.selectedSlide = selectedSlide;
        if (selectedSlide >= 0) {
            JComponent mainComponent = getMainComponent();
            CardLayout slidesLayout = (CardLayout) mainComponent.getLayout();
            slidesLayout.show(mainComponent, String.valueOf(selectedSlide));
        }
        firePropertyChange("slideChanged", oldSlide, selectedSlide);
    }

    public FullScreenFrame getFullScreenFrame() {
        return fullScreenFrame;
    }

    public void setFullScreenFrame(FullScreenFrame fullScreenFrame) {
        this.fullScreenFrame = fullScreenFrame;
    }

    @Override
    public void writeProperties(Properties properties) {
        super.writeProperties(properties);
    }

    @Override
    public void readProperties(Properties properties) {
        super.readProperties(properties);
    }
}
