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
package org.joeffice.wordprocessor.reader;

import javax.swing.text.*;
import javax.swing.text.Document;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;

import javax.swing.UIManager;
import org.joeffice.wordprocessor.DocxDocument;

import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.LineSpacingRule;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.officeDocument.x2006.sharedTypes.STVerticalAlignRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblGridCol;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHighlightColor;

/**
 * Implements reader of document.
 *
 * @author	Stanislav Lapitsky
 * @author Anthony Goubard - Japplis
 */
public class POIDocxReader {

    public static final int INDENTS_MULTIPLIER = 20;
    /**
     * document instance to the building (for the editor kit).
     */
    private DocxDocument document;

    /**
     * Document as read from POI library
     */
    private XWPFDocument poiDocument;

    /**
     * Current offset in the document for insert action.
     */
    private int currentOffset = 0;
    private SimpleAttributeSet parAttrs;
    private SimpleAttributeSet charAttrs;

    /**
     * Builds new instance of reader.
     *
     * @param	doc document for reading to.
     */
    public POIDocxReader(Document doc) {
        document = (DocxDocument) doc;
    }

    /**
     * Reads content of specified file to the document.
     *
     * @param fileName path to the file.
     * @param offset offset to read the content.
     */
    public void read(String fileName, int offset) throws IOException, BadLocationException {
        try (FileInputStream input = new FileInputStream(fileName)) {
            read(input, offset);
        }
    }

    /**
     * Reads content of specified stream to the document.
     *
     * @param in stream.
     */
    public void read(InputStream in, int offset) throws IOException, BadLocationException {
        poiDocument = new XWPFDocument(in);

        iteratePart(poiDocument.getBodyElements());

        this.currentOffset = offset;
        document.putProperty("XWPFDocument", poiDocument);
    }

    public void iteratePart(List<IBodyElement> content) throws BadLocationException {
        for (IBodyElement elem : content) {
            /*if (charAttrs != null && parAttrs != null) {
                document.insertString(currentOffset, "\n", charAttrs);
                document.setParagraphAttributes(currentOffset, 1, parAttrs, true);
                currentOffset++;
            }*/
            if (elem instanceof XWPFParagraph) {
                processParagraph((XWPFParagraph) elem);
                if (elem != content.get(content.size() - 1)) {
                    document.insertString(currentOffset, "\n", charAttrs);
                    document.setParagraphAttributes(currentOffset, 1, parAttrs, true);
                    currentOffset++;
                } else {
                    document.setParagraphAttributes(currentOffset, 1, parAttrs, true);
                }
            } else if (elem instanceof XWPFTable) {
                processTable((XWPFTable) elem);
            } else {
                System.out.println(elem);
            }
        }
    }

    protected void processParagraph(XWPFParagraph paragraph) throws BadLocationException {
        parAttrs = new SimpleAttributeSet();
        ParagraphAlignment alignment = paragraph.getAlignment();
        if (alignment == ParagraphAlignment.CENTER) {
            StyleConstants.setAlignment(parAttrs, StyleConstants.ALIGN_CENTER);
        } else if (alignment == ParagraphAlignment.LEFT) {
            StyleConstants.setAlignment(parAttrs, StyleConstants.ALIGN_LEFT);
        } else if (alignment == ParagraphAlignment.RIGHT) {
            StyleConstants.setAlignment(parAttrs, StyleConstants.ALIGN_RIGHT);
        } else if (alignment == ParagraphAlignment.BOTH || alignment == ParagraphAlignment.DISTRIBUTE) {
            StyleConstants.setAlignment(parAttrs, StyleConstants.ALIGN_JUSTIFIED);
        }
        List<TabStop> tabs = new ArrayList<>();
        int leftIndentation = paragraph.getIndentationLeft();
        if (leftIndentation > 0) {
            float indentation = leftIndentation / INDENTS_MULTIPLIER;
            StyleConstants.setLeftIndent(parAttrs, indentation);
            /*TabStop stop = new TabStop(pos, TabStop.ALIGN_LEFT, TabStop.LEAD_NONE);
             tabs.add(stop);*/
        }
        int rightIndentation = paragraph.getIndentationRight();
        if (rightIndentation > 0) {
            float indentation = rightIndentation / INDENTS_MULTIPLIER;
            StyleConstants.setLeftIndent(parAttrs, indentation);
            /*TabStop stop = new TabStop(pos, TabStop.ALIGN_RIGHT, TabStop.LEAD_NONE);
             tabs.add(stop);*/
        }
        /*TabSet tabSet = new TabSet(tabs.toArray(new TabStop[tabs.size()]));
         StyleConstants.setTabSet(parAttrs, tabSet);*/
        int firstLineIndentation = paragraph.getIndentationFirstLine();
        if (firstLineIndentation > 0) {
            float indentation = firstLineIndentation / INDENTS_MULTIPLIER;
            StyleConstants.setFirstLineIndent(parAttrs, indentation);
            /*TabStop stop = new TabStop(pos, TabStop.ALIGN_RIGHT, TabStop.LEAD_NONE);
             tabs.add(stop);*/
        }

        int spacingBefore = paragraph.getSpacingBefore();
        if (spacingBefore > 0) {
            int before = spacingBefore / INDENTS_MULTIPLIER;
            StyleConstants.setSpaceAbove(parAttrs, before);
        }
        int spacingAfter = paragraph.getSpacingAfter();
        if (spacingAfter > 0) {
            int after = spacingAfter / INDENTS_MULTIPLIER;
            StyleConstants.setSpaceAbove(parAttrs, after);
        }
        LineSpacingRule spacingLine = paragraph.getSpacingLineRule();
        if (spacingLine == LineSpacingRule.AT_LEAST || spacingLine == LineSpacingRule.AUTO) {
            float spacing = spacingLine.getValue() / 240;
            StyleConstants.setLineSpacing(parAttrs, spacing);
        }
        document.setParagraphAttributes(currentOffset, 1, parAttrs, true);
        for (XWPFRun run : paragraph.getRuns()) {
            processRun(run);
        }
    }

