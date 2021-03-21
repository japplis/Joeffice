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
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Properties;
import javax.swing.*;

import org.jdesktop.swingx.scrollpaneselector.ScrollPaneSelector;

import org.joeffice.desktop.file.OfficeDataObject;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.awt.Toolbar;

import org.openide.awt.UndoRedo;
import org.openide.cookies.CloseCookie;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.CloneableTopComponent;
import org.openide.windows.TopComponent;

/**
 * Generic TopComponent to show the opened documents.
 *
 * @author Anthony Goubard - Japplis
 */
public abstract class OfficeTopComponent extends CloneableTopComponent {

    private JComponent mainComponent;
    private UndoRedo.Manager manager = new UndoRedo.Manager();
    private InstanceContent services;

    /**
     * Empty constructor used for (de)serialization.
     */
    public OfficeTopComponent() {
    }

    public OfficeTopComponent(OfficeDataObject dataObject) {
        init(dataObject);
    }

    public OfficeDataObject getDataObject() {
        OfficeDataObject dataObject = getLookup().lookup(OfficeDataObject.class);
        return dataObject;
    }

    protected void init(OfficeDataObject dataObject) {
        services = new InstanceContent();
        Lookup actionsLookup = ExplorerUtils.createLookup(new ExplorerManager(), getActionMap());
        Lookup lookup = new ProxyLookup(actionsLookup, dataObject.getLookup(), new AbstractLookup(services));
        associateLookup(lookup);

        initComponents();
        FileObject documentFileObject = dataObject.getPrimaryFile();
        String fileDisplayName = FileUtil.getFileDisplayName(documentFileObject);
        setToolTipText(fileDisplayName);
        setName(documentFileObject.getName());
        loadDocument(documentFileObject);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    protected void initComponents() {
        setLayout(new BorderLayout());
        JToolBar topToolbar = createToolbar();
        mainComponent = createMainComponent();
        if (mainComponent instanceof JScrollPane || mainComponent instanceof JTabbedPane) {
            add(mainComponent);
        } else {
            JScrollPane mainPane = new JScrollPane(mainComponent);
            mainPane.getVerticalScrollBar().setUnitIncrement(16);
            ScrollPaneSelector.installScrollPaneSelector(mainPane);
            //JLayer<Component> zoomLayer = new JLayer<>(mainPane.getViewport().getView(), new ZoomLayerUI());
            //mainPane.getViewport().setView(zoomLayer);
            add(mainPane);
        }

        add(topToolbar, BorderLayout.NORTH);
    }

    protected JToolBar createToolbar() {
        JToolBar toolbar = new Toolbar(getShortName());
        String toolbarActionsPath = "Office/" + getShortName() + "/Toolbar";
        List<? extends Action> toolbarActions = Utilities.actionsForPath(toolbarActionsPath);
        for (Action action : toolbarActions) {
            if (action == null) {
                toolbar.addSeparator();
            } else if (action instanceof Presenter.Toolbar) {
                Component actionComponent = ((Presenter.Toolbar) action).getToolbarPresenter();
                toolbar.add(actionComponent);
            } else {
                JButton newButton = toolbar.add(action);
                if (newButton.getToolTipText() == null) {
                    String label = (String) action.getValue("displayName");
                    newButton.setToolTipText(label);
                }
            }
        }
        return toolbar;
    }

    protected abstract JComponent createMainComponent();

    public JComponent getMainComponent() {
        return mainComponent;
    }

    @Messages({"# {0} - file name", "MSG_Opening=Opening {0}"})
    protected void loadDocument(FileObject documentFileObject) {
        final File documentFile = FileUtil.toFile(documentFileObject);
        // String openingTitle = NbBundle.getMessage(getClass(), "MSG_Opening", documentFile.getName());
        final ProgressHandle progress = ProgressHandleFactory.createHandle("Opening " + documentFile.getName());
        progress.start();
        SwingWorker loader = new DocumentLoader(documentFile, progress);
        RequestProcessor requestProcessor = new RequestProcessor(getClass());
        requestProcessor.post(loader);
    }

    protected abstract Object loadDocument(File documentFile) throws Exception;

    protected void documentLoaded() {
    }

    public InstanceContent getServices() {
        return services;
    }

    public static <T> T getSelectedComponent(Class<T> expectedTopComponent) {
        TopComponent selected = TopComponent.getRegistry().getActivated();
        if (selected.getClass().isAssignableFrom(expectedTopComponent)) {
            return (T) selected;
        } else {
            return null;
        }
    }

    @Override
    protected CloneableTopComponent createClonedObject() {
        try {
            // Use reflection
            Constructor componentContructor = getClass().getConstructor(OfficeDataObject.class);
            Object newComponent = componentContructor.newInstance(getDataObject());
            return (CloneableTopComponent) newComponent;
        } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @Override
    protected boolean closeLast() {
        OfficeDataObject dataObject = getDataObject();
        int answer = OfficeUIUtils.checkSaveBeforeClosing(dataObject, this);
        boolean canClose = answer == JOptionPane.YES_OPTION || answer == JOptionPane.NO_OPTION;
        if (canClose && dataObject != null) {
            dataObject.setModified(false);
        }
        return canClose;
    }

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    @Override
    public UndoRedo getUndoRedo() {
        return manager;
    }

    public void writeProperties(Properties properties) {
        properties.setProperty("version", "1.0");
        File closingFile = FileUtil.toFile(getDataObject().getPrimaryFile());
        properties.setProperty("path", closingFile.getAbsolutePath());
    }

    public void readProperties(Properties properties) {
        String version = properties.getProperty("version");
        try {
            String path = properties.getProperty("path");
            File openingFile = FileUtil.normalizeFile(new File(path));
            FileObject openingFileObject = FileUtil.toFileObject(openingFile);
            OfficeDataObject openingDataObject = (OfficeDataObject) DataObject.find(openingFileObject);
            init(openingDataObject);

            // If the file has moved or has been deleted
        } catch (DataObjectNotFoundException ex) {
            close();
        }
    }

    private class ChangeTitleIfModified implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals(DataObject.PROP_MODIFIED)) {
                final boolean modified = (Boolean) evt.getNewValue();
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if (modified) {
                            setHtmlDisplayName("<html><body><b>" + getDataObject().getName());
                        } else {
                            setHtmlDisplayName("<html><body>" + getDataObject().getName());
                        }
                    }
                });
            }
        }
    }

    class DocumentLoader extends SwingWorker {

        private boolean successful;
        private File documentFile;
        private ProgressHandle progress;

        DocumentLoader(File documentFile, ProgressHandle progress) {
            this.documentFile = documentFile;
            this.progress = progress;
        }

        @Override
        protected Object doInBackground() throws Exception {
            try {
                Object document = loadDocument(documentFile);
                getDataObject().setDocument(document);
                successful = true;
                return document;
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                throw ex;
            }
        }

        @Override
        protected void done() {
            if (successful) {
                documentLoaded();
            } else {
                getDataObject().getLookup().lookup(CloseCookie.class).close();
            }
            progress.finish();
            getDataObject().addPropertyChangeListener(new ChangeTitleIfModified());
        }
    }

    // See https://code.google.com/p/link-collector/source/browse/ver2/trunk/labs/zoom-with-jxpanel-src/org/pbjar/jxlayer/plaf/ext/TransformUI.java
    /*class ZoomLayerUI extends LayerUI<Component> {

        @Override
        public void paint(Graphics g, JComponent c) {
            int w = c.getWidth();
            int h = c.getHeight();

            if (w == 0 || h == 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) g;

            g2.scale(2.0, 2.0);
            super.paint(g2, c);
        }
    }*/
}
