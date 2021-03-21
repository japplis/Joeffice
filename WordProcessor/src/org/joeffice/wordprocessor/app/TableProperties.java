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
package org.joeffice.wordprocessor.app;

import org.joeffice.wordprocessor.BorderAttributes;
import org.joeffice.wordprocessor.DocxDocument;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents dialog of table and table's elements properties. User can set table to reflect table's attributes. Row to
 * reflect row's attributes. Cell to reflect cell attributes.
 *
 * @author Stanislav Lapitsky
 */
public class TableProperties extends JPanel {

    BorderControl bc = new BorderControl();

    MarginsControl mc = new MarginsControl();

    DoubleSpinEdit widthSpin = new DoubleSpinEdit(10, 500);

    DoubleSpinEdit heightSpin = new DoubleSpinEdit(10, 500);

    DocxDocument.TableElement table;

    DocxDocument.RowElement row;

    DocxDocument.CellElement cell;

    JToggleButton alignLeft = new JToggleButton("Left");
    JToggleButton alignCenter = new JToggleButton("Center");
    JToggleButton alignRight = new JToggleButton("Right");
    ButtonGroup group = new ButtonGroup();

    /**
     * Constructs new dialog instance.
     */
    public TableProperties() {
        super();
        init();
    }

    /**
     * Performs layout of dialog's inner controls.
     */
    protected void init() {
        setLayout(new GridBagLayout());
        JPanel pAlign = new JPanel();
        pAlign.setBorder(new TitledBorder(new EtchedBorder(), "Align:"));
        pAlign.add(alignLeft);
        pAlign.add(alignCenter);
        pAlign.add(alignRight);
        group.add(alignLeft);
        group.add(alignCenter);
        group.add(alignRight);

        add(pAlign, new GridBagConstraints(0, 0, 3, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        add(bc, new GridBagConstraints(0, 1, 1, 3, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel("Width:"), new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
        add(widthSpin, new GridBagConstraints(2, 1, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(10, 0, 0, 0), 0, 0));
        add(new JLabel("Height:"), new GridBagConstraints(1, 2, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
        add(heightSpin, new GridBagConstraints(2, 2, 1, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));
        add(mc, new GridBagConstraints(1, 3, 2, 1, 1, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        JPanel p = new JPanel(new GridLayout(1, 2));
        JButton bOk = new JButton("Ok");
        p.add(bOk);
        ActionListener lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                apply();
                setVisible(false);
            }
        };
        bOk.addActionListener(lst);

        JButton bCancel = new JButton("Cancel");
        p.add(bCancel);
        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        };
        bCancel.addActionListener(lst);
        add(p, new GridBagConstraints(2, 4, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel(""), new GridBagConstraints(2, 5, 1, 1, 0, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * Sets table element. Dialog setVisible(true)s table's borders. Dialog setVisible(true)s table width and height but
     * user can't edit these values (we don't know how to distribute changed space between existing rows and columns).
     * Also we always setVisible(true)s margins equal to 2 because different cell can contain different margins values.
     *
     * @param el table element
     */
    public void setTable(Element el) {
        table = (DocxDocument.TableElement) el;
        widthSpin.setValue(table.getWidth());
        widthSpin.setEnabled(false);
        heightSpin.setValue(table.getHeight());
        heightSpin.setEnabled(false);

        BorderAttributes ba = (BorderAttributes) table.getAttribute("BorderAttributes");
        bc.setBorderAttributes(ba);

        alignLeft.setEnabled(true);
        alignRight.setEnabled(true);
        alignCenter.setEnabled(true);

        int align = StyleConstants.getAlignment(table.getAttributes());
        switch (align) {
            case StyleConstants.ALIGN_LEFT:
                alignLeft.setSelected(true);
                break;
            case StyleConstants.ALIGN_RIGHT:
                alignRight.setSelected(true);
                break;
            case StyleConstants.ALIGN_CENTER:
                alignCenter.setSelected(true);
                break;
        }
    }

    /**
     * Sets row element. Dialog setVisible(true)s row's borders (Row doesn't process horizontal (inner) border so this
     * check box can contains any value). Dialog setVisible(true)s row width and height but user can't edit only height
     * (we set this height to each of cell). Also we always setVisible(true)s margins equal to 2 because different cell
     * can contain different margins values.
     *
     * @param el row element
     */
    public void setRow(Element el) {
        row = (DocxDocument.RowElement) el;
        widthSpin.setValue(row.getWidth());
        widthSpin.setEnabled(false);
        heightSpin.setValue(row.getHeight());
        heightSpin.setEnabled(true);

        BorderAttributes ba = (BorderAttributes) row.getAttribute("BorderAttributes");
        bc.setBorderAttributes(ba);
        alignLeft.setEnabled(false);
        alignRight.setEnabled(false);
        alignCenter.setEnabled(false);
    }

    /**
     * Sets cell element. Dialog setVisible(true)s cell's borders (cell doesn't process horizontal and vertical (inner)
     * border so thess check boxes can contains any values). Dialog setVisible(true)s cell width and height.
     *
     * @param el cell element
     */
    public void setCell(Element el) {
        cell = (DocxDocument.CellElement) el;
        widthSpin.setValue(cell.getWidth());
        widthSpin.setEnabled(true);
        heightSpin.setValue(cell.getHeight());
        heightSpin.setEnabled(true);

        BorderAttributes ba = (BorderAttributes) cell.getAttribute("BorderAttributes");
        bc.setBorderAttributes(ba);

        mc.setMargins(cell.getMargins());
        alignLeft.setEnabled(false);
        alignRight.setEnabled(false);
        alignCenter.setEnabled(false);
    }

    /**
     * Gets border attributes according to user's selection.
     *
     * @return
     */
    public BorderAttributes getBorderAttributes() {
        return bc.getBorderAttributes();
    }

    /**
     * Applays user's changes to current element (Table, row or cell).
     */
    protected void apply() {
        if (table != null) {
            int align = StyleConstants.ALIGN_LEFT;
            if (alignRight.isSelected()) {
                align = StyleConstants.ALIGN_RIGHT;
            }
            if (alignCenter.isSelected()) {
                align = StyleConstants.ALIGN_CENTER;
            }

            table.setAlignment(align);
            table.setMargins(mc.getMargins());
            table.setBorders(getBorderAttributes());

        }
        if (row != null) {
            row.setMargins(mc.getMargins());
            row.setHeight((int) heightSpin.getValue());
            row.setBorders(getBorderAttributes());
        }
        if (cell != null) {
            cell.setMargins(mc.getMargins());
            cell.setHeight((int) heightSpin.getValue());
            cell.setWidth((int) widthSpin.getValue());
            cell.setBorders(getBorderAttributes());
        }
    }
}