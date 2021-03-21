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
package org.joeffice.database;

import java.awt.Font;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

import org.joeffice.database.tablemodel.TableMetaDataModel;
import org.joeffice.desktop.ui.OfficeUIUtils;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

/**
 * Panel that shows the column names of the database and allow to enter a value for them.
 *
 * @author Anthony Goubard - Japplis
 */
public class FieldsPanel extends JPanel {

    public final static int LIMIT_FOR_SCROLLPANE = 15;
    private final static String COMPONENTS_KEY = "Components";

    private TableMetaDataModel metaData;

    public FieldsPanel(TableMetaDataModel metaData) {
        this.metaData = metaData;
        initUI();
    }

    protected void initUI() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        List<JLabel> fieldLabels = new ArrayList<>();
        List<JComponent> fieldComponents = new ArrayList<>();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
            JLabel fieldLabel = getLabelForField(i);
            fieldLabels.add(fieldLabel);

            JComponent fieldComponent = getComponentForField(i);
            fieldComponents.add(fieldComponent);
        }
        putClientProperty(COMPONENTS_KEY, fieldComponents);

        GroupLayout.ParallelGroup labelGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false);
        for (JLabel fieldLabel : fieldLabels) {
            labelGroup.addComponent(fieldLabel, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE);
        }
        GroupLayout.ParallelGroup componentGroup = layout.createParallelGroup(GroupLayout.Alignment.TRAILING, false);
        for (JComponent fieldComponent : fieldComponents) {
            componentGroup.addComponent(fieldComponent, GroupLayout.Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE, Short.MAX_VALUE);
        }
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(labelGroup)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(componentGroup)
                .addContainerGap()));

        GroupLayout.SequentialGroup fieldsGroup = layout.createSequentialGroup().addContainerGap();
        for (int i = 0; i < fieldLabels.size(); i++) {
            JLabel fieldLabel = fieldLabels.get(i);
            JComponent fieldComponent = fieldComponents.get(i);
            fieldsGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED);
            fieldsGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    .addComponent(fieldLabel)
                    .addComponent(fieldComponent));
        }
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(fieldsGroup));
    }

    private JLabel getLabelForField(int index) {
        String columnName = (String) metaData.getValueAt(0, index);
        String labelText = OfficeUIUtils.toDisplayable(columnName);
        JLabel fieldLabel = new JLabel(labelText);
        if ((Boolean) metaData.getValueAt(2, index) || (Boolean) metaData.getValueAt(3, index)) {
            fieldLabel.setFont(fieldLabel.getFont().deriveFont(Font.BOLD));
        }
        return fieldLabel;
    }

    private JComponent getComponentForField(int index) {
        JComponent fieldComponent;
        String fieldType = (String) metaData.getValueAt(1, index);
        switch (fieldType) {
            case "BOOLEAN":
                fieldComponent = new JCheckBox();
                break;
            default:
                fieldComponent = new JTextField(40);
        }
        String columnName = (String) metaData.getValueAt(0, index);
        fieldComponent.putClientProperty("COLUMN_NAME", columnName);
        return fieldComponent;
    }

    public static Map<String, Object> askFields(TableMetaDataModel metaData, String title) {
        Map<String, Object> fieldValues = new LinkedHashMap<>();
        JPanel fieldsPanel = new FieldsPanel(metaData);
        DialogDescriptor descriptor;
        if (metaData.getColumnCount() > LIMIT_FOR_SCROLLPANE) {
            descriptor = new DialogDescriptor(new JScrollPane(fieldsPanel), title);
        } else {
            descriptor = new DialogDescriptor(fieldsPanel, title);
        }
        Object dialogResult = DialogDisplayer.getDefault().notify(descriptor);
        if (dialogResult == DialogDescriptor.OK_OPTION) {
            List<JComponent> components = (List<JComponent>) fieldsPanel.getClientProperty(COMPONENTS_KEY);
            for (int i = 0; i < components.size(); i++) {
                JComponent fieldComponent = components.get(i);
                String columnName = (String) fieldComponent.getClientProperty("COLUMN_NAME");
                if (fieldComponent instanceof JTextField) {
                    String value = ((JTextField) fieldComponent).getText();
                    fieldValues.put(columnName, value);
                } else if (fieldComponent instanceof JCheckBox) {
                    Boolean value = ((JCheckBox) fieldComponent).isSelected();
                    fieldValues.put(columnName, value);
                }
            }
        }
        return fieldValues;
    }
}
