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

import java.awt.*;
import javax.swing.border.AbstractBorder;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Border for POI Cells.
 *
 * @author Anthony Goubard - Japplis
 */
public class CellBorder extends AbstractBorder {

    private CellStyle style;

    public CellBorder(Cell cell) {
        style = cell.getCellStyle();
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        boolean paintBorder = applyBorderColor(g2, style.getBorderBottom(), style.getBottomBorderColor());
        if (paintBorder) {
            g.drawLine(x, y + height - 1, x + width, y + height - 1);
        }
        paintBorder = applyBorderColor(g2, style.getBorderTop(), style.getTopBorderColor());
        if (paintBorder) {
            g.drawLine(x, y, x + width, y);
        }
        paintBorder = applyBorderColor(g2, style.getBorderLeft(), style.getLeftBorderColor());
        if (paintBorder) {
            g.drawLine(x, y, x, y + height);
        }
        paintBorder = applyBorderColor(g2, style.getBorderRight(), style.getRightBorderColor());
        if (paintBorder) {
            g.drawLine(x + width - 1, y, x + width - 1, y + height);
        }
    }

    private boolean applyBorderColor(Graphics2D g2, BorderStyle border, short borderColor) {
        if (border != BorderStyle.NONE) {
            Color awtBorderColor = CellUtils.shortToColor(borderColor);
            if (awtBorderColor == null) {
                awtBorderColor = Color.BLACK;
            }
            g2.setColor(awtBorderColor);
            if (border == BorderStyle.THIN) {
                g2.setStroke(new BasicStroke(0.5F));
            } else if (border == BorderStyle.THICK) {
                g2.setStroke(new BasicStroke(2.0F));
            } else {
                g2.setStroke(new BasicStroke(1.0F));
            }
            return true;
        }
        return false;
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        insets.set(0, 1, 0, 1);
        return insets;
    }
}
