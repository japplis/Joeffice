/*
 * Copyright 2021 Japplis.
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
package org.joeffice.desktop.actions;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import org.openide.awt.ActionRegistration;

/**
 * FIXME There seems to be a problem with Actions / ActionListener as they show by default as AlwaysEnabledAction[null] in layer.
 *
 * This is a workaround to show them correctly in the IDE and in the application.
 * If in @ActionRegistration 'lazy = false' is not specified the action shows as AlwaysEnabledAction[null] and
 * you can get the instance with Actions.getFromID
 * If you set 'lazy = false' the default constructor is called and iconBase is ignored
 *
 * @author Anthony Goubard - Japplis
 */
@Deprecated // Unused as this is a workaround - problem is somewhere else
public abstract class OfficeAction extends AbstractAction {

    public OfficeAction() {
        putValue(DEFAULT, getClass().getSimpleName());
        if (getClass().isAnnotationPresent(ActionRegistration.class)) { // False see warning
            ActionRegistration registration = getClass().getAnnotation(ActionRegistration.class);
            putValue(NAME, registration.displayName());
            if (!registration.iconBase().isEmpty()) {
                ImageIcon icon = new ImageIcon(getClass().getClassLoader().getResource(registration.iconBase()));
                putValue(SMALL_ICON, icon);
            }
        }
    }
}
