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

import java.util.List;

/**
 * Completes the sequence based on the same incrementation.
 *
 * @author Anthony Goubard - Japplis
 */
public class NumberSequence implements Sequence {

    @Override
    public String getNextValue(List<String> previousValues) {
        double incrementation = 1.0;
        boolean firstValue = true;
        boolean secondValue = true;
        double previousValue = 0.0;
        for (String value : previousValues) {
            try {
                double numberValue = Double.parseDouble(value);
                if (firstValue) {
                    previousValue = numberValue;
                    firstValue = false;
                } else {
                    double lastIncrementation = numberValue - previousValue;
                    if (secondValue) {
                        incrementation = lastIncrementation;
                        secondValue = false;
                    } else if (incrementation != lastIncrementation) {
                        return null;
                    }
                    previousValue = numberValue;
                }
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        double nextValue = previousValue + incrementation;
        String valueAsString = String.valueOf(nextValue);
        if (valueAsString.endsWith(".0")) {
            valueAsString  = valueAsString.substring(0, valueAsString.length() - 2);
        }
        return valueAsString;
    }
}
