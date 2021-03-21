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
package org.joeffice.spreadsheet.sequence;

import java.util.*;
import org.openide.util.NbBundle;

/**
 * Sequence for date related text (eg. Mon Tue Wed ...)
 *
 * @author Anthony Goubard - Japplis
 */
public class ResourceBundleSequence implements Sequence {

    private Locale locale;

    private List<List<String>> textSequences = new ArrayList<>();

    public ResourceBundleSequence() {
        this(Locale.getDefault());
    }

    public ResourceBundleSequence(Locale locale) {
        this.locale = locale;
        addStringSequences();
    }

    private void addStringSequences() {
        ResourceBundle resource = NbBundle.getBundle("org.joeffice.spreadsheet.sequence.FreeTextSequences", locale);
        Enumeration<String> keys = resource.getKeys();
        while (keys.hasMoreElements()) {
            String nextKey = keys.nextElement();
            String value = resource.getString(nextKey);
            List<String> sequence = Arrays.asList(value.split(","));
            textSequences.add(sequence);
        }
    }

    @Override
    public String getNextValue(List<String> previousValues) {
        String lastValue = previousValues.get(previousValues.size() - 1);
        for (List<String> textSequence : textSequences) {
            int lastValueIndex = textSequence.indexOf(lastValue);
            if (lastValueIndex == textSequence.size() - 1) {
                return textSequence.get(0);
            } else if (lastValueIndex >= 0) {
                return textSequence.get(lastValueIndex + 1);
            }
        }
        return null;
    }
}
