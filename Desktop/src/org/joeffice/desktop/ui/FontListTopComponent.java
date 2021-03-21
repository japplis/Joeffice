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

package org.joeffice.desktop.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.swingx.JXList;
import org.joeffice.desktop.actions.ChooseFontAction;


import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.ErrorManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.cookies.InstanceCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;


/**
 * Top component which the fonts.
 */
@ConvertAsProperties(
    dtd="-//org.joeffice.desktop.ui//FontList//EN",
    autostore=false
)
@TopComponent.Description(
    preferredID="FontListTopComponent",
    //iconBase="SET/PATH/TO/ICON/HERE",
    persistenceType=TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "palette", openAtStartup = false)
@ActionID(category = "Window", id = "org.joeffice.desktop.ui.FontListTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
    displayName = "#CTL_FontListAction",
    preferredID="FontListTopComponent"
)
@Messages({
    "CTL_FontListAction=Fonts",
    "CTL_FontListTopComponent=Fonts",
    "HINT_FontListTopComponent=This is a Font window"
})
public final class FontListTopComponent extends TopComponent implements ListSelectionListener, DocumentListener {

    private JTextField filterField;
    private JXList fontList;

    public FontListTopComponent() {
        initComponents();
        setName(Bundle.CTL_FontListTopComponent());
        setToolTipText(Bundle.HINT_FontListTopComponent());
        setFocusable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(5,5));
        filterField = new JTextField();
        filterField.getDocument().addDocumentListener(this);
        add(filterField, BorderLayout.NORTH);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = env.getAvailableFontFamilyNames();
        fontList = new JXList(fontFamilies);
        fontList.setAutoCreateRowSorter(true);
        fontList.setCellRenderer(new FontCellRenderer());
        fontList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        fontList.addListSelectionListener(this);
        fontList.setFocusable(false);
        add(new JScrollPane(fontList));
    }

    @Override
    public void valueChanged(ListSelectionEvent lse) {
        String selectedFont = (String) fontList.getSelectedValue();
        if (selectedFont != null) {
            //Action chooseFont = findAction("Actions/Edit/Office/org-joeffice-desktop-actions-ChooseFontAction.instance");
            Action chooseFont = Utilities.actionsGlobalContext().lookup(ChooseFontAction.class);
            ActionEvent event = new ActionEvent(lse.getSource(), lse.getFirstIndex(), selectedFont);
            chooseFont.actionPerformed(event);
            /*Styleable styleable = Lookup.getDefault().lookup(Styleable.class);
            AttributedString attributes = new AttributedString("ChangeFont");
            attributes.addAttribute(TextAttribute.FAMILY, selectedFont);
            styleable.setFontAttributes(attributes);*/
        }
    }

    public void noSelectionListener() {
        fontList.removeListSelectionListener(this);
    }

    public String getSelectedFontName() {
        return (String) fontList.getSelectedValue();
    }

    public Action findAction(String key) {
        FileObject fo = FileUtil.getConfigFile(key);
        if (fo != null && fo.isValid()) {
            try {
                DataObject dob = DataObject.find(fo);
                InstanceCookie ic = dob.getLookup().lookup(InstanceCookie.class);
                if (ic != null) {
                    Object instance = ic.instanceCreate();
                    if (instance instanceof Action) {
                        Action a = (Action) instance;
                        return a;
                    }
                }
            } catch (Exception e) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
                return null;
            }
        }
        return null;
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
        updateFilter();
    }

    void updateFilter() {
        String text = filterField.getText();
        fontList.setRowFilter(RowFilter.regexFilter("(?i)" + text)); // Doesn't work
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }
    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }

    class FontCellRenderer extends JPanel implements ListCellRenderer<String> {

        private Color selectedColor;
        private JLabel fontNameLabel;
        private JLabel fontExampleLabel;

        public FontCellRenderer() {
            fontNameLabel = new JLabel();
            fontNameLabel.setOpaque(false);
            fontExampleLabel = new JLabel("abcABC");
            fontExampleLabel.setOpaque(false);
            fontExampleLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
            BoxLayout stretchLayout = new BoxLayout(this, BoxLayout.X_AXIS);
            setLayout(stretchLayout);
            add(fontNameLabel);
            add(Box.createHorizontalGlue());
            add(fontExampleLabel);
            selectedColor = UIManager.getColor("List.selectionBackground");
            setBackground(UIManager.getColor("List.background"));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            fontNameLabel.setText(value);
            fontExampleLabel.setFont(new Font(value, Font.PLAIN, fontNameLabel.getFont().getSize()));
            if (isSelected) {
                setBorder(new LineBorder(selectedColor));
            } else {
                setBorder(null);
            }
            return this;
        }
    }
}
