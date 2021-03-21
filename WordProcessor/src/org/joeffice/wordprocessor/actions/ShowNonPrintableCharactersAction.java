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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JTextPane;
import javax.swing.text.Document;

import org.joeffice.wordprocessor.WordProcessorTopComponent;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.BooleanStateAction;

/**
 * Action that will show (or hide) all the non printable characters of the editor.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "View/Office/Word Processor",
        id = "org.joeffice.wordprocessor.actions.ShowNonPrintableCharactersAction")
@ActionRegistration(
        iconBase = "org/joeffice/wordprocessor/actions/pilcrow.png",
        displayName = "#CTL_ShowNonPrintableCharactersAction",
        lazy = true)
@ActionReferences({
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor", position = 800),
    @ActionReference(path = "Office/Word Processor/Toolbar", position = 300)})
@Messages("CTL_ShowNonPrintableCharactersAction=Show Non Printable Characters")
public final class ShowNonPrintableCharactersAction extends BooleanStateAction implements PropertyChangeListener {

    public ShowNonPrintableCharactersAction() {
        setBooleanState(false);
        addPropertyChangeListener(this);
    }

    @Override
    public String getName() {
        String name = NbBundle.getMessage(getClass(), "CTL_ShowNonPrintableCharactersAction");
        return name;
    }

    @Override
    protected String iconResource() {
        return "org/joeffice/wordprocessor/actions/pilcrow.png";
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROP_BOOLEAN_STATE.equals(evt.getPropertyName())) {
            JTextPane editor = WordProcessorTopComponent.findCurrentTextPane();
            Document doc = editor.getDocument();
            boolean enabled = (Boolean) evt.getNewValue();
            if (enabled) {
                doc.putProperty("show paragraphs", Boolean.TRUE);
            } else {
                doc.putProperty("show paragraphs", null);
            }
            editor.repaint();
        }
    }
}
