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
package org.joeffice.spreadsheet.actions;

import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 * Factory of format actions.
 *
 * @author Anthony Goubard - Japplis
 */
public class FormatActionFactory {

    /**
     * Return an Action which format cell with currency.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Spreadsheet",
            id = "org.joeffice.spreadsheet.actions.moneyFormat")
    @ActionRegistration(
            iconBase = "org/joeffice/spreadsheet/actions/money.png",
            displayName = "#CTL_moneyFormat")
    @ActionReferences({
        @ActionReference(path = "Office/Spreadsheet/Format", position = 810),
        @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 810)})
    @NbBundle.Messages("CTL_moneyFormat=Money Format")
    public static Action moneyFormat() {
        return new FormatAction("#,##0.## ¤");
    }

    /**
     * Return an Action which format cell as percentage.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Spreadsheet",
            id = "org.joeffice.spreadsheet.actions.percentageFormat")
    @ActionRegistration(
            displayName = "#CTL_percentageFormat")
    @ActionReferences({
        @ActionReference(path = "Office/Spreadsheet/Format", position = 820),
        @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 820)})
    @NbBundle.Messages("CTL_percentageFormat=Percentage Format")
    public static Action percentageFormat() {
        return new FormatAction("#,##0 %");
    }

    /**
     * Return an Action which format cell as date.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Spreadsheet",
            id = "org.joeffice.spreadsheet.actions.dateFormat")
    @ActionRegistration(
            iconBase = "org/joeffice/spreadsheet/actions/date.png",
            displayName = "#CTL_dateFormat")
    @ActionReferences({
        @ActionReference(path = "Office/Spreadsheet/Format", position = 830),
        @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 830)})
    @NbBundle.Messages("CTL_dateFormat=Date Format")
    public static Action dateFormat() {
        return new FormatAction("d-mmm-yy");
    }

    /**
     * Return an Action which format cell as number.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Spreadsheet",
            id = "org.joeffice.spreadsheet.actions.numberFormat")
    @ActionRegistration(
            displayName = "#CTL_numberFormat")
    @ActionReferences({
        @ActionReference(path = "Office/Spreadsheet/Format", position = 840),
        @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 840)})
    @NbBundle.Messages("CTL_numberFormat=Number Format")
    public static Action numberFormat() {
        return new FormatAction("#,##0.##");
    }

    /**
     * Return an Action which format cell for a chosen list.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Spreadsheet",
            id = "org.joeffice.spreadsheet.actions.chooseFormat")
    @ActionRegistration(
            displayName = "#CTL_chooseFormat")
    @ActionReferences({
        @ActionReference(path = "Office/Spreadsheet/Format", position = 850),
        @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 850)})
    @NbBundle.Messages("CTL_chooseFormat=Choose Format")
    public static Action chooseFormat() {
        return new FormatAction("choose");
    }

    /**
     * Return an Action which format cell for a specified text.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Spreadsheet",
            id = "org.joeffice.spreadsheet.actions.defineFormat")
    @ActionRegistration(
            displayName = "#CTL_defineFormat")
    @ActionReferences({
        @ActionReference(path = "Office/Spreadsheet/Format", position = 860),
        @ActionReference(path = "Menu/Edit/Gimme More/Spreadsheet", position = 860)})
    @NbBundle.Messages("CTL_defineFormat=Define Format")
    public static Action defineFormat() {
        return new FormatAction("define");
    }
}
