package org.joeffice.tools;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

// java -classpath "../Apache-POI/release/modules/ext/*" org\joeffice\tools\PoiXlsxBackground.java
public class PoiXlsxBackground {

    public static void checkBackground(File xlsxFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(xlsxFile));
        XSSFSheet sheet = workbook.getSheetAt(0);
        Cell firstCell = sheet.getRow(0).getCell(0);
        CellStyle style = firstCell.getCellStyle();
        IndexedColorMap colorMap = workbook.getStylesSource().getIndexedColors();

        // The first cell has a blue background
        XSSFColor fgColor = XSSFColor.toXSSFColor(style.getFillForegroundColorColor());
        System.out.println("Foreground fill color " + fgColor.getARGBHex());
        int fgColorIndex = style.getFillForegroundColor();
        if (fgColorIndex > 0) {
            byte[] rgb = colorMap.getRGB(fgColorIndex);
            System.out.println("Foreground fill color from index " + Arrays.toString(rgb));
        }

        XSSFColor bgColor = XSSFColor.toXSSFColor(style.getFillBackgroundColorColor());
        System.out.println("Background fill color " + bgColor.getARGBHex());
        int bgColorIndex = style.getFillBackgroundColor();
        if (bgColorIndex > 0) {
            byte[] rgb = colorMap.getRGB(bgColorIndex);
            System.out.println("Foreground fill color from index " + Arrays.toString(rgb));
        }
    }



    public static void main(String... args) throws IOException {
        File xlsxFile = new File("blue-background.xlsx");
        checkBackground(xlsxFile);
    }
}
