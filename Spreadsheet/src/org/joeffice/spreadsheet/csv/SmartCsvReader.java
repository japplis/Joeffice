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

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.h2.tools.Csv;
import org.h2.tools.SimpleResultSet;
import org.joeffice.spreadsheet.cell.CellUtils;
import org.mozilla.universalchardet.UniversalDetector;

/**
 * Smart CSV reader is a CSV reader that is able to detect:
 * <ul><li>The character encoding of the file</li>
 * <li>The field separator</li>
 * <li>The escape character (quote)</li>
 * </ul>
 *
 * @author Anthony Goubard - Japplis
 */
public class SmartCsvReader {

    private Charset charset;
    private Csv csvMetadata;
    private String[] headers;

    public SmartCsvReader() {
        csvMetadata = new Csv();
        csvMetadata.setLineCommentCharacter('#');
    }

    protected void detectFormat(File csvFile) {
        List<String> lines = detectCharset(csvFile.toPath());
        String header = lines.get(0);
        int index = 1;
        while ((header.isEmpty() || header.charAt(0) == csvMetadata.getLineCommentCharacter()) && index < lines.size()) {
            header = lines.get(index);
            index++;
        }
        detectDelimiter(header);
        headers = getValues(header, true);
        detectEscapeCharacter(lines);
    }

    private List<String> detectCharset(Path csvPath) {
        String detectedCharset = getDetectedCharset(csvPath);
        List<String> lines = read(csvPath, Charset.forName(detectedCharset));
        if (lines == null) {
            lines = read(csvPath, StandardCharsets.UTF_8);
        }
        if (lines == null) {
            lines = read(csvPath, Charset.defaultCharset());
        }
        if (lines == null) {
            lines = read(csvPath, StandardCharsets.ISO_8859_1);
        }
        return lines;
    }

    private String getDetectedCharset(Path csvPath) {
        UniversalDetector detector = new UniversalDetector(null);
        try {
            byte[] contentAsBytes = Files.readAllBytes(csvPath);
            detector.handleData(contentAsBytes, 0, contentAsBytes.length);
            detector.dataEnd();
            if (detector.isDone()) {
                return detector.getDetectedCharset();
            }
        } catch (IOException ex) {
            // Ignore and use UTF-8
        }
        return "UTF-8";
    }

    private List<String> read(Path path, Charset charset) {
        try {
            List<String> lines = Files.readAllLines(path, charset);
            this.charset = charset;
            return lines;
        } catch (IOException iex) {
            // Wrong charset probably
            return null;
        }
    }

    private void detectDelimiter(String header) {
        int tabCount = header.split("\t").length;
        int commaCount = header.split(",").length;
        int semiColomCount = header.split(";").length;
        char fieldSeparator = '\t';
        if (tabCount > commaCount && tabCount > semiColomCount) {
            fieldSeparator = '\t';
        } else if (commaCount > tabCount && commaCount > semiColomCount) {
            fieldSeparator = ',';
        } else if (semiColomCount > tabCount && semiColomCount > commaCount) {
            fieldSeparator = ';';
        }
        csvMetadata.setFieldSeparatorRead(fieldSeparator);
        csvMetadata.setFieldSeparatorWrite("" + fieldSeparator);
    }

    public String[] getValues(String line, boolean removeQuotes) {
        // This won't work if a delimiter is in a quoted text
        String[] values;
        if (csvMetadata.getFieldDelimiter() == '\t') {
            values = line.split("\\t");
        } else {
            values = line.split("" + csvMetadata.getFieldSeparatorRead());
        }
        if (removeQuotes && csvMetadata.getFieldDelimiter() > 0) {
            for (int i = 0; i < values.length; i++) {
                String value = values[i];
                if (value.length() > 1 && value.startsWith("" + csvMetadata.getFieldDelimiter()) && value.endsWith("" + csvMetadata.getFieldDelimiter())) {
                    values[i] = value.substring(1, value.length() - 1);
                }
            }
        }
        return values;
    }

    private void detectEscapeCharacter(List<String> lines) {
        int quoteCount = 0;
        int doubleQuoteCount = 0;
        char escapeCharacter = csvMetadata.getEscapeCharacter();
        for (String line : lines) {
            String[] values = getValues(line, false);
            for (String value : values) {
                if (value.startsWith("'") && value.endsWith("'")) quoteCount++;
                if (value.startsWith("\"") && value.endsWith("\"")) doubleQuoteCount++;
                if (quoteCount > 20 && doubleQuoteCount < quoteCount / 10) {
                    escapeCharacter = '\'';
                    break;
                }
                if (doubleQuoteCount > 20 && quoteCount < doubleQuoteCount / 10) {
                    escapeCharacter = '\"';
                    break;
                }
            }
            if (escapeCharacter == 0 && quoteCount > doubleQuoteCount) {
                escapeCharacter = '\'';
            } else if (escapeCharacter == 0) {
                escapeCharacter = '\"';
            }
        }
        csvMetadata.setEscapeCharacter(escapeCharacter);
        csvMetadata.setFieldDelimiter(escapeCharacter);
    }

    public Workbook read(File csvFile) throws IOException {
        detectFormat(csvFile);

        Workbook csvWorkbook = new CSVWorkbook(this);
        Sheet csvSheet = csvWorkbook.createSheet(csvFile.getName());

        Reader csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), getCharset()));
        ResultSet rs = csvMetadata.read(csvReader, getHeaders());
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int rowIndex = 0; // First row contains the headers
            while (rs.next()) {
                Row dataRow = csvSheet.createRow(rowIndex);
                for (int i = 0; i < meta.getColumnCount(); i++) {
                    Cell dataCell = dataRow.createCell(i);
                    String cellValue = rs.getString(i + 1);
                    if (cellValue == null) continue;
                    try {
                        double cellNumericValue = Double.parseDouble(cellValue);
                        dataCell.setCellValue(cellNumericValue);
                    } catch (NumberFormatException ex) {
                        dataCell.setCellValue(cellValue);
                    }
                }
                rowIndex++;
            }
            rs.close();
        } catch (SQLException ex) {
            throw new IOException("Failed to read CSV", ex);
        }
        csvSheet.setDefaultColumnWidth(-1);
        return csvWorkbook;
    }

    public void write(OutputStream output, Workbook workbook) throws IOException {
        SimpleResultSet rs = new SimpleResultSet();
        // TODO use the first row
        for (String header : headers) {
            rs.addColumn(header, Types.VARCHAR, 2000, 0);
        }
        Sheet firstSheet = workbook.getSheetAt(0);
        for (int i = 1; i <= firstSheet.getLastRowNum(); i++) {
            Row row = firstSheet.getRow(i);
            String[] rowValues = new String[headers.length];
            for (int j = 0; j < headers.length; j++) {
                Cell cell = row.getCell(j);
                rowValues[j] = CellUtils.getFormattedText(cell);
            }
            rs.addRow((Object[]) rowValues);
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(output, charset));
        try {
            csvMetadata.write(writer, rs);
        } catch (SQLException ex) {
            throw new IOException(ex);
        }
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(String[] headers) {
        this.headers = headers;
    }
}
