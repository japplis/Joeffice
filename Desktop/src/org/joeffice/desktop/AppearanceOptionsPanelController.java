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
package org.joeffice.desktop;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import org.joeffice.desktop.actions.NewFileAction;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

/**
 * This class contains the setting controlor for the application appearance settings.
 *
 * @author Anthony Goubard - Japplis
 */
@OptionsPanelController.SubRegistration(
        location = "Advanced",
        displayName = "#AdvancedOption_DisplayName_Appearance",
        keywords = "#AdvancedOption_Keywords_Appearance",
        keywordsCategory = "Advanced/Appearance")
@Messages({
    "AdvancedOption_DisplayName_Appearance=Appearance",
    "AdvancedOption_Keywords_Appearance=appearance,look,feel,language,langue,taal,idioma,lengua,aspect",
    "MSG_RestartApplication=Restart this application to change the language"
})
public final class AppearanceOptionsPanelController extends OptionsPanelController {

    private AppearancePanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    @Override
    public void update() {
        getPanel().load();
        panel.setLanguage(Locale.getDefault().getLanguage());
        changed = false;
    }

    @Override
    public void applyChanges() {
        getPanel().store();
        String chosenLanguage = panel.getChosenLanguage();
        String currentLanguage = Locale.getDefault().getLanguage();
        if (!(chosenLanguage.equals(currentLanguage) ||
                (!panel.getLanguagesAsISO().contains(currentLanguage) && "en".equals(chosenLanguage)))) {
            changeLocale(chosenLanguage);
        }
        changed = false;
    }

    private void changeLocale(String newLanguage) {
        Path confPath = FileSystems.getDefault().getPath(System.getProperty ("netbeans.home"), "etc", "joeffice.conf");
        try {
            List<String> confLines = Files.readAllLines(confPath, Charset.forName("ISO-8859-1"));
            for (int i = 0; i < confLines.size(); i++) {
                String line = confLines.get(i);
                if (line.startsWith("default_options=\"")) {
                    String newLine = line.replaceFirst("\\-\\-locale [a-z]{2}", "--locale " + newLanguage);
                    if (!newLine.contains("--locale ")) {
                        newLine = line.substring(0, line.lastIndexOf("\"")) + " --locale " + newLanguage + "\"";
                    }
                    confLines.set(i, newLine);
                }
            }
            Files.write(confPath, confLines, Charset.forName("ISO-8859-1"), StandardOpenOption.CREATE);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        showRestartMessage();
    }

    private void showRestartMessage() {
        String restartApplicationMessage = NbBundle.getMessage(NewFileAction.class, "MSG_RestartApplication");
        NotifyDescriptor restartApplicationMessageDialog =
                new NotifyDescriptor.Message(restartApplicationMessage, NotifyDescriptor.INFORMATION_MESSAGE);
        DialogDisplayer.getDefault().notify(restartApplicationMessageDialog);
    }

    @Override
    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    @Override
    public boolean isValid() {
        return getPanel().valid();
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private AppearancePanel getPanel() {
        if (panel == null) {
            panel = new AppearancePanel();
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
