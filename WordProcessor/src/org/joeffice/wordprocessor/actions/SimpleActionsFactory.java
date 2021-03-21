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
package org.joeffice.wordprocessor.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.text.*;

import org.joeffice.wordprocessor.BorderAttributes;
import org.joeffice.wordprocessor.DocxDocument;
import org.joeffice.wordprocessor.RichTextTransferHandler;
import org.joeffice.wordprocessor.WordProcessorTopComponent;
import org.joeffice.wordprocessor.app.*;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;

/**
 * A set of actions for the editor.
 *
 * @author Anthony Goubard - Japplis
 */
public class SimpleActionsFactory {

    /**
     * Return an Action which copies text without its format.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.copyAsText")
    @ActionRegistration(
            displayName = "#CTL_CopyAsText")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor", position = 600)
    @Messages("CTL_CopyAsText=Copy as text")
    public static Action copyAsText() {
        String actionName = NbBundle.getMessage(SimpleActionsFactory.class, "CTL_CopyAsText");
        Action action = new AbstractAction(actionName) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                RichTextTransferHandler transferHandler = ((RichTextTransferHandler) edit.getTransferHandler());
                try {
                    transferHandler.setAsTextOnly(true);
                    edit.copy();
                } finally {
                    transferHandler.setAsTextOnly(false);
                }
            }
        };
        return action;
    }

    /**
     * Return an Action which pastes text unformatted.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.pasteAsText")
    @ActionRegistration(
            displayName = "#CTL_PasteAsText")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor", position = 700)
    @Messages("CTL_PasteAsText=Paste as text")
    public static Action pasteAsText() {
        String actionName = NbBundle.getMessage(SimpleActionsFactory.class, "CTL_PasteAsText");
        Action action = new AbstractAction(actionName) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                RichTextTransferHandler transferHandler = ((RichTextTransferHandler) edit.getTransferHandler());
                try {
                    transferHandler.setAsTextOnly(true);
                    edit.paste();
                } finally {
                    transferHandler.setAsTextOnly(false);
                }
            }
        };
        return action;
    }

    /**
     * Return an Action which inserts image into document. Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.insertImageAction")
    @ActionRegistration(
            displayName = "#CTL_InsertImageAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor", position = 200)
    @Messages("CTL_InsertImageAction=Insert image")
    public static Action insertImageAction() {
        Action action = new AbstractAction("Insert image") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                Frame mainFrame = WindowManager.getDefault().getMainWindow();
                JFileChooser fc = new JFileChooser();
                if (fc.showOpenDialog(mainFrame) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                ImageIcon icon = new ImageIcon(fc.getSelectedFile().getPath());
                int w = icon.getIconWidth();
                int h = icon.getIconHeight();
                if (w <= 0 || h <= 0) {
                    JOptionPane.showMessageDialog(mainFrame, "Invalid image!");
                    return;
                }
                DocxDocument doc = (DocxDocument) edit.getDocument();
                doc.insertPicture(icon, edit.getCaretPosition());
            }
        };
        return action;
    }

    /**
     * Return an Action which setVisible(true)s paragraph attributes. Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.paragraphAttributesAction")
    @ActionRegistration(
            displayName = "#CTL_ParagraphAttributesAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor", position = 100)
    @Messages("CTL_ParagraphAttributesAction=Paragraph...")
    public static Action paragraphAttributesAction() {
        Action action = new AbstractAction("Paragraph...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
                ParagraphPanel paragraph = new ParagraphPanel();
                AttributeSet attrs = ((DocxDocument) edit.getDocument()).getParagraphElement(edit.getCaretPosition()).getAttributes();
                paragraph.setAttributes(attrs);
                paragraph.showDialog(mainFrame);
                if (paragraph.getOption() == JOptionPane.OK_OPTION) {
                    DocxDocument doc = (DocxDocument) edit.getDocument();
                    AttributeSet attr = paragraph.getAttributes();
                    int selectionStart = Math.min(edit.getSelectionStart(), edit.getSelectionEnd());
                    int selectionLength = Math.abs(edit.getSelectionEnd() - selectionStart);
                    doc.setParagraphAttributes(selectionStart, selectionLength, attr, false);
                }
            }
        };
        return action;
    }

    /**
     * Return an Action which sets margins of document. Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.MarginsAction")
    @ActionRegistration(
            displayName = "#CTL_MarginsAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor", position = 1800)
    @Messages("CTL_MarginsAction=Margins...")
    public static Action setMarginsAction() {
        Action action = new AbstractAction("Margins...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                MarginsPanel marginsPanel = new MarginsPanel();
                marginsPanel.setMargins(doc.getDocumentMargins());
                marginsPanel.showDialog(mainFrame);
                if (marginsPanel.getOption() == JOptionPane.OK_OPTION) {
                    doc.setDocumentMargins(marginsPanel.getMargins());
                }
            }
        };
        return action;
    }

    /**
     * Return an Action which inserts table into document. Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.insertTableAction")
    @ActionRegistration(
            displayName = "#CTL_InsertTableAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 1000)
    @Messages("CTL_InsertTableAction=Insert table...")
    public static Action insertTableAction() {
        Action action = new AbstractAction("Table...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                JFrame mainFrame = (JFrame) WindowManager.getDefault().getMainWindow();
                InsertTablePanel insertPanel = new InsertTablePanel();
                insertPanel.showDialog(mainFrame);
                if (insertPanel.getOption() == JOptionPane.OK_OPTION) {
                    int pos = edit.getCaretPosition();
                    DocxDocument doc = (DocxDocument) edit.getDocument();
                    Element cell = doc.getCell(pos);
                    SimpleAttributeSet attrs = new SimpleAttributeSet();
                    BorderAttributes ba = new BorderAttributes();
                    ba.setBorders(1 + 2 + 4 + 8 + 16 + 32);
                    ba.lineColor = insertPanel.getColor();
                    attrs.addAttribute("BorderAttributes", ba);
                    int[] widths = new int[insertPanel.getColumnCount()];
                    if (cell == null) {
                        for (int i = 0; i < widths.length; i++) {
                            widths[i] = 100;
                        }
                    } else {
                        int width = ((DocxDocument.CellElement) cell).getWidth() - 4;
                        for (int i = 0; i < widths.length; i++) {
                            widths[i] = width / insertPanel.getColumnCount();
                        }
                    }
                    int[] heights = new int[insertPanel.getRowCount()];
                    for (int i = 0; i < heights.length; i++) {
                        heights[i] = 1;
                    }
                    doc.insertTable(pos, insertPanel.getRowCount(), insertPanel.getColumnCount(), attrs, widths, heights);
                }
            }
        };
        return action;
    }

    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.insertRowBelowAction")
    @ActionRegistration(
            displayName = "#CTL_InsertRowBelowAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 1200)
    @Messages("CTL_InsertRowBelowAction=Insert row below")
    public static Action insertRowBelowAction() {
        return insertRowAction(false);
    }

    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.insertRowAboveAction")
    @ActionRegistration(
            displayName = "#CTL_InsertRowAboveAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 1300)
    @Messages("CTL_InsertRowAboveAction=Insert row above")
    public static Action insertRowAboveAction() {
        return insertRowAction(true);
    }

    /**
     * Return an Action which inserts row into document's table. Used for creating menu item.
     *
     * @param above if true new row will be inserted above current row.
     * @return
     */
    public static Action insertRowAction(final boolean above) {
        String label = "Row";
        if (above) {
            label += " above";
        } else {
            label += " below";
        }
        Action action = new AbstractAction(label) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                doc.insertRow(edit.getCaretPosition(), above);
            }
        };
        return action;
    }

    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.insertColumnToTheRightAction")
    @ActionRegistration(
            displayName = "#CTL_InsertColumnToTheRightAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 1400)
    @Messages("CTL_InsertColumnToTheRightAction=Insert column to the right")
    public static Action insertColumnToTheRightAction() {
        return insertColumnAction(false);
    }

    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.insertColumnToTheLeftAction")
    @ActionRegistration(
            displayName = "#CTL_InsertColumnToTheLeftAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 1500)
    @Messages("CTL_InsertColumnToTheLeftAction=Insert column to the left")
    public static Action insertColumnToTheLeftAction() {
        return insertColumnAction(true);
    }

    /**
     * Return an Action which inserts column into document's table. Used for creating menu item.
     *
     * @param beforeove if true new column will be inserted before current column.
     * @return
     */
    public static Action insertColumnAction(final boolean before) {
        String label = "Column";
        if (before) {
            label += " to the left";
        } else {
            label += " to the right";
        }
        Action action = new AbstractAction(label) {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                doc.insertColumn(edit.getCaretPosition(), 50, before);
            }
        };
        return action;
    }

    /**
     * Return an Action which removes table from document. Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.deleteTableAction")
    @ActionRegistration(
            displayName = "#CTL_DeleteTableAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 2000, separatorBefore = 1990)
    @Messages("CTL_DeleteTableAction=Delete table")
    public static Action deleteTableAction() {
        Action action = new AbstractAction("Table") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                doc.deleteTable(edit.getCaretPosition());
            }
        };
        return action;
    }

    /**
     * Return an Action which removes row from document's table. Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.deleteRowAction")
    @ActionRegistration(
            displayName = "#CTL_DeleteRowAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 2100)
    @Messages("CTL_DeleteRowAction=Delete row")
    public static Action deleteRowAction() {
        Action action = new AbstractAction("Row") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                doc.deleteRow(edit.getCaretPosition());
            }
        };
        return action;
    }

    /**
     * Return an Action which removes column from document's table. Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.deleteColumnAction")
    @ActionRegistration(
            displayName = "#CTL_DeleteColumnAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 2200)
    @Messages({"CTL_DeleteColumnAction=Delete column"})
    public static Action deleteColumnAction() {
        Action action = new AbstractAction("Column") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                doc.deleteColumn(edit.getCaretPosition());
            }
        };
        return action;
    }

    /**
     * Return an Action which setVisible(true)s table's properties Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.tablePropertiesAction")
    @ActionRegistration(
            displayName = "#CTL_TablePropertiesAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 2500, separatorBefore = 2490)
    @Messages("CTL_TablePropertiesAction=Table Properties...")
    public static Action tablePropertiesAction() {
        Action action = new AbstractAction("Table...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                Element table = doc.getTable(edit.getCaretPosition());
                if (table == null) {
                    JOptionPane.showMessageDialog(null, "Invalid table offset!");
                    return;
                }
                TableProperties tp = new TableProperties();
                tp.setTable(table);
                tp.setVisible(true);
            }
        };
        return action;
    }

    /**
     * Return an Action which setVisible(true)s row's properties Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.rowPropertiesAction")
    @ActionRegistration(
            displayName = "#CTL_RowPropertiesAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 2600)
    @Messages("CTL_RowPropertiesAction=Row Properties...")
    public static Action rowPropertiesAction() {
        Action action = new AbstractAction("Row...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                Element row = doc.getRow(edit.getCaretPosition());
                if (row == null) {
                    JOptionPane.showMessageDialog(null, "Invalid row offset!");
                    return;
                }
                TableProperties tp = new TableProperties();
                tp.setRow(row);
                tp.setVisible(true);
            }
        };
        return action;
    }

    /**
     * Return an Action which setVisible(true)s cell's properties Used for creating menu item.
     *
     * @return
     */
    @ActionID(
            category = "Edit/Office/Word Processor",
            id = "org.joeffice.wordprocessor.actions.cellPropertiesAction")
    @ActionRegistration(
            displayName = "#CTL_CellPropertiesAction")
    @ActionReference(path = "Menu/Edit/Gimme More/Word Processor/Table", position = 2700)
    @Messages("CTL_CellPropertiesAction=Cell Properties...")
    public static Action cellPropertiesAction() {
        Action action = new AbstractAction("Cell...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
                DocxDocument doc = (DocxDocument) edit.getDocument();
                Element cell = doc.getCell(edit.getCaretPosition());
                if (cell == null) {
                    JOptionPane.showMessageDialog(null, "Invalid cell offset!");
                    return;
                }
                TableProperties tp = new TableProperties();
                tp.setCell(cell);
                tp.setVisible(true);
            }
        };
        return action;
    }
}
