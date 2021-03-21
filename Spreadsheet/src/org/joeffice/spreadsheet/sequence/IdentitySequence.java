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
 * Repeat the same text.
 *
 * @author Anthony Goubard - Japplis
 */
public class IdentitySequence implements Sequence {

    @Override
    public String getNextValue(List<String> previousValues) {
        String lastValue = null;
        for (String value : previousValues) {
            if (lastValue == null) {
                lastValue = value;
            } else if (!lastValue.equals(value)) {
                return null;
            }
        }
        return lastValue;
    }
}
