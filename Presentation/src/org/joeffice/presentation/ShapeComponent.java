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
package org.joeffice.presentation;


import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.poi.sl.usermodel.PaintStyle;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;
import org.joeffice.desktop.actions.EditorStyleable;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.desktop.ui.Styleable;
import org.openide.awt.UndoRedo;

import org.openide.util.Exceptions;

/**
 * Component that displays a presentation shape.
 *
 * @author Anthony Goubard - Japplis
 */
public class ShapeComponent extends JPanel implements DocumentListener {

    private XSLFShape shape;
    private SlideComponent slideComponent;
    private boolean editable;
    private Styleable styleable;

    public ShapeComponent(XSLFShape shape, SlideComponent slideComponent) {
        this.shape = shape;
        this.slideComponent = slideComponent;
        setOpaque(false);
        // setBorder(BorderFactory.createLineBorder(Color.RED)); // for debug

        Rectangle shapeBounds = shape.getAnchor().getBounds();
        double scale = slideComponent.getScale();
        Rectangle.Double scaledBounds = new Rectangle.Double(shapeBounds.x * scale, shapeBounds.y * scale, shapeBounds.width * scale, shapeBounds.height * scale);
        setBounds(scaledBounds.getBounds());

        setOpaque(false);
        setLayout(new BorderLayout());
        editable = slideComponent.getSlidesComponent() != null;
        initComponent();
    }

    private void initComponent() {
        if (shape instanceof XSLFTextShape &&
                (!"".equals(((XSLFTextShape) shape).getText().trim()) ||
                ((XSLFTextShape) shape).getSheet() instanceof XSLFNotes)) {
            handleTextShape((XSLFTextShape) shape);
        } else {
            double scale = slideComponent.getScale();
            BufferedImage img = shapeToImage(shape, scale);
            JLabel shapeLabel = new JLabel(new ImageIcon(img));
            // shapeLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE)); // for debug
            add(shapeLabel);
        }
    }

    public static BufferedImage shapeToImage(XSLFShape shape, double scale) {
        BufferedImage img = new BufferedImage((int) (shape.getAnchor().getWidth() * scale), (int) (shape.getAnchor().getHeight() * scale), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D graphics = img.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.translate(-shape.getAnchor().getX(), -shape.getAnchor().getY());
        Rectangle2D.Double bounds = new Rectangle2D.Double(shape.getAnchor().getX(), shape.getAnchor().getY(), img.getWidth(), img.getHeight());
        shape.draw(graphics, bounds);
        graphics.dispose();
        return img;
    }

    private void handleTextShape(XSLFTextShape textShape) {
        final JTextPane textField = new JTextPane();
        textField.setBorder(BorderFactory.createEmptyBorder());
        textField.setOpaque(false);
        textField.getActionMap().remove(DefaultEditorKit.pageDownAction); // used to move to next or previous slide
        textField.getActionMap().remove(DefaultEditorKit.pageUpAction);
        List<XSLFTextParagraph> paragraphs = textShape.getTextParagraphs();
        boolean newLine = false;
        for (XSLFTextParagraph paragraph : paragraphs) {
            applyAlignment(paragraph, textField);
            List<XSLFTextRun> textParts = paragraph.getTextRuns();
            String simpleBullet = getBullet(paragraph);
            for (XSLFTextRun textPart : textParts) {
                try {
                    String text = simpleBullet + textPart.getRawText();
                    if (!simpleBullet.isEmpty()) simpleBullet = ""; // One bullet per paragraph
                    AttributeSet attributes = null;
                    try {
                        attributes = getFontAttributes(textPart);
                    } catch (Exception ex) {
                        // ignore
                    }
                    if (newLine) {
                        text = "\r\n" + text;
                        newLine = false;
                    }
                    int documentLength = textField.getDocument().getLength();
                    textField.getDocument().insertString(documentLength, text, attributes);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            newLine = true;
        }

        add(textField);
        if (editable) {
            textField.getDocument().addDocumentListener(this);
            SlidesTopComponent slidesTopComponent = (SlidesTopComponent) getSlideComponent().getSlidesComponent();
            textField.getDocument().addUndoableEditListener((UndoRedo.Manager) slidesTopComponent.getUndoRedo());
            textField.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent e) {
                    registerActions(textField);
                }

                @Override
                public void focusLost(FocusEvent e) {
                    unregisterActions();
                }
            });
        } else {
            textField.setEditable(false);
        }
    }

    private AttributeSet getFontAttributes(XSLFTextRun textPart) {
        SimpleAttributeSet attributes = new SimpleAttributeSet();
        String fontFamily = textPart.getFontFamily();
        if (fontFamily != null) {
            StyleConstants.setFontFamily(attributes, fontFamily);
        }
        PaintStyle textColor = textPart.getFontColor();
        if (textColor instanceof PaintStyle.SolidPaint) {
            StyleConstants.setForeground(attributes, ((PaintStyle.SolidPaint) textColor).getSolidColor().getColor());
        }
        double fontSize = textPart.getFontSize();
        if (fontSize > 0) {
            fontSize = fontSize * slideComponent.getScale();
            StyleConstants.setFontSize(attributes, (int) fontSize);
        }
        boolean italic = textPart.isItalic();
        if (italic) {
            StyleConstants.setItalic(attributes, true);
        }
        boolean bold = textPart.isBold();
        if (bold) {
            StyleConstants.setBold(attributes, true);
        }
        boolean underlined = textPart.isUnderlined();
        if (underlined) {
            StyleConstants.setUnderline(attributes, true);
        }
        boolean strikeThrough = textPart.isStrikethrough();
        if (strikeThrough) {
            StyleConstants.setStrikeThrough(attributes, true);
        }
        boolean subScript = textPart.isSubscript();
        if (subScript) {
            StyleConstants.setSubscript(attributes, true);
        }
        boolean superScript = textPart.isSuperscript();
        if (superScript) {
            StyleConstants.setSuperscript(attributes, true);
        }
        return attributes;
    }

    private void applyAlignment(XSLFTextParagraph paragraph, JTextPane textField) {
        TextParagraph.TextAlign alignment;
        try {
            alignment = paragraph.getTextAlign();
        } catch (Exception ex) {
            return;
        }
        if (alignment == null) return;
        switch (alignment) {
            case CENTER:
                align(textField, StyleConstants.ALIGN_CENTER);
                break;
            case RIGHT:
                align(textField, StyleConstants.ALIGN_RIGHT);
                break;
            case LEFT:
                align(textField, StyleConstants.ALIGN_LEFT);
                break;
            case JUSTIFY:
            case JUSTIFY_LOW:
                align(textField, StyleConstants.ALIGN_JUSTIFIED);
                break;
        }
    }

    private void align(JTextPane textField, int swingAlignment) {
        StyledDocument doc = textField.getStyledDocument();
        SimpleAttributeSet alignmentAttibute = new SimpleAttributeSet();
        StyleConstants.setAlignment(alignmentAttibute, swingAlignment);
        doc.setParagraphAttributes(0, doc.getLength(), alignmentAttibute, false);
    }

    private String getBullet(XSLFTextParagraph paragraph) {
        if (paragraph.isBullet()) {
            return paragraph.getBulletCharacter();
        }
        return ""; // No bullets
    }

    public SlideComponent getSlideComponent() {
        return slideComponent;
    }

    @Override
    public void insertUpdate(DocumentEvent de) {
        changedUpdate(de);
    }

    @Override
    public void removeUpdate(DocumentEvent de) {
        changedUpdate(de);
    }

    @Override
    public void changedUpdate(DocumentEvent de) {
        try {
            ((XSLFTextShape) shape).setText(de.getDocument().getText(0, de.getDocument().getLength()));
            OfficeTopComponent topComponent = (OfficeTopComponent) getSlideComponent().getSlidesComponent();
            topComponent.getDataObject().setModified(true);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void registerActions(JTextPane textField) {
        OfficeTopComponent topComponent = (OfficeTopComponent) getSlideComponent().getSlidesComponent();
        ActionMap topComponentActions = topComponent.getActionMap();
        ActionMap textFieldActions = textField.getActionMap();

        // Actives the cut / copy / paste buttons
        topComponentActions.put(DefaultEditorKit.cutAction, textFieldActions.get(DefaultEditorKit.cutAction));
        topComponentActions.put(DefaultEditorKit.copyAction, textFieldActions.get(DefaultEditorKit.copyAction));
        topComponentActions.put(DefaultEditorKit.pasteAction, textFieldActions.get(DefaultEditorKit.pasteAction));
        styleable = new EditorStyleable(textField);
        topComponent.getServices().add(styleable);
    }

    public void unregisterActions() {
        OfficeTopComponent topComponent = (OfficeTopComponent) getSlideComponent().getSlidesComponent();
        ActionMap topComponentActions = topComponent.getActionMap();

        // Deactivates the cut / copy / paste buttons
        topComponentActions.remove(DefaultEditorKit.cutAction);
        topComponentActions.remove(DefaultEditorKit.copyAction);
        topComponentActions.remove(DefaultEditorKit.pasteAction);
        topComponent.getServices().remove(styleable);
    }
}
