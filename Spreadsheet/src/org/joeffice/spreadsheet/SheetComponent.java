/*
 * Copyright 2013-2022 Japplis.
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

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.text.DefaultEditorKit;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.jdesktop.swingx.scrollpaneselector.ScrollPaneSelector;
import org.joeffice.spreadsheet.actions.ClipboardAction;
import org.joeffice.spreadsheet.sheet.SheetListener;

import org.joeffice.spreadsheet.cell.CellRenderer;
import org.joeffice.spreadsheet.sheet.TableColumnAdjuster;
import org.joeffice.spreadsheet.rows.RowTableFactory;
import org.joeffice.spreadsheet.sheet.SheetTableModel;

/**
 * Component that displays one sheet.
 *
 * @author Anthony Goubard - Japplis
 */
public class SheetComponent extends JPanel {

    public static final String SHEET_MODIFIED_PROPERTY = "SheetModified";
    public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
    public static final int UNIT_OFFSET_LENGTH = 7;
    public static final int CELL_HEIGHT_MARGINS = 1;
    public static final int CELL_EXTRA_HEIGHT_MARGINS = 5;

    private JLayeredPane layers;
    private JTable sheetTable;
    private Sheet sheet;
    private boolean addExtraSpace;

    public SheetComponent(Sheet sheet) {
        this(sheet, true);
    }

    public SheetComponent(Sheet sheet, boolean addExtraSpace) {
        this.sheet = sheet;
        this.addExtraSpace = addExtraSpace;
        initComponent();
    }

    private void initComponent() {
        sheetTable = createTable(sheet);
        listenToChanges();
        layers = createSheetLayers(sheetTable);

        JScrollPane scrolling = RowTableFactory.attachRows(sheetTable, layers);
        scrolling.setColumnHeaderView(sheetTable.getTableHeader());
        ScrollPaneSelector.installScrollPaneSelector(scrolling);

        setLayout(new BorderLayout());
        add(scrolling);
    }

    public JTable createTable(Sheet sheet) {
        SheetTableModel sheetTableModel = new SheetTableModel(sheet, addExtraSpace);
        JTable table = new SheetTable(sheetTableModel);

        CellRenderer cellRenderer = new CellRenderer();
        table.setDefaultRenderer(Cell.class, cellRenderer);
        TableCellEditor editor = new org.joeffice.spreadsheet.cell.CellEditor();
        table.setDefaultEditor(Cell.class, editor);
        int columnsCount = sheetTableModel.getColumnCount();
        for (int i = 0; i < columnsCount; i++) {
            TableColumn tableColumn = table.getColumnModel().getColumn(i);
            tableColumn.setCellRenderer(cellRenderer);
            tableColumn.setCellEditor(editor);
            int widthUnits = sheet.getColumnWidth(i);
            tableColumn.setPreferredWidth(widthUnitsToPixel(widthUnits));
        }

        int rowCount = sheetTableModel.getRowCount();
        int extraMargin = addExtraSpace ? CELL_EXTRA_HEIGHT_MARGINS : CELL_HEIGHT_MARGINS;
        for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null) {
                int cellHeight = (int) Math.ceil(sheet.getRow(rowIndex).getHeightInPoints());
                cellHeight += extraMargin;
                table.setRowHeight(rowIndex, cellHeight);
            }
        }

        table.setAutoscrolls(true);
        table.setFillsViewportHeight(true);
        JLabel tableHeader = (JLabel) table.getTableHeader().getDefaultRenderer();
        tableHeader.setHorizontalAlignment(SwingConstants.CENTER);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.setCellSelectionEnabled(true);

        TableColumnAdjuster tca = new TableColumnAdjuster(table, 20);
        if (sheet.getDefaultColumnWidth() == -1) {
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tca.setOnlyAdjustLarger(true);
            tca.setLeaveEmptyAsIs(true);
            tca.adjustColumns();
        }

        table.setTransferHandler(new TableTransferHandler());
        table.setDragEnabled(true);
        table.setDropMode(DropMode.ON_OR_INSERT);

        Action cutAction = new ClipboardAction(DefaultEditorKit.cutAction);
        Action copyAction = new ClipboardAction(DefaultEditorKit.copyAction);
        Action pasteAction = new ClipboardAction(DefaultEditorKit.pasteAction);
        table.getActionMap().put(DefaultEditorKit.cutAction, cutAction);
        table.getActionMap().put(DefaultEditorKit.copyAction, copyAction);
        table.getActionMap().put(DefaultEditorKit.pasteAction, pasteAction);

        //table.setIntercellSpacing(new Dimension(0, 0));
        table.putClientProperty("print.printable", Boolean.TRUE);
        Rectangle lastDataCellBounds = table.getCellRect(sheet.getLastRowNum(), sheetTableModel.getLastColumnNum(), true);
        table.putClientProperty("print.size", new Dimension(lastDataCellBounds.x + lastDataCellBounds.width, lastDataCellBounds.y + lastDataCellBounds.height));
        new SheetListener(table);

        if (!sheet.isDisplayGridlines()) {
            table.setShowGrid(false);
        }
        return table;
    }

    // From http://stackoverflow.com/questions/6663591/jtable-inside-jlayeredpane-inside-jscrollpane-how-do-you-get-it-to-work
    public JLayeredPane createSheetLayers(final JTable table) {
        JLayeredPane layers = new JLayeredPane() {
            @Override
            public Dimension getPreferredSize() {
                return table.getPreferredSize();
            }

            @Override
            public void setSize(int width, int height) {
                super.setSize(width, height);
                table.setSize(width, height);
            }

            @Override
            public void setSize(Dimension d) {
                super.setSize(d);
                table.setSize(d);
            }
        };
        // NB you must use Integer.valueOf - the int version is a different method
        layers.add(table, Integer.valueOf(JLayeredPane.DEFAULT_LAYER), 0);
        return layers;
    }

    // From http://apache-poi.1045710.n5.nabble.com/Excel-Column-Width-Unit-Converter-pixels-excel-column-width-units-td2301481.html
    public static int widthUnitsToPixel(int widthUnits) {
        int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR) * UNIT_OFFSET_LENGTH;

        int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
        pixels += Math.round((float) offsetWidthUnits / ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));

        return pixels;
    }

    public void listenToChanges() {
        sheetTable.getModel().addTableModelListener(tme -> {
            firePropertyChange(SHEET_MODIFIED_PROPERTY, false, true);
        });
        sheetTable.addPropertyChangeListener("singleRowHeight", pce -> {
            int rowChanged = (Integer) pce.getNewValue();
            int newHeight = sheetTable.getRowHeight(rowChanged);
            if (newHeight != sheetTable.getRowHeight(rowChanged)) {
                Row row = sheet.getRow(rowChanged);
                if (row == null) {
                    row = sheet.createRow(rowChanged);
                }
                row.setHeight((short) newHeight);
            }
        });
    }

    public JTable getTable() {
        return sheetTable;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet, boolean addExtraSpace) {
        this.sheet = sheet;
        this.addExtraSpace = addExtraSpace;
        SheetTableModel sheetTableModel = new SheetTableModel(sheet, addExtraSpace);
        sheetTable.setModel(sheetTableModel);
        sheetTableModel.addTableModelListener(tme -> {
            firePropertyChange(SHEET_MODIFIED_PROPERTY, false, true);
        });
    }
}
