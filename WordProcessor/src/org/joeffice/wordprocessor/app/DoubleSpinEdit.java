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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * This class implements spinedit functionality with double format of value.
 *
 * @author	Stanislav Lapitsky
 */
public class DoubleSpinEdit extends JPanel {

    /**
     * Text field to user's input.
     */
    protected JTextField textValue;

    /**
     * current value.
     */
    protected double value = 0;

    /**
     * least value of SpinEdit.
     */
    protected double minValue = 0;

    /**
     * greatest value of SpinEdit.
     */
    protected double maxValue = 0;

    /**
     * step value to increase/decrease.
     */
    protected double step = 1;

    /**
     * if user press this button value is increased.
     */
    protected JButton bUp;

    /**
     * if user press this button value is decreased.
     */
    protected JButton bDown;

    /**
     * Constructs new instance with specified minimum and maximum values.
     *
     * @param min minimum value.
     * @param max maximum value.
     */
    public DoubleSpinEdit(double min, double max) {
        this();
        setMaxValue(max);
        setMinValue(min);
    }

    /**
     * Constructs new instance with specified minimum and maximum values and current value.
     *
     * @param min minimum value.
     * @param max maximum value.
     * @param value current value.
     */
    public DoubleSpinEdit(double min, double max, double value) {
        this();
        setMaxValue(max);
        setMinValue(min);
        setValue(value);
    }

    /**
     * Constructs an instance of class. By default value=0 minValue and max Value=0 the same. It means that user can set
     * value field without restrictions to any number.
     */
    public DoubleSpinEdit() {
        super(new BorderLayout());

        textValue = new JTextField("0");
        FocusListener focusLostListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    double v = new Double(textValue.getText()).doubleValue();
                    if ((v <= maxValue) && (v >= minValue) && (minValue < maxValue)) {
                        value = v;
                    } else if (v > maxValue) {
                        value = maxValue;
                    } else if (v < minValue) {
                        value = minValue;
                    }
                } catch (Exception ex) {
                }
                draw();
            }
        };
        textValue.addFocusListener(focusLostListener);
        add(textValue, BorderLayout.CENTER);

        JPanel p = new JPanel(new GridLayout(2, 1));
        bUp = new UpButton();
        bUp.setMargin(new Insets(0, 0, 0, 0));
        bUp.setPreferredSize(new Dimension(20, 5));
        ActionListener upAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((value + step <= maxValue) && (value + step >= minValue) && (minValue < maxValue)) {
                    value += step;
                    draw();
                }
            }
        };
        bUp.addActionListener(upAction);
        p.add(bUp);

        bDown = new DownButton();
        bDown.setMargin(new Insets(0, 0, 0, 0));
        bDown.setPreferredSize(new Dimension(20, 5));
        ActionListener downAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((value - step <= maxValue) && (value - step >= minValue) && (minValue < maxValue)) {
                    value -= step;
                    draw();
                }
            }
        };
        bDown.addActionListener(downAction);
        p.add(bDown);
        add(p, BorderLayout.EAST);
    }

    /**
     * repaint text presentation of double value.
     */
    private void draw() {
        textValue.setText(Double.toString(value));
    }

    /**
     * @return current selected value.
     */
    public double getValue() {
        return value;
    }

    /**
     * Set current value to specified parameter.
     *
     * @param	newValue
     */
    public void setValue(double newValue) {
        value = newValue;
        draw();
    }

    /**
     * @return minimum SpinEdit value.
     */
    public double getMinValue() {
        return minValue;
    }

    /**
     * set minimum SpinEdit value to specified parameter.
     *
     * @param	newValue
     */
    public void setMinValue(double newValue) {
        minValue = newValue;
    }

    /**
     * @return maximum SpinEdit value.
     */
    public double getMaxValue() {
        return maxValue;
    }

    /**
     * set maximum SpinEdit value to specified parameter.
     *
     * @param	newValue
     */
    public void setMaxValue(double newValue) {
        maxValue = newValue;
    }

    /**
     * @return value of increase/decrease step.
     */
    public double getStep() {
        return step;
    }

    /**
     * set value of increase/decrease step.
     *
     * @param	newValue
     */
    public void setStep(double newValue) {
        step = newValue;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textValue.setEnabled(enabled);
        bUp.setEnabled(enabled);
        bDown.setEnabled(enabled);
    }

    //--- inner classes ------------------------------------------------------------

    protected class UpButton extends JButton {

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Color old = g.getColor();
            g.setColor(Color.black);
            Rectangle clip = g.getClipBounds();
            int x = (int) (clip.getX() + clip.getWidth() / 2);
            int y = (int) (clip.getY() + clip.getHeight() / 2);
            g.fillPolygon(new int[]{x - 6, x + 5, x}, new int[]{y + 3, y + 3, y - 3}, 3);
            g.setColor(old);
        }
    }

    protected class DownButton extends JButton {

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            Color old = g.getColor();
            g.setColor(Color.black);
            Rectangle clip = g.getClipBounds();
            int x = (int) (clip.getX() + clip.getWidth() / 2);
            int y = (int) (clip.getY() + clip.getHeight() / 2);
            g.fillPolygon(new int[]{x - 6, x + 5, x}, new int[]{y - 3, y - 3, y + 3}, 3);
            g.setColor(old);
        }
    }
}