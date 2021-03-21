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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class constructs simple panel for inserting tables.
 *
 * @author Stanislav Lapitsky
 */
public class InsertTablePanel extends JPanel {

    private JComboBox cbRowCount = new JComboBox(new String[]{"2", "3", "4"});

    private JComboBox cbColCount = new JComboBox(new String[]{"2", "3", "4"});

    private JComboBox cbColors;

    private int option = JOptionPane.CANCEL_OPTION;

    /**
     * Constructs new panel instance.
     */
    public InsertTablePanel() {
        init();
    }

    public JDialog showDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent);
        dialog.setModal(true);
        dialog.setTitle("Insert table dialog");
        dialog.add(this);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    /**
     * Initializes all dialogs' controls (button, edits etc.).
     */
    protected void init() {

        Color[] colors = new Color[4];
        colors[0] = Color.black;
        colors[1] = Color.red;
        colors[2] = Color.green;
        colors[3] = Color.blue;
        cbColors = new JComboBox(colors);
        cbColors.setRenderer(new ColorComboRenderer());
        setLayout(new GridBagLayout());
        add(new JLabel("Row count:"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5, 5, 0, 0), 0, 0));
        add(new JLabel("Column count:"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
        add(new JLabel("Border color:"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));

        add(cbRowCount, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(5, 0, 0, 5), 0, 0));
        add(cbColCount, new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        add(cbColors, new GridBagConstraints(1, 2, 2, 1, 1, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        JPanel p = new JPanel();
        JButton okButton = new JButton("Ok");
        p.add(okButton);
        ActionListener lst = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                option = JOptionPane.OK_OPTION;
                SwingUtilities.getWindowAncestor(InsertTablePanel.this).dispose();
            }
        };
        okButton.addActionListener(lst);

        JButton cancelButton = new JButton("Cancel");
        lst = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                option = JOptionPane.CANCEL_OPTION;
                SwingUtilities.getWindowAncestor(InsertTablePanel.this).dispose();
            }
        };
        cancelButton.addActionListener(lst);
        p.add(cancelButton);

        add(p, new GridBagConstraints(2, 3, 1, 1, 0, 0, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        add(new JLabel(), new GridBagConstraints(1, 4, 1, 1, 0, 1, GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * @return number of rows for table.
     */
    public int getRowCount() {
        return cbRowCount.getSelectedIndex() + 2;
    }

    /**
     * @return number of columns for table.
     */
    public int getColumnCount() {
        return cbColCount.getSelectedIndex() + 2;
    }

    /**
     * @return color tables' borders.
     */
    public Color getColor() {
        return (Color) cbColors.getSelectedItem();
    }

    /**
     * @return user's selection
     */
    public int getOption() {
        return option;
    }
}