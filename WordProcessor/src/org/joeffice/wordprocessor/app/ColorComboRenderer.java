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
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

/**
 * This class represents a Renderer for color selection combobox If values of combobox elements list are colors then
 * renderer paints apecified color. In other case renderer paints string value of object.
 *
 * @author	Stanislav Lapitsky
 */
public class ColorComboRenderer extends JLabel implements ListCellRenderer {

    /**
     * Current color value for combobox element painting
     */
    protected Color color = Color.black;

    /**
     * color for the focused element
     */
    protected Color focusColor = (Color) UIManager.get("List.selectionBackground");

    /**
     * color for non focused element
     */
    protected Color nonFocusColor = Color.white;

    /**
     * constructs an instance of renderer
     */
    public ColorComboRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object obj, int row,
            boolean sel, boolean hasFocus) {
        if (hasFocus || sel) {
            setBorder(new CompoundBorder(
                    new MatteBorder(2, 10, 2, 10, focusColor),
                    new LineBorder(Color.black)));
        } else {
            setBorder(new CompoundBorder(
                    new MatteBorder(2, 10, 2, 10, nonFocusColor),
                    new LineBorder(Color.black)));
        }

        if (obj instanceof Color) {
            color = (Color) obj;
            setBackground(color);
            setText(" ");
        } else {
            setText(obj.toString());
            setBackground(Color.white);
            color = Color.white;
        }

        return this;
    }

    @Override
    public void paintComponent(Graphics g) {
        setBackground(color);

        super.paintComponent(g);
    }
}
