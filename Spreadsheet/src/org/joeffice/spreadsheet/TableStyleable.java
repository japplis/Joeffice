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
package org.joeffice.spreadsheet;


import org.joeffice.spreadsheet.cell.CellUtils;
import static java.awt.font.TextAttribute.*;
import static org.joeffice.desktop.actions.ExtraTextAttribute.*;

import java.awt.Color;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.text.StyleConstants;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import org.joeffice.desktop.actions.TextTransformer;
import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.desktop.ui.Styleable;

/**
 * Class that applies the style to the selected cells.
 *
 * This involves converting the {@link AttributedString} to a {@link CellStyle} and vice versa.
 *
 * @author Anthony Goubard - Japplis
 */
public class TableStyleable implements Styleable {

    @Override
    public void setFontAttributes(AttributedString attributes) {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            JTable table = currentTopComponent.getSelectedTable();

            List<Cell> selectedCells = CellUtils.getSelectedCells(table);
            for (Cell cell : selectedCells) {
                AttributedCharacterIterator attributesIterator = attributes.getIterator();
                for (Attribute attribute : attributesIterator.getAllAttributeKeys()) {
                    Object value = attributesIterator.getAttribute(attribute);
                    addAttribute(attribute, value, cell);
                    ((AbstractTableModel) table.getModel()).fireTableCellUpdated(cell.getRowIndex(), cell.getColumnIndex());
                }
            }
        }
    }

    /**
     * Add the attribute as defined in {@link AttributedString} to the {@link MutableAttributeSet} for the JTextPane.
     *
     * @see java.awt.font.TextAttribute
     */
    protected void addAttribute(AttributedCharacterIterator.Attribute attribute, Object attributeValue, Cell cell) {
        CellStyle oldStyle = cell.getCellStyle();
        Workbook workbook = cell.getSheet().getWorkbook();
        CellStyle style = cell.getSheet().getWorkbook().createCellStyle();
        style.cloneStyleFrom(oldStyle);
        Font newFont = copyFont(cell);
        if (attribute == FAMILY) {
            newFont.setFontName((String) attributeValue);
            CellUtil.setFont(cell, newFont);
        } else if (attribute == FOREGROUND) {
            Color color = (Color) attributeValue;
            if (cell instanceof XSSFCell) {
                ((XSSFCellStyle) style).setFillForegroundColor(new XSSFColor(color));
            } else {
                HSSFWorkbook xlsWorkbook = (HSSFWorkbook) workbook;
                HSSFColor xlsColor = xlsWorkbook.getCustomPalette().findColor((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
                if (xlsColor == null) {
                    xlsColor = xlsWorkbook.getCustomPalette().addColor((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
                }
                style.setFillForegroundColor(xlsColor.getIndex());
            }
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        } else if (attribute == BACKGROUND) {
            Color color = (Color) attributeValue;
            if (cell instanceof XSSFCell) {
                ((XSSFCellStyle) style).setFillBackgroundColor(new XSSFColor(color));
            } else {
                HSSFWorkbook xlsWorkbook = (HSSFWorkbook) workbook;
                HSSFColor xlsColor = xlsWorkbook.getCustomPalette().findColor((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
                if (xlsColor == null) {
                    xlsColor = xlsWorkbook.getCustomPalette().addColor((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue());
                }
                style.setFillBackgroundColor(xlsColor.getIndex());
            }
        } else if (attribute == WEIGHT) {
            boolean bold = !newFont.getBold(); // Invert bold value
            newFont.setBold(bold);
            CellUtil.setFont(cell, newFont);
        } else if (attribute == UNDERLINE) {
            byte underlineValue = Font.U_SINGLE;
            if (newFont.getUnderline() == Font.U_SINGLE) {
                underlineValue = Font.U_NONE;
            }
            newFont.setUnderline(underlineValue);
            CellUtil.setFont(cell, newFont);
        } else if (attribute == SUPERSCRIPT) {
            short superscriptValue = Font.SS_NONE;
            if (SUPERSCRIPT_SUB.equals(attributeValue)) {
                superscriptValue = Font.SS_SUB;
            } else if (SUPERSCRIPT_SUPER.equals(attributeValue)) {
                superscriptValue = Font.SS_SUPER;
            }
            newFont.setTypeOffset(superscriptValue);
            CellUtil.setFont(cell, newFont);
        } else if (attribute == STRIKETHROUGH) {
            boolean strikeThrough = true;
            if (newFont.getStrikeout()) {
                strikeThrough = false;
            }
            newFont.setStrikeout(strikeThrough);
            CellUtil.setFont(cell, newFont);
        } else if (attribute == POSTURE) {
            boolean italic = true;
            if (newFont.getItalic()) {
                italic = false;
            }
            newFont.setItalic(italic);
            CellUtil.setFont(cell, newFont);
        } else if (attribute == SIZE) {
            newFont.setFontHeightInPoints(((Number) attributeValue).shortValue());
            CellUtil.setFont(cell, newFont);
        } else if (attribute == JUSTIFICATION) {
            CellUtil.setAlignment(cell, HorizontalAlignment.JUSTIFY);
        } else if (attribute == ALIGNMENT) {
            if (attributeValue.equals(StyleConstants.ALIGN_LEFT)) {
                CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);
            } else if (attributeValue.equals(StyleConstants.ALIGN_RIGHT)) {
                CellUtil.setAlignment(cell, HorizontalAlignment.RIGHT);
            } else if (attributeValue.equals(StyleConstants.ALIGN_CENTER)) {
                CellUtil.setAlignment(cell, HorizontalAlignment.CENTER);
            }
        } else if (attribute == INDENTATION) {
            style.setIndention(((Number) attributeValue).shortValue());
        } else if (attribute == TEXT_TRANSFORM) {
            String text = CellUtils.getFormattedText(cell);
            String transformedText = ((TextTransformer) attributeValue).transformText(text);
            cell.setCellValue(transformedText);
        }
    }

    private Font copyFont(Cell cell) {
        CellStyle style = cell.getCellStyle();
        Workbook workbook = cell.getSheet().getWorkbook();
        int fontIndex = style.getFontIndex();
        Font xlsFont = cell.getSheet().getWorkbook().getFontAt(fontIndex);
        Font newFont = workbook.createFont();
        newFont.setFontName(xlsFont.getFontName());
        newFont.setFontHeight(xlsFont.getFontHeight());
        newFont.setBold(xlsFont.getBold());
        newFont.setItalic(xlsFont.getItalic());
        newFont.setUnderline(xlsFont.getUnderline());
        newFont.setColor(xlsFont.getColor());
        return newFont;
    }

    @Override
    public AttributedString getCommonFontAttributes() {
        AttributedString commonAttributes = new AttributedString("Selection");
        return commonAttributes;
    }
}