    protected void processRun(XWPFRun run) throws BadLocationException {
        charAttrs = new SimpleAttributeSet();

        if (run.getFontSize() > 0) {
            int size = run.getFontSize();
            StyleConstants.setFontSize(charAttrs, size);
        }
        StyleConstants.setBold(charAttrs, run.isBold());
        StyleConstants.setItalic(charAttrs, run.isItalic());
        StyleConstants.setStrikeThrough(charAttrs, run.isStrike());
        boolean underlined = run.getUnderline() != UnderlinePatterns.NONE;
        StyleConstants.setUnderline(charAttrs, underlined);
        STVerticalAlignRun.Enum verticalAlignment = run.getVerticalAlignment();
        if (verticalAlignment == STVerticalAlignRun.SUBSCRIPT) {
            StyleConstants.setSubscript(parAttrs, true);
        } else if (verticalAlignment == STVerticalAlignRun.SUPERSCRIPT) {
            StyleConstants.setSuperscript(parAttrs, true);
        } else {
            StyleConstants.setSubscript(parAttrs, false);
            StyleConstants.setSuperscript(parAttrs, false);
        }
        if (run.getFontFamily() != null) {
            StyleConstants.setFontFamily(charAttrs, run.getFontFamily());
        }
        if (run.getColor() != null) {
            String name = run.getColor();
            if (!name.toLowerCase().equals("auto")) {
                Color color = Color.decode("#" + name);
                StyleConstants.setForeground(charAttrs, color);
            } else {
                // FIXME Why is it not the default ?
                StyleConstants.setForeground(charAttrs, UIManager.getColor("TextPane.foreground")); //
            }
        } else {
            StyleConstants.setForeground(charAttrs, UIManager.getColor("TextPane.foreground"));
        }

        // Not working
        if (run.getCTR().getRPr() != null && !run.getCTR().getRPr().getHighlightList().isEmpty()) {
            STHighlightColor.Enum colorEnum = run.getCTR().getRPr().getHighlightList().get(0).getVal();
            Color color = decodeHighlightName(colorEnum);
            StyleConstants.setBackground(charAttrs, color);
        }

        for (XWPFPicture picture : run.getEmbeddedPictures()) {
            processPicture(picture);
        }
        String text = run.toString();
        document.insertString(currentOffset, text, charAttrs);
        currentOffset += text.length();
    }

    protected Color decodeHighlightName(STHighlightColor.Enum colorEnum) {
        switch (colorEnum.intValue()) {
            case STHighlightColor.INT_YELLOW:
            case STHighlightColor.INT_DARK_YELLOW:
                return Color.YELLOW;
            case STHighlightColor.INT_BLUE:
            case STHighlightColor.INT_DARK_BLUE:
                return Color.BLUE;
            case STHighlightColor.INT_CYAN:
            case STHighlightColor.INT_DARK_CYAN:
                return Color.CYAN;
            case STHighlightColor.INT_LIGHT_GRAY:
                return Color.LIGHT_GRAY;
            case STHighlightColor.INT_DARK_GRAY:
                return Color.DARK_GRAY;
            case STHighlightColor.INT_GREEN:
            case STHighlightColor.INT_DARK_GREEN:
                return Color.GREEN;
            case STHighlightColor.INT_MAGENTA:
            case STHighlightColor.INT_DARK_MAGENTA:
                return Color.MAGENTA;
            case STHighlightColor.INT_RED:
            case STHighlightColor.INT_DARK_RED:
                return Color.RED;
            case STHighlightColor.INT_WHITE:
                return Color.WHITE;
            case STHighlightColor.INT_BLACK:
                return Color.BLACK;
        }
        return null;
    }

    protected void processTable(XWPFTable table) throws BadLocationException {
        int rowCount = table.getNumberOfRows();
        int columnCount = 0;
        for (XWPFTableRow row : table.getRows()) {
            columnCount = Math.max(columnCount, row.getTableCells().size());
        }
        int[] rowsHeight = new int[rowCount];
        int[] columnsWidth = new int[columnCount];
        for (int i = 0; i < rowCount; i++) {
            rowsHeight[i] = 1;
        }
        for (int i = 0; i < columnCount; i++) {
            List<CTTblGridCol> colList = table.getCTTbl().getTblGrid().getGridColList();
            columnsWidth[i] = Integer.parseInt(colList.get(i).getW().toString()) / INDENTS_MULTIPLIER;
        }
        SimpleAttributeSet tableAttrs = new SimpleAttributeSet();
        document.insertTable(currentOffset, rowCount, columnCount, tableAttrs, columnsWidth, rowsHeight);
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                iteratePart(cell.getBodyElements());
                currentOffset++;
            }
        }
    }

    protected void processPicture(XWPFPicture picture) throws BadLocationException {
        byte[] pictureBytes = picture.getPictureData().getData();
        ImageIcon image = new ImageIcon(pictureBytes);
        image.setDescription(picture.getDescription());
        document.insertPicture(image, currentOffset);
        currentOffset++;
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) {
            String filePath = args[0];
            POIDocxReader reader = new POIDocxReader(new DocxDocument());
            reader.read(filePath, 0);
            System.out.println(reader.document.getText(0, reader.document.getLength()));
        }
    }
}