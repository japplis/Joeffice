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
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * This class implements a paragraph settings functionality. Such as alignment, indentation, spacing and other paragraph
 * attributes.
 *
 * @author	Stanislav Lapitsky
 */
public class ParagraphPanel extends JPanel {

    /**
     * return option value
     */
    protected int option = JOptionPane.CLOSED_OPTION;
    /**
     * Paragraph attributes
     */
    protected MutableAttributeSet attributes;
    /**
     * contains line spacing value
     */
    protected DoubleSpinEdit lineSpacing;
    /**
     * contains value of above spacing
     */
    protected DoubleSpinEdit spaceAbove;
    /**
     * contains value of below spacing
     */
    protected DoubleSpinEdit spaceBelow;
    /**
     * contains value of indent of first line of paragraph text
     */
    protected DoubleSpinEdit firstIndent;
    /**
     * contains value of left paragraph indent
     */
    protected DoubleSpinEdit leftIndent;
    /**
     * contains value of right paragraph indent
     */
    protected DoubleSpinEdit rightIndent;
    /**
     * if button is pressed then paragraph alignment sets to the left
     */
    protected JToggleButton btLeft;
    /**
     * if button is pressed then paragraph alignment sets to the center
     */
    protected JToggleButton btCenter;
    /**
     * if button is pressed then paragraph alignment sets to the right
     */
    protected JToggleButton btRight;
    /**
     * if button is pressed then paragraph alignment sets to the justify
     */
    protected JToggleButton btJustified;
    /**
     * reflects paragraph with specified attributes view
     */
    protected JEditorPane preview;

    public ParagraphPanel() {
        init();
    }

    protected void init() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel p = new JPanel(new GridLayout(1, 2, 5, 2));

        JPanel ps = new JPanel(new GridLayout(3, 2, 10, 2));
        ps.setBorder(new TitledBorder(new EtchedBorder(), "Space"));
        ps.add(new JLabel("Line spacing"));
        //space between lines
        lineSpacing = new DoubleSpinEdit();
        lineSpacing.setMinValue(1);
        lineSpacing.setMaxValue(5);
        ps.add(lineSpacing);
        ps.add(new JLabel("Space above"));
        //space above the paragraph
        spaceAbove = new DoubleSpinEdit();
        spaceAbove.setMinValue(0);
        spaceAbove.setMaxValue(500);
        ps.add(spaceAbove);
        ps.add(new JLabel("Space below"));
        //space below the paragraph
        spaceBelow = new DoubleSpinEdit();
        spaceBelow.setMinValue(0);
        spaceBelow.setMaxValue(500);
        ps.add(spaceBelow);
        p.add(ps);

        JPanel pi = new JPanel(new GridLayout(3, 2, 10, 2));
        pi.setBorder(new TitledBorder(new EtchedBorder(), "Indent"));
        pi.add(new JLabel("First line indent"));
        // ident before first line
        firstIndent = new DoubleSpinEdit();
        firstIndent.setMinValue(-500);
        firstIndent.setMaxValue(500);
        pi.add(firstIndent);
        pi.add(new JLabel("Left indent"));
        //left paragraph ident
        leftIndent = new DoubleSpinEdit();
        leftIndent.setMinValue(0);
        leftIndent.setMaxValue(500);
        pi.add(leftIndent);
        pi.add(new JLabel("Right indent"));
        //right paragraph ident
        rightIndent = new DoubleSpinEdit();
        rightIndent.setMinValue(0);
        rightIndent.setMaxValue(500);
        pi.add(rightIndent);
        p.add(pi);
        add(p);

        p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.add(new JLabel("Alignment"));

        ButtonGroup bg = new ButtonGroup();
        btLeft = new JToggleButton("Left");
        bg.add(btLeft);
        p.add(btLeft);
        btCenter = new JToggleButton("Center");
        bg.add(btCenter);
        p.add(btCenter);
        btRight = new JToggleButton("Right");
        bg.add(btRight);
        p.add(btRight);
        btJustified = new JToggleButton("Justified");
        bg.add(btJustified);
        p.add(btJustified);
        add(p);

        p = new JPanel();
        p.setLayout(new BorderLayout());
        p.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
        preview = new JEditorPane();
        preview.setEditorKit(new StyledEditorKit());
        preview.setEnabled(false);
        preview.setText("Previous paragraph\nParagraph preview, paragraph preview, paragraph preview, paragraph preview\nNext paragraph");

        StyledDocument styled = (StyledDocument) preview.getDocument();
        SimpleAttributeSet attr = new SimpleAttributeSet();
        StyleConstants.setFontSize(attr, 6);
        styled.setCharacterAttributes(0, styled.getLength(), attr, false);

        JScrollPane scroll = new JScrollPane(preview);
        p.add(scroll, BorderLayout.CENTER);
        add(p);

