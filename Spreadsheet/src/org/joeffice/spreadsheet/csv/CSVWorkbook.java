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
package org.joeffice.spreadsheet.csv;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Workbook that handle csv files.
 *
 * @author Anthony Goubard - Japplis
 */
public class CSVWorkbook extends XSSFWorkbook {

    private SmartCsvReader reader;

    public CSVWorkbook(SmartCsvReader reader) {
        this.reader = reader;
    }

    // The write method is final in XSSFWorkbook so I've change the name
    public void write2(OutputStream output) throws IOException {
        reader.write(output, this);
    }
}
