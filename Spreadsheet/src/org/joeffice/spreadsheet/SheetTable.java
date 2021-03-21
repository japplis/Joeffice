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
package org.joeffice.spreadsheet;

import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.JTable;
import javax.swing.table.TableModel;

/**
 * A JTable with extra features.
 * This class is only to fix bugs or improve existing functionalities.
 *
 * @author Anthony Goubard - Japplis
 */
public class SheetTable extends JTable {

    private Map<Integer, Set<Integer>> selectedCells = new HashMap<>();
    private Point firstExtendCell;
    private Point lastExtendCell;

    public SheetTable(TableModel tableModel) {
        super(tableModel);
    }

    @Override
    public void setRowHeight(int row, int rowHeight) {
        int oldRowHeight = getRowHeight(row);
        super.setRowHeight(row, rowHeight);
        // Fire the row changed
        firePropertyChange("singleRowHeight", oldRowHeight, row);
    }

    @Override
    public void changeSelection(int rowIndex, int columnIndex, boolean toggle, boolean extend) {
        if (toggle && isCellSelected(rowIndex, columnIndex) && !extend && selectedCells.containsKey(rowIndex)) {
            selectedCells.get(rowIndex).remove(columnIndex);
        } else {
            if (!toggle && !extend) {
                lastExtendCell = null;
                selectedCells.clear();
            }
            addCellToSelection(rowIndex, columnIndex);
            Point extendCell = new Point(rowIndex, columnIndex);
            if (!extend) {
                firstExtendCell = extendCell;
                lastExtendCell = null;
            } else if (lastExtendCell == null || !extendCell.equals(lastExtendCell)) {
                for (int i = Math.min(firstExtendCell.x, rowIndex); i <= Math.max(firstExtendCell.x, rowIndex); i++) {
                    for (int j = Math.min(firstExtendCell.y, columnIndex); j <= Math.max(firstExtendCell.y, columnIndex); j++) {
                        addCellToSelection(i, j);
                    }
                }
                lastExtendCell = extendCell;
            }
        }
        super.changeSelection(rowIndex, columnIndex, toggle, extend);
    }

    private void addCellToSelection(int row, int column) {
        Set<Integer> selectedColumns = selectedCells.get(row);
        if (selectedColumns == null) {
            selectedColumns = new TreeSet<>();
            selectedCells.put(row, selectedColumns);
        }
        selectedColumns.add(column);
    }

    @Override
    public void addRowSelectionInterval(int index0, int index1) {
        for (int i = index0; i <= index1; i++) {
            selectedCells.remove(i);
        }
        getColumnModel().getSelectionModel().addSelectionInterval(0, getColumnCount() - 1);
        super.addRowSelectionInterval(index0, index1);
    }

    @Override
    public void removeRowSelectionInterval(int index0, int index1) {
        for (int i = index0; i <= index1; i++) {
            selectedCells.remove(i);
        }
        super.removeRowSelectionInterval(index0, index1);
    }

    @Override
    public void selectAll() {
        selectedCells.clear();
        super.selectAll();
    }

    @Override
    public void clearSelection() {
        if (selectedCells != null) {
            selectedCells.clear();
        }
        super.clearSelection();
    }

    @Override
    public boolean isCellSelected(int row, int column) {
        if (!getSelectionModel().isSelectedIndex(row)) {
            return false;
        }
        if (getSelectionModel().isSelectedIndex(row) && selectedCells.get(row) == null) {
            return true;
        }
        return selectedCells.get(row).contains(column);
    }

    @Override
    public void print(Graphics g) {
        boolean showHGrid = getShowHorizontalLines();
        boolean showVGrid = getShowVerticalLines();
        setShowGrid(false);
        super.print(g);
        setShowHorizontalLines(showHGrid);
        setShowVerticalLines(showVGrid);
    }

}
