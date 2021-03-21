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
package org.joeffice.wordprocessor;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.apache.poi.xwpf.usermodel.*;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

/**
 * Top component which displays the tree structure.
 *
 * @author Anthony Goubard
 * @author Stanislav Lapitsky
 */
@ConvertAsProperties(
        dtd = "-//org.joeffice.wordprocessor//DocumentTree//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "DocumentTreeTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER)
@TopComponent.Registration(mode = "rightSlidingSide", openAtStartup = false)
@ActionID(category = "Window", id = "org.joeffice.wordprocessor.DocumentTreeTopComponent")
//@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DocumentTreeAction",
        preferredID = "DocumentTreeTopComponent")
@Messages({
    "CTL_DocumentTreeAction=DocumentTree",
    "CTL_DocumentTreeTopComponent=DocumentTree Window",
    "HINT_DocumentTreeTopComponent=This is a DocumentTree window"
})
public final class DocumentTreeTopComponent extends TopComponent {

    JTree trDocument = new JTree() {
        @Override
        public String getToolTipText(MouseEvent event) {
            return processDocumentTooltip(event);
        }
    };

    JTree poiDocumentTree = new JTree() {
        @Override
        public String getToolTipText(MouseEvent event) {
            return ""; //processPOIDocumentTooltip(event);
        }
    };

    DocxDocument doc;
    private XWPFDocument poiDocument;

    public DocumentTreeTopComponent() {
        initComponents();
        setName(Bundle.CTL_DocumentTreeTopComponent());
        setToolTipText(Bundle.HINT_DocumentTreeTopComponent());
        JTextPane edit = WordProcessorTopComponent.findCurrentTextPane();
        doc = (DocxDocument) edit.getDocument();
        poiDocument = (XWPFDocument) doc.getProperty("XWPFDocument");
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        JSplitPane horizontalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        horizontalSplit.setTopComponent(new JScrollPane(trDocument));
        horizontalSplit.setBottomComponent(new JScrollPane(poiDocumentTree));
        add(horizontalSplit);
        ToolTipManager.sharedInstance().registerComponent(trDocument);
    }

    public void createDOMDocumentTree() {
        Element elem = doc.getDefaultRootElement();
        if (elem instanceof TreeNode) {
            trDocument.setModel(new DefaultTreeModel((TreeNode) elem));
        } else {
            DefaultMutableTreeNode node1 = new DefaultMutableTreeNode(elem);
            buildElementsTree(node1, elem);
            trDocument.setModel(new DefaultTreeModel(node1));
        }
    }

    public void buildElementsTree(DefaultMutableTreeNode root, Element elem) {
        for (int i = 0; i < elem.getElementCount(); i++) {
            AttributeSet attrs = getAttributes(elem.getElement(i));
            String str = elem.getElement(i).toString() + " " + attrs.getClass().getName() + "@" + Integer.toHexString(attrs.hashCode());
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(str);
            root.add(node);
            buildElementsTree(node, elem.getElement(i));
        }
    }

    protected AttributeSet getAttributes(Element elem) {
        if (elem instanceof AbstractDocument.AbstractElement) {
            try {
                Field f = AbstractDocument.AbstractElement.class.getDeclaredField("attributes");
                f.setAccessible(true);
                AttributeSet res = (AttributeSet) f.get(elem);
                return res;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    protected String processDocumentTooltip(MouseEvent e) {
        int rn = trDocument.getRowForLocation(e.getX(), e.getY());
        if (trDocument.getPathForRow(rn) != null) {
            Element tn = (Element) trDocument.getPathForRow(rn).getLastPathComponent();
            StringBuffer buff = new StringBuffer();
            buff.append("<html>");
            buff.append("<b>Start offset: </b>").append(tn.getStartOffset()).append("<br>");
            buff.append("<b>End offset: </b>").append(tn.getEndOffset()).append("<br>");
            buff.append("<b>Child count: </b>").append(tn.getElementCount()).append("<br>");
            buff.append("<b>Text: </b>\"").append(getText(tn.getDocument(), tn.getStartOffset(), tn.getEndOffset())).append("\"<br>");
            buff.append("<b>Attributes: </b>").append("<br>");
            Enumeration names = tn.getAttributes().getAttributeNames();
            while (names.hasMoreElements()) {
                Object name = names.nextElement();
                Object value = tn.getAttributes().getAttribute(name);
                buff.append("&nbsp;&nbsp;<b>").append(name).append(":</b>").append(value).append("<br>");
            }
            buff.append("</html>");
            return buff.toString();
        }

        return null;
    }

    protected String getText(Document doc, int startOffset, int endOffset) {
        try {
            String text = doc.getText(startOffset, endOffset - startOffset);
            text = text.replaceAll("\n", "\\\\n");
            text = text.replaceAll("\t", "\\\\t");
            text = text.replaceAll("\r", "\\\\r");

            return text;
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    int currentOffset = 0;

    public void createPOIDocumentTree() {
        currentOffset = 0;
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        buildPart(root, poiDocument.getBodyElements());
        poiDocumentTree.setModel(new DefaultTreeModel(root));
    }

    public void buildPart(DefaultMutableTreeNode root, List<IBodyElement> content) {
        for (IBodyElement elem : content) {
            if (elem instanceof XWPFParagraph) {
                String nodeText = elem.getClass().getSimpleName() + ":" + currentOffset;
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeText);
                root.add(node);
                buildParagraph(node, (XWPFParagraph) elem);
                //currentOffset++;
            } else if (elem instanceof XWPFTable) {
                //buildTable(root, (XWPFTable) elem);
            }
        }
    }

    public void buildParagraph(DefaultMutableTreeNode root, XWPFParagraph paragraph) {
        for (XWPFRun run : paragraph.getRuns()) {
            String nodeText = paragraph.getClass().getSimpleName() + ":" + currentOffset;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeText);
            root.add(node);
            buildRun(node, run);
        }
    }

    public void buildRun(DefaultMutableTreeNode root, XWPFRun run) {
        List<CTText> texts = run.getCTR().getTList();
        for (CTText text : texts) {
            String nodeText = run.getClass().getSimpleName() + ":" + currentOffset;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(nodeText);
            root.add(node);
            buildCTText(node, text);
        }
    }

    public void buildCTText(DefaultMutableTreeNode root, CTText text) {
        String textValue = text.getStringValue();
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(textValue);
        root.add(node);
        currentOffset += textValue.length();
    }

    @Override
    protected void componentActivated() {
        createDOMDocumentTree();
        createPOIDocumentTree();
    }

    void writeProperties(java.util.Properties p) {
        p.setProperty("version", "1.0");
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
    }
}