        p = new JPanel(); //new FlowLayout());
        //JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
        JButton btOK = new JButton("Ok");
        ActionListener lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*
                 * processing OK button pressing
                 */
                option = JOptionPane.OK_OPTION;
                SwingUtilities.getWindowAncestor(ParagraphPanel.this).dispose();
            }
        };
        btOK.addActionListener(lst);
        //p1.add(btOK);
        p.add(btOK);

        JButton btCancel = new JButton("Cancel");
        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /*
                 * processing CANCEL button pressing
                 */
                option = JOptionPane.CANCEL_OPTION;
                SwingUtilities.getWindowAncestor(ParagraphPanel.this).dispose();
            }
        };
        btCancel.addActionListener(lst);
        //p1.add(btCancel);
        //p.add(p1);
        p.add(btCancel);

        add(p);

        /*
         * when we lost focus of paragraph properties controls we should update
         * paragraph preview.
         */
        FocusListener flst = new FocusListener() {
            public void focusGained(FocusEvent e) {
            }

            public void focusLost(FocusEvent e) {
                updatePreview();
            }
        };
        lineSpacing.addFocusListener(flst);
        spaceAbove.addFocusListener(flst);
        spaceBelow.addFocusListener(flst);
        firstIndent.addFocusListener(flst);
        leftIndent.addFocusListener(flst);
        rightIndent.addFocusListener(flst);

        lst = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updatePreview();
            }
        };
        btLeft.addActionListener(lst);
        btCenter.addActionListener(lst);
        btRight.addActionListener(lst);
        btJustified.addActionListener(lst);
    }

    /**
     * Set paragraph attributes and reflect their in the dialog
     *
     * @param	a settable attributes
     */
    public void setAttributes(AttributeSet a) {
        attributes = new SimpleAttributeSet(a);

        float newValue;

        double value = StyleConstants.getLineSpacing(a);
        if (value < 1) {
            value = 1;
        }
        lineSpacing.setValue(value);

        newValue = StyleConstants.getSpaceAbove(a);
        spaceAbove.setValue(PixelConverter.converPixelsToInches((int) newValue));

        newValue = StyleConstants.getSpaceBelow(a);
        spaceBelow.setValue(PixelConverter.converPixelsToInches((int) newValue));

        newValue = StyleConstants.getFirstLineIndent(a);
        firstIndent.setValue(PixelConverter.converPixelsToInches((int) newValue));

        newValue = StyleConstants.getLeftIndent(a);
        leftIndent.setValue(PixelConverter.converPixelsToInches((int) newValue));

        newValue = StyleConstants.getRightIndent(a);
        rightIndent.setValue(PixelConverter.converPixelsToInches((int) newValue));

        int alignment = StyleConstants.getAlignment(a);
        if (alignment == StyleConstants.ALIGN_LEFT) {
            btLeft.setSelected(true);
        } else if (alignment == StyleConstants.ALIGN_CENTER) {
            btCenter.setSelected(true);
        } else if (alignment == StyleConstants.ALIGN_RIGHT) {
            btRight.setSelected(true);
        } else if (alignment == StyleConstants.ALIGN_JUSTIFIED) {
            btJustified.setSelected(true);
        }

        updatePreview();
    }

    /**
     * @return current paragraph attributes
     */
    public AttributeSet getAttributes() {
        if (attributes == null) {
            return null;
        }

        double newValue;

        newValue = lineSpacing.getValue();
        StyleConstants.setLineSpacing(attributes, (float) newValue);

        newValue = spaceAbove.getValue();
        StyleConstants.setSpaceAbove(attributes, PixelConverter.converInchesToPixels(newValue));

        newValue = spaceBelow.getValue();
        StyleConstants.setSpaceBelow(attributes, PixelConverter.converInchesToPixels(newValue));

        newValue = firstIndent.getValue();
        StyleConstants.setFirstLineIndent(attributes, PixelConverter.converInchesToPixels(newValue));

        newValue = leftIndent.getValue();
        StyleConstants.setLeftIndent(attributes, PixelConverter.converInchesToPixels(newValue));

        newValue = rightIndent.getValue();
        StyleConstants.setRightIndent(attributes, PixelConverter.converInchesToPixels(newValue));

        StyleConstants.setAlignment(attributes, getAlignment());

        return attributes;
    }

    public JDialog showDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, "Paragraph properties", true);
        dialog.setModal(true);
        dialog.add(this);
        dialog.pack();
        dialog.setVisible(true);
        return dialog;
    }

    /**
     * return how user closed the dialog
     */
    public int getOption() {
        return option;
    }

    /**
     * @repaint preview paragraph object to reflect changed attribute set
     */
    protected void updatePreview() {
        AttributeSet a = getAttributes();
        StyledDocument styled = (StyledDocument) preview.getDocument();
        styled.setParagraphAttributes(20, 0, a, false);
//    preview.repaint();
    }

    /**
     * @return paragraph alignment
     *
     */
    protected int getAlignment() {
        if (btLeft.isSelected()) {
            return StyleConstants.ALIGN_LEFT;
        }
        if (btCenter.isSelected()) {
            return StyleConstants.ALIGN_CENTER;
        } else if (btRight.isSelected()) {
            return StyleConstants.ALIGN_RIGHT;
        } else {
            return StyleConstants.ALIGN_JUSTIFIED;
        }
    }
}
