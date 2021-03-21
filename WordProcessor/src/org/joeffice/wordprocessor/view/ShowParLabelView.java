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
package org.joeffice.wordprocessor.view;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.LabelView;
import javax.swing.text.Position;

/**
 * Label view that has the options to show all characters.
 *
 * @author Stanislav Lapitsky
 */
public class ShowParLabelView extends LabelView {

    public ShowParLabelView(Element elem) {
        super(elem);
    }

    @Override
    public void paint(Graphics g, Shape a) {
        boolean isShowParagraphs = getDocument().getProperty("show paragraphs") != null;

        super.paint(g, a);
        if (isShowParagraphs) {
            try {
                Rectangle r = a instanceof Rectangle ? (Rectangle) a : a.getBounds();
                String labelStr = getDocument().getText(getStartOffset(), getEndOffset() - getStartOffset());
                int x0 = modelToView(getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x;
                for (int i = 0; i < labelStr.length(); i++) {
                    int x = modelToView(i + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
                    char c = labelStr.charAt(i);
                    if (c == '\n') {
                        String s = "\u00B6";
                        g.setFont(getFont());
                        int w = g.getFontMetrics().stringWidth(s);
                        Rectangle clip = new Rectangle(r.x + x, r.y, 2 * w, r.height);
                        Shape oldClip = g.getClip();
                        g.setClip(clip);
                        g.drawString(s, r.x + x, r.y + g.getFontMetrics().getMaxAscent());
                        g.setClip(oldClip);
                    } else if (c == '\r') {
                        int w = 5;
                        Rectangle clip = new Rectangle(r.x + x, r.y, 2 * w, r.height);
                        Shape oldClip = g.getClip();
                        g.setClip(clip);
                        g.drawLine(r.x + x, r.y + r.height / 2, r.x + x + w, r.y + r.height / 2);
                        g.drawLine(r.x + x, r.y + r.height / 2, r.x + x + 3, r.y + r.height / 2 + 3);
                        g.drawLine(r.x + x, r.y + r.height / 2, r.x + x + 3, r.y + r.height / 2 - 3);

                        g.drawLine(r.x + x + w, r.y + r.height / 2, r.x + x + w, r.y + 2);
                        g.setClip(oldClip);
                    } else if (c == '\t') {
                        int x2 = modelToView(i + 1 + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
                        int w = Math.min(x2 - x, 10);
                        Rectangle clip = new Rectangle(r.x + x, r.y, x2 - x, r.height);
                        Shape oldClip = g.getClip();
                        g.setClip(clip);
                        x = x + (x2 - x - w) / 2;
                        g.drawLine(r.x + x, r.y + r.height / 2, r.x + x + w, r.y + r.height / 2);
                        g.drawLine(r.x + x + w, r.y + r.height / 2, r.x + x + w - 3, r.y + r.height / 2 + 3);
                        g.drawLine(r.x + x + w, r.y + r.height / 2, r.x + x + w - 3, r.y + r.height / 2 - 3);

                        g.setClip(oldClip);
                    } else if (c == ' ') {
                        int x2 = modelToView(i + 1 + getStartOffset(), new Rectangle(r.width, r.height), Position.Bias.Forward).getBounds().x - x0;
                        int w = 2;
                        Rectangle clip = new Rectangle(r.x + x, r.y, 2 * w, r.height);
                        Shape oldClip = g.getClip();
                        g.setClip(clip);
                        x = x + (x2 - x - w) / 2;
                        g.drawLine(r.x + x, r.y + r.height / 2, r.x + x + w, r.y + r.height / 2);
                        g.drawLine(r.x + x, r.y + r.height / 2 + 1, r.x + x + w, r.y + r.height / 2 + 1);
                        g.setClip(oldClip);
                    }
                }
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}
