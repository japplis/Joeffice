/*
 * Copyright 2021
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

import javax.swing.UIManager;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        // FIXME Weird that it's needed as I would except to be done by the FlatLaf module itself:
        // https://github.com/apache/netbeans/blob/master/platform/o.n.swing.laf.flatlaf/src/org/netbeans/swing/laf/flatlaf/Installer.java
        UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo("FlatLaf Dark", FlatDarkLaf.class.getName()));
        UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo("FlatLaf Light", FlatLightLaf.class.getName()));
        //UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo("FlatLaf Darcula", FlatDarculaLaf.class.getName()));
    }

}
