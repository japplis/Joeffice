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

import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.apache.poi.ss.usermodel.*;
import org.apache.xmlbeans.impl.values.XmlValueDisconnectedException;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.SpreadsheetTopComponent;

/**
 * The POI cell renderer.
 *
 * @author Anthony Goubard - Japplis
 */
public class CellRenderer extends DefaultTableCellRenderer {

    // Formatters
    private final static DataFormatter DATA_FORMATTER = new DataFormatter();

    private final static TableCellRenderer DEFAULT_RENDERER = new CellRenderer();
    private FormulaEvaluator formulaEvaluator;

    public CellRenderer() {
        DATA_FORMATTER.setDefaultNumberFormat(NumberFormat.getInstance());
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        //System.out.println("row " + row + "; column " + column + "; isSelected " + isSelected);
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (!isSelected) setBackground(UIManager.getColor("Table.background"));
        if (value != null) {
            JLabel defaultComponent = (JLabel) DEFAULT_RENDERER.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
            Cell cell = (Cell) value;
            try {
                decorateLabel(cell, defaultComponent);
            } catch (XmlValueDisconnectedException ex) {
                reloadTableModel();
            }
        }
        return this;
    }

    public void decorateLabel(Cell cell, JLabel defaultRenderer) {

        // Text
        // String text = getFormattedText(cell);
        // XXX small bug with decimal not using the correct comma's
        if (cell.getCellType() == CellType.FORMULA && formulaEvaluator == null) {
            formulaEvaluator = cell.getRow().getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        }
        String text = DATA_FORMATTER.formatCellValue(cell, formulaEvaluator);
        setText(text);

        decorateComponent(cell, this, defaultRenderer);

        // Alignment
        CellStyle style = cell.getCellStyle();
        HorizontalAlignment alignment = style.getAlignment();
        if (alignment == HorizontalAlignment.CENTER || cell.getCellType() == CellType.BOOLEAN) {
            setHorizontalAlignment(SwingConstants.CENTER);
        } else if (alignment == HorizontalAlignment.RIGHT || cell.getCellType() == CellType.NUMERIC) {
            setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            setHorizontalAlignment(defaultRenderer.getHorizontalAlignment());
        }
        VerticalAlignment verticalAlignment = style.getVerticalAlignment();
        if (verticalAlignment == VerticalAlignment.TOP) {
            setVerticalAlignment(SwingConstants.TOP);
        } else if (verticalAlignment == VerticalAlignment.CENTER) {
            setVerticalAlignment(SwingConstants.CENTER);
        } else if (verticalAlignment == VerticalAlignment.BOTTOM) {
            setVerticalAlignment(SwingConstants.BOTTOM);
        } else {
            setVerticalAlignment(defaultRenderer.getVerticalAlignment());
        }
    }

    public static void decorateComponent(Cell cell, JComponent renderingComponent, JComponent defaultRenderer) {
        CellStyle style = cell.getCellStyle();

        // Background neither the index or the color works for XSSF cells
        Color backgroundColor = CellUtils.poiToAwtColor(style.getFillForegroundColorColor());
        if (backgroundColor != null) {
            renderingComponent.setBackground(backgroundColor);
        } else {
            renderingComponent.setBackground(defaultRenderer.getBackground());
        }

        // Font and foreground
        int fontIndex = style.getFontIndex();
        if (fontIndex >= 0) {
            Font xlsFont = cell.getSheet().getWorkbook().getFontAt(fontIndex);
            java.awt.Font font = java.awt.Font.decode(xlsFont.getFontName());
            font = font.deriveFont((float) xlsFont.getFontHeightInPoints());
            font = font.deriveFont(java.awt.Font.PLAIN);
            if (xlsFont.getItalic()) {
                font = font.deriveFont(java.awt.Font.ITALIC);
            }
            if (xlsFont.getBold()) {
                font = font.deriveFont(java.awt.Font.BOLD);
            }
            if (xlsFont.getUnderline() > Font.U_NONE) {
                // no underline in fonts
            }
            //short fontColorIndex = xlsFont.getColor();
            //Color fontColor = CellUtils.shortToColor(fontColorIndex);
            Color fontColor = CellUtils.getFontColor(xlsFont, cell.getSheet().getWorkbook());
            if (fontColor != null && !fontColor.equals(Color.BLACK)) {
                renderingComponent.setForeground(fontColor);
            } else {
                renderingComponent.setForeground(defaultRenderer.getForeground());
            }
            renderingComponent.setFont(font);
        } else {
            renderingComponent.setForeground(defaultRenderer.getForeground());
            renderingComponent.setFont(defaultRenderer.getFont());
        }

        // Borders
        // At the moment done in renderer but should be done with a JLayer to paint over the grid
        renderingComponent.setBorder(new CellBorder(cell));

        if (cell.getCellComment() != null) {
            renderingComponent.setToolTipText(cell.getCellComment().getString().getString());
        }
    }

    // Due to https://issues.apache.org/bugzilla/show_bug.cgi?id=49940
    private void reloadTableModel() {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            currentTopComponent.getSpreadsheetComponent().reload();
        }
    }
}
