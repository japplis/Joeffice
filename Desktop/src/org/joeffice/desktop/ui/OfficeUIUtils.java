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

import java.awt.Component;
import java.awt.GridLayout;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.StringTokenizer;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;

import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;
import org.openide.cookies.SaveCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.Lookup.Item;
import org.openide.util.Lookup.Template;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 * Generic UI utilities.
 *
 * @author Anthony Goubard - Japplis
 */
public class OfficeUIUtils {

    @NbBundle.Messages({"MSG_SAVE_BEFORE_CLOSE=Save before closing?"})
    public static int checkSaveBeforeClosing(DataObject dataObject, TopComponent component) {
        if (dataObject == null) {
            return JOptionPane.NO_OPTION;
        }

        boolean isModified = dataObject.isModified();
        int answer = JOptionPane.NO_OPTION;
        if (isModified) {
            String question = NbBundle.getMessage(OfficeUIUtils.class, "MSG_SAVE_BEFORE_CLOSE");
            answer = JOptionPane.showConfirmDialog(component, question, question, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION) {
                try {
                    SaveCookie saveCookie = dataObject.getCookie(SaveCookie.class);
                    saveCookie.save();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        // This seams to get rid of the data object from the memory, otherwise reopening the file would open the one in memory
        try {
            dataObject.setValid(false);
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
        return answer;
    }

    /**
     * String utility method that changes TEST_NAME to Test Name
     *
     * @param technicalName the technical name (e.g. a database table name)
     * @return the display name
     */
    public static String toDisplayable(String technicalName) {
        String noUnderscoreName = technicalName.replace('_', ' ');
        StringTokenizer wordsIterator = new StringTokenizer(noUnderscoreName, " ");

        StringBuilder displayableName = new StringBuilder(technicalName.length());
        boolean hasNextWord = wordsIterator.hasMoreTokens();
        while (hasNextWord) {
            String nextWord = wordsIterator.nextToken();
            if (nextWord.toUpperCase().equals(nextWord) && nextWord.length() > 1) {
                nextWord = nextWord.charAt(0) + nextWord.substring(1, nextWord.length()).toLowerCase();
            }
            displayableName.append(nextWord);
            hasNextWord = wordsIterator.hasMoreTokens();
            if (hasNextWord) {
                displayableName.append(' ');
            }
        }
        return displayableName.toString();
    }

    /**
     * Ask the user for a value
     *
     * @param title
     *      the title for the dialog
     * @param optionType
     *      the option types of the buttons such as in {@link DialogDescriptor}
     * @param messages
     *      the elements to display can be String or Component.
     */
    public static Object ask(String title, int optionType, Object... messages) {
        JPanel askPanel = new JPanel(new GridLayout(messages.length, 1, 5, 5));
        askPanel.setBorder(new EmptyBorder(10, 10, 0, 10));
        for (Object message : messages) {
            if (message instanceof String) {
                askPanel.add(new JLabel((String) message));
            } else if (message instanceof Component) {
                askPanel.add((Component) message);
            }
        }
        DialogDescriptor description = new DialogDescriptor(askPanel, title);
        Object dialogAnswer = DialogDisplayer.getDefault().notify(description);
        return dialogAnswer;
    }

    /**
     * Retrieves an action instance
     * @param category e.g., "Maps"
     * @param id e.g., "com-emxsys-worldwind-ribbon-actions-ToggleLayerAction"
     * @return the Action instance or null
     * @see http://forums.netbeans.org/topic44790.html
     */
    @Deprecated
    public static Action getAction(String category, String id) {
        String folder = "Actions/" + category + "/";
        Lookup pathLookup = Lookups.forPath(folder);

        Template<Action> actionTemplate = new Template<>(Action.class, folder + id, null);
        Item<Action> item = pathLookup.lookupItem(actionTemplate);
        if (item != null) {
            return item.getInstance();
        }
        return null;
    }

    // https://stackoverflow.com/questions/32008616/how-to-enable-disable-action-in-netbeans-platform
    public static Action findAction(String category, String actionName) {
        FileObject myActionsFolder = FileUtil.getConfigFile("Actions/" + category);
        if (myActionsFolder != null){
            FileObject[] myActionsFolderKids = myActionsFolder.getChildren();
            for (FileObject fileObject : myActionsFolderKids) {
                //Probably want to make this more robust,
                //but the point is that here we find a particular Action:
                if (fileObject.getName().contains(actionName)) {
                    try {
                        DataObject dob = DataObject.find(fileObject);
                        InstanceCookie instanceCookie = dob.getLookup().lookup(InstanceCookie.class);
                        if (instanceCookie != null) {
                            Object instance = instanceCookie.instanceCreate();
                            if (instance instanceof Action) {
                                Action action = (Action) instance;
                                return action;
                            }
                        }
                    } catch (Exception e) {
                        ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
                        return null;
                    }
                }
            }
        }
        return null;
    }
}
