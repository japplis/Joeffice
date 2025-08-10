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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultEditorKit;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import org.joeffice.desktop.ui.OfficeTopComponent;
import org.joeffice.spreadsheet.actions.ShowHideGridAction;
import org.openide.util.Utilities;
import org.openide.filesystems.FileUtil;

/**
 * Component that displays several sheets.
 *
 * @author Anthony Goubard - Japplis
 */
public class SpreadsheetComponent extends JTabbedPane implements ChangeListener {

    private Workbook workbook;
    private FormulaEvaluator formulaEvaluator;
    private TableStyleable styleable;
    private SpreadsheetTopComponent spreadsheetAndToolbar;

    /**
     * Creates a spreadsheet component that doesn't depend on NetBeans framework classes
     */
    public SpreadsheetComponent() {
        super(JTabbedPane.BOTTOM, SCROLL_TAB_LAYOUT);
        initComponents();
    }

    private void initComponents() {
        styleable = new TableStyleable();
        addChangeListener(this);
    }

    public void load(Workbook workbook) {
        closeWorkbook();
        this.workbook = workbook;
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            String sheetName = workbook.getSheetName(i);
            JComponent sheetPanel = new SheetComponent(sheet); //new JScrollPane(new JTable(20, 50));
            add(sheetName, sheetPanel);
            sheetPanel.addPropertyChangeListener(SheetComponent.SHEET_MODIFIED_PROPERTY, pce -> setModified(true));
        }
        int activeSheetIndex = workbook.getActiveSheetIndex();
        if (activeSheetIndex < 0) activeSheetIndex = 0;
        setSelectedIndex(activeSheetIndex);
        formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
    }

    // Due to bug https://issues.apache.org/bugzilla/show_bug.cgi?id=49940
    public void reload() {
        if (spreadsheetAndToolbar == null) return;
        this.workbook = spreadsheetAndToolbar.getWorkbook();
        int numberOfSheets = workbook.getNumberOfSheets();
        for (int i = 0; i < numberOfSheets; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            ((SheetComponent) getComponentAt(i)).setSheet(sheet, true);
        }
    }
    
    private void closeWorkbook() {
        if (getTabCount() > 0) {
            for (int i = 0; i < getTabCount(); i++) {
                JComponent jpTab = (JComponent) getTabComponentAt(i);
                Stream.of(jpTab.getPropertyChangeListeners()).forEach(jpTab::removePropertyChangeListener);
            }
            removeAll();
        }
        if (this.workbook != null && this.workbook != workbook) {
            try {
                this.workbook.close();
            } catch (IOException ex) {
            }
        }
    }

    public JComponent getSpreadsheetAndToolbar() {
        return spreadsheetAndToolbar;
    }

    public void setSpreadsheetAndToolbar(SpreadsheetTopComponent spreadsheetAndToolbar) {
        this.spreadsheetAndToolbar = spreadsheetAndToolbar;
        addPopupToTabs();
    }

    public SheetComponent getSelectedSheet() {
        return (SheetComponent) getComponentAt(workbook.getActiveSheetIndex());
    }

    public FormulaEvaluator getFormulaEvaluator() {
        return formulaEvaluator;
    }

    private void addPopupToTabs() {
        List<? extends Action> buildActions = Utilities.actionsForPath("Office/Spreadsheet/Tabs/Popup");
        final JPopupMenu menu = Utilities.actionsToPopup(buildActions.toArray(new Action[buildActions.size()]), this);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent me) {
                showPopup(me);
            }

            @Override
            public void mouseReleased(MouseEvent me) {
                showPopup(me);
            }

            private void showPopup(MouseEvent me) {
                if (me.isPopupTrigger()) {
                    menu.show(SpreadsheetComponent.this, me.getX(), me.getY());
                }
            }
        });
    }

    public void insertSheet(String name) throws IllegalArgumentException {
        Sheet sheet = workbook.createSheet(name);
        int newSheetPosition = workbook.getActiveSheetIndex() + 1;
        workbook.setSheetOrder(name, newSheetPosition);
        JPanel sheetPanel = new SheetComponent(sheet);
        insertTab(name, null, sheetPanel, null, newSheetPosition);
        sheetPanel.addPropertyChangeListener(SheetComponent.SHEET_MODIFIED_PROPERTY, pce -> setModified(true));

        setSelectedIndex(newSheetPosition);
        setModified(true);
    }

    public void removeCurrentSheet() {
        if (workbook.getNumberOfSheets() > 1) {
            int selectedSheetIndex = workbook.getActiveSheetIndex();
            workbook.removeSheetAt(selectedSheetIndex);
            remove(selectedSheetIndex);
            setModified(true);
        }
    }

    public void renameCurrentSheet(String newName) {
        int selectedSheetIndex = workbook.getActiveSheetIndex();
        workbook.setSheetName(selectedSheetIndex, newName);
        setTitleAt(selectedSheetIndex, newName);
        setModified(true);
    }

    @Override
    public void stateChanged(ChangeEvent ce) {
        workbook.setActiveSheet(getSelectedIndex());
        registerActions();
    }

    public void setModified(boolean modified) {
        if (spreadsheetAndToolbar == null) return;
        spreadsheetAndToolbar.getDataObject().setModified(modified);
    }

    /**
     * Registers the table actions also in the TopComponent (for example to active global actions)
     */
    public void registerActions() {
        if (spreadsheetAndToolbar == null) return;
        ActionMap topComponentActions = spreadsheetAndToolbar.getActionMap();
        ActionMap tableActions = getSelectedSheet().getTable().getActionMap();

        // Actives the cut / copy / paste buttons
        topComponentActions.put(DefaultEditorKit.cutAction, tableActions.get(DefaultEditorKit.cutAction));
        topComponentActions.put(DefaultEditorKit.copyAction, tableActions.get(DefaultEditorKit.copyAction));
        topComponentActions.put(DefaultEditorKit.pasteAction, tableActions.get(DefaultEditorKit.pasteAction));
        spreadsheetAndToolbar.getServices().add(styleable);
        // FIXME Actions not found. Should the property be used instead of search for the action ?
        ShowHideGridAction showHideGridAction = //(ShowHideGridAction) OfficeUIUtils.findAction("View/Office/Spreadsheet", "org-joeffice-spreadsheet-actions-ShowHideGridAction");
                FileUtil.getConfigObject("Actions/View/Office/Spreadsheet/org-joeffice-spreadsheet-actions-ShowHideGridAction.instance", ShowHideGridAction.class);
                // (ShowHideGridAction) Actions.forID("View/Office/Spreadsheet", "org.joeffice.spreadsheet.actions.ShowHideGridAction"); // ClassCastException AlwaysEnabledAction <-> ShowHideGridAction due to lazy = true by default
        if (showHideGridAction != null) {
            showHideGridAction.setBooleanState(getSelectedSheet().getSheet().isPrintGridlines());
        }
        //Action deleteCellsAction = FileUtil.getConfigObject(
        //    "Actions/Edit/Office/Spreadsheet/org-joeffice-spreadsheet-actions-DeleteCellsAction.instance", Action.class);
        //Action deleteCellsAction = OfficeUIUtils.getAction("Edit/Office/Spreadsheet", "org-joeffice-spreadsheet-actions-DeleteCellsAction");
        /*Action deleteCellsAction = new DeleteCellsAction();
        getSelectedSheet().getTable().getInputMap(WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "delete");
        tableActions.put("delete", deleteCellsAction);
        topComponentActions.put("delete", deleteCellsAction);*/
    }

    public void unregisterActions() {
        if (spreadsheetAndToolbar == null) return;
        spreadsheetAndToolbar.getServices().remove(styleable);
    }

    public static SpreadsheetComponent getSelectedInstance() {
        SpreadsheetTopComponent currentTopComponent = OfficeTopComponent.getSelectedComponent(SpreadsheetTopComponent.class);
        if (currentTopComponent != null) {
            return (SpreadsheetComponent) currentTopComponent.getMainComponent();
        }
        return null;
    }
}
