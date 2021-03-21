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
package org.joeffice.tools;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import org.jdesktop.swingx.JXList;

/**
 * Test class for the JXList and filtering feature.
 */
public class FilterJXList {

    public static void showDemo(JComponent demo, String title) {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle(title);

        mainFrame.add(demo);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static JPanel createDemoPanel() {
        JPanel listPanel = new JPanel(new BorderLayout(5, 5));
        String[] data = {"hello", "world", "world 2"};
        final JXList list = new JXList(data);
        list.setAutoCreateRowSorter(true);
        final JTextField filterField = new JTextField(30);
        final RowFilter<ListModel, Integer> filter = new RowFilter<ListModel, Integer>() {
            @Override
            public boolean include(RowFilter.Entry<? extends ListModel, ? extends Integer> entry) {
                ListModel listModel = entry.getModel();
                String value = (String) listModel.getElementAt(entry.getIdentifier());
                System.out.println("value " + value);
                return value.contains(filterField.getText());
                /*for (int i = entry.getValueCount() - 1; i >= 0; i--) {
                    String value = entry.getStringValue(i);
                    if (value.contains(filterField.getText())) {
                        return true;
                    }
                }
                return false;*/
            }
        };
        //list.setRowFilter(filter);
        //list.setSortable(true);
        filterField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String text = filterField.getText();
                System.out.println("Filtering with " + text);
                //list.setRowFilter(RowFilter.regexFilter(text));
                list.setRowFilter(filter);
            }
        });
        listPanel.add(filterField, BorderLayout.NORTH);
        listPanel.add(new JScrollPane(list));
        return listPanel;
    }

    public static void main(String[] args) {
        JPanel listPanel = createDemoPanel();
        showDemo(listPanel, "JXList filtering problem");
    }
}