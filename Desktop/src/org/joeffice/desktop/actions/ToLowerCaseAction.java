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
package org.joeffice.desktop.actions;

import java.awt.event.ActionEvent;
import java.text.AttributedString;
import javax.swing.AbstractAction;

import org.joeffice.desktop.ui.Styleable;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 * Transformation that set selected text to lower case.
 *
 * @author Anthony Goubard - Japplis
 */
@ActionID(
        category = "Edit/Office",
        id = "org.joeffice.desktop.actions.ToLowerCaseAction")
@ActionRegistration(
        iconBase = "org/joeffice/desktop/actions/text_lowercase.png",
        displayName = "#CTL_ToLowerCaseAction")
@ActionReference(path = "Menu/Edit/Gimme More/Text", position = 40)
@Messages("CTL_ToLowerCaseAction=To Lower Case")
public final class ToLowerCaseAction extends AbstractAction implements TextTransformer {

    private Styleable styleable;

    public ToLowerCaseAction(Styleable styleable) {
        this.styleable = styleable;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttributedString attributes = new AttributedString("ToLowerCase");
        attributes.addAttribute(ExtraTextAttribute.TEXT_TRANSFORM, this);
        styleable.setFontAttributes(attributes);
    }

    @Override
    public String transformText(String text) {
        return text.toLowerCase();
    }
}
