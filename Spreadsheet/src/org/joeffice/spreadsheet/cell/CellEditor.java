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
package org.joeffice.spreadsheet.cell;

import java.awt.Component;
import javax.swing.*;
import javax.swing.table.TableCellEditor;
import org.apache.poi.ss.usermodel.Cell;

/**
 * Editor for POI Cell objects.
 *
 * @author Anthony Goubard - Japplis
 */
public class CellEditor extends DefaultCellEditor implements TableCellEditor {

    public final static CellEditor DEFAULT_EDITOR = new CellEditor();

    public CellEditor() {
        super(new JTextField());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        super.getTableCellEditorComponent(table, value, isSelected, row, column);
        if (value != null) {
            JComponent defaultComponent = (JComponent) DEFAULT_EDITOR.
                    getTableCellEditorComponent(table, null, isSelected, row, column);
            Cell cell = (Cell) value;
            ((JTextField) getComponent()).setText(CellUtils.getFormattedText(cell));
            CellRenderer.decorateComponent(cell, (JComponent) getComponent(), defaultComponent);
        }
        return getComponent();
    }
}
