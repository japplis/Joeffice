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

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class JTableCellSelection {

    public static void showDemo(JComponent demo, String title) {
        JFrame mainFrame = new JFrame();
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setTitle(title);

        mainFrame.add(demo);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        final JTable table = new JTable(10, 10) {
            public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
                System.out.println(rowIndex + "," + columnIndex + "," + toggle + "," + extend);
                super.changeSelection(rowIndex, columnIndex, toggle, extend);
            }
        };
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);
        table.addRowSelectionInterval(6, 7); // Select 2 lines
        /*table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent lse) {
                System.out.println(lse.getFirstIndex() + ";" + lse.getLastIndex() + ";" + table.isCellSelected(8, 2));
            }
        });*/
        showDemo(new JScrollPane(table), "Select a block and some rows");
    }
}