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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Sequence for date related text (eg. Mon Tue Wed ...)
 *
 * @author Anthony Goubard - Japplis
 */
public class DateStringSequence implements Sequence {

    private Locale locale;

    private List<List<String>> dateSequences = new ArrayList<>();

    public DateStringSequence() {
        this(Locale.getDefault());
    }

    public DateStringSequence(Locale locale) {
        this.locale = locale;
        addStringSequence("EEEE", Calendar.DAY_OF_WEEK);
        addStringSequence("EEE", Calendar.DAY_OF_WEEK);
        addStringSequence("MMMM", Calendar.MONTH);
        addStringSequence("MMM", Calendar.MONTH);
    }

    private void addStringSequence(String pattern, int dateField) {
        Calendar calendar = Calendar.getInstance(locale);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
        dateFormat.setCalendar(calendar);
        List<String> dateSequence = new ArrayList<>();
        for (int i = calendar.getMinimum(dateField); i <= calendar.getMaximum(dateField); i++) {
            calendar.set(dateField, i);
            String formattedText = dateFormat.format(calendar.getTime());
            dateSequence.add(formattedText);
        }
        dateSequences.add(dateSequence);
    }

    @Override
    public String getNextValue(List<String> previousValues) {
        String lastValue = previousValues.get(previousValues.size() - 1);
        for (List<String> dateSequence : dateSequences) {
            int lastValueIndex = dateSequence.indexOf(lastValue);
            if (lastValueIndex == dateSequence.size() - 1) {
                return dateSequence.get(0);
            } else if (lastValueIndex >= 0) {
                return dateSequence.get(lastValueIndex + 1);
            }
        }
        return null;
    }
}
