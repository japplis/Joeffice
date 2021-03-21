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
package org.joeffice.spreadsheet.rows;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.Action;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.openide.util.Utilities;

/**
 * Listeners for events related to the row table.
 *
 * @author Anthony Goubard - Japplis
 */
public class RowEventsListeners implements PropertyChangeListener, ListSelectionListener, MouseListener, TableModelListener {

    private RowTable rowTable;

    public RowEventsListeners(RowTable rowTable) {
        this.rowTable = rowTable;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String property = evt.getPropertyName();
        if (property.equals("rowHeight")) {
            int newRowHeight = (Integer) evt.getNewValue();
            rowTable.setRowHeight(newRowHeight);
        }
        if (property.equals("singleRowHeight")) {
            int rowChanged = (Integer) evt.getNewValue();
            int newHeight = rowTable.getDataTable().getRowHeight(rowChanged);
            if (newHeight != rowTable.getRowHeight(rowChanged)) {
                rowTable.setRowHeight(rowChanged, newHeight);
            }
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        ListSelectionModel rowSelectionModel = rowTable.getSelectionModel();
        JTable dataTable = rowTable.getDataTable();
        for (int i = lse.getFirstIndex(); i <= lse.getLastIndex(); i++) {
            if (rowSelectionModel.isSelectedIndex(i)) {
                dataTable.addRowSelectionInterval(i, i);
            } else {
                dataTable.removeRowSelectionInterval(i, i);
            }
        }
    }

    private void showPopup(MouseEvent me) {
        if (me.isPopupTrigger()) {
            List<? extends Action> buildActions =
                    Utilities.actionsForPath("Office/Spreadsheet/Rows/Popup");
            JPopupMenu menu = Utilities.actionsToPopup(
                    buildActions.toArray(new Action[buildActions.size()]), me.getComponent());
            menu.show(me.getComponent(), me.getX(), me.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
    }

    @Override
    public void mousePressed(MouseEvent me) {
        showPopup(me);
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        showPopup(me);
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    @Override
    public void tableChanged(TableModelEvent tme) {
        int row = tme.getFirstRow();
        if (tme.getType() == TableModelEvent.INSERT) {
            Object[] data = new Object[] {row + 1};
            ((DefaultTableModel) rowTable.getModel()).insertRow(row + 1, data);
        }
        if (tme.getType() == TableModelEvent.DELETE) {
            ((DefaultTableModel) rowTable.getModel()).removeRow(row);
        }
        if (tme.getType() == TableModelEvent.UPDATE) {
            ((DefaultTableModel) rowTable.getModel()).fireTableStructureChanged();
        }
    }
}
