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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.apache.poi.xslf.usermodel.XSLFNotes;
import org.apache.poi.xslf.usermodel.XSLFSlide;

import org.joeffice.desktop.ui.OfficeTopComponent;

import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays the current selected slide notes.
 */
@ConvertAsProperties(
        dtd = "-//org.joeffice.presentation//SlideNotes//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "SlideNotesTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "commonpalette", openAtStartup = false)
@ActionID(category = "Window", id = "org.joeffice.presentation.SlideNotesTopComponent")
@ActionReference(path = "Menu/Window", position = 720)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_SlideNotesAction",
        preferredID = "SlideNotesTopComponent")
@Messages({
    "CTL_SlideNotesAction=Slide Notes",
    "CTL_SlideNotesTopComponent=Slide Notes Window",
    "HINT_SlideNotesTopComponent=This is a Slide Notes window"
})
public final class SlideNotesTopComponent extends TopComponent implements PropertyChangeListener {

    private SlidesTopComponent slidesEditor;

    public SlideNotesTopComponent() {
        initComponents();
        setName(Bundle.CTL_SlideNotesTopComponent());
        setToolTipText(Bundle.HINT_SlideNotesTopComponent());
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        slidesEditor = OfficeTopComponent.getSelectedComponent(SlidesTopComponent.class);
        if (slidesEditor == null) {
            return;
        }
        XSLFSlide selectedSlide = slidesEditor.getSelectedSlide();
        XSLFNotes notes = selectedSlide.getNotes();
        SlideComponent notesComponent = new SlideComponent(notes, slidesEditor, new Dimension(300, 200));
        add(notesComponent);
        slidesEditor.addPropertyChangeListener("slideChanged", this);
        putClientProperty("print.printable", Boolean.TRUE);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        int newSlideIndex = (Integer) evt.getNewValue();
        remove(0);
        XSLFSlide selectedSlide = slidesEditor.getPresentation().getSlides().get(newSlideIndex);
        XSLFNotes notes = selectedSlide.getNotes();
        SlideComponent notesComponent = new SlideComponent(notes, slidesEditor, new Dimension(300, 200));
        add(notesComponent);
        revalidate();
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
