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
package org.joeffice.database;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Anthony Goubard - Japplis
 */
public class HighlightCellRenderer extends DefaultTableCellRenderer {

    private Color evenColor;
    private Color oddColor;

    public HighlightCellRenderer() {
        evenColor = UIManager.getColor("Table.background");
        int colorOffset = evenColor.getBlue() > 200 ? -30 : 30;
        oddColor = new Color(evenColor.getRed(), evenColor.getGreen(), evenColor.getBlue() + colorOffset, evenColor.getAlpha());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component renderComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            if (row % 2 == 0) {
                renderComponent.setBackground(evenColor);
            } else {
                renderComponent.setBackground(oddColor);
            }
        }
        return renderComponent;
    }
}
