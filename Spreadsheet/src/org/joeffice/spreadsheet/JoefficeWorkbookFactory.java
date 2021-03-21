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
package org.joeffice.spreadsheet;

import java.io.File;
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import org.joeffice.spreadsheet.csv.SmartCsvReader;

/**
 * Workbook factory that also support CSV files.
 *
 * @author Anthony Goubard - Japplis
 */
public class JoefficeWorkbookFactory {

    public static Workbook create(File file) throws IOException, InvalidFormatException {
        String fileName = file.getName().toLowerCase();
        if (fileName.endsWith(".csv") || fileName.endsWith(".txt")) {
            SmartCsvReader csvReader = new SmartCsvReader();
            return csvReader.read(file);
        } else {
            return WorkbookFactory.create(file);
        }
    }
}
