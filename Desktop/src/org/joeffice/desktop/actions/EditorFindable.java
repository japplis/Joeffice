/*
 * Copyright 2023 Japplis.
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

import static java.awt.font.TextAttribute.*;
import static org.joeffice.desktop.actions.ExtraTextAttribute.*;
import static javax.swing.text.StyleConstants.*;

import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.AttributedString;
import java.util.EnumSet;
import java.util.Enumeration;

import javax.swing.JTextPane;
import javax.swing.text.*;

import org.joeffice.desktop.ui.Findable;

/**
 * Class that applies the style to the editor.
 *
 * This involves converting the {@link AttributedString} to an {@link AttributeSet} and vice versa.
 *
 * @author Anthony Goubard - Japplis
 */
public class EditorFindable implements Findable {

    private JTextPane textPane;
    private int position = 0;
    private String lastSearch;
    private EnumSet<FindOption> lastOptions;

    public EditorFindable(JTextPane textPane) {
        this.textPane = textPane;
    }

    @Override
    public boolean find(String search, EnumSet<FindOption> options) {
        lastSearch = search;
        lastOptions = options;
        return find(0);
    }
    
    private boolean find(int fromPosition) {
        try {
            String text = textPane.getDocument().getText(0, textPane.getDocument().getLength());
            position = find(text, lastSearch, fromPosition, lastOptions);
            if (position != -1) {
                textPane.setCaretPosition(position);
            }
            return position != -1;
        } catch (BadLocationException ex) {
            return false;
        }
        
    }

    @Override
    public boolean findNext(boolean forward) {
        if (position == textPane.getCaretPosition()) {
            position++;
        }
        return find(position);
    }
}
