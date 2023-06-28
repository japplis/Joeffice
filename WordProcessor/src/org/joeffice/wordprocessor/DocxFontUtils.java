/**
 * Copyright 2023 - Japplis
 */
package org.joeffice.wordprocessor;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.math.BigInteger;
import java.util.List;

import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

/**
 * Get font information based on XWPFRun or paragraph style.
 *
 * @author Anthony Goubard - Japplis
 */
public class DocxFontUtils {

    private static String defaultFontName;

    public static String getFontFamily(XWPFRun run) {
        if (run.getFontFamily() != null) {
            return run.getFontFamily();
        }
        CTRPr paragraphStyle = getFontStyle(run);
        if (paragraphStyle == null) return getDefaultFontFamily();
        List<CTFonts> fonts = paragraphStyle.getRFontsList();
        if (fonts.isEmpty()) return getDefaultFontFamily();
        return fonts.get(0).getAscii();
    }

    public static String getDefaultFontFamily() {
        if (defaultFontName == null) {
            List<String> fontFamilies = List.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
            if (fontFamilies.contains("Calibri")) defaultFontName = "Calibri";
            else if (fontFamilies.contains("Liberation Serif")) defaultFontName = "Liberation Serif";
            else defaultFontName = new Font(Font.SERIF, Font.PLAIN, 12).getFamily();
        }
        return defaultFontName;
    }

    public static int getFontSize(XWPFRun run) {
        int size = run.getFontSize();
        if (size != -1) return size;
        CTRPr paragraphStyle = getFontStyle(run);
        int defaultSize = run.getDocument().getStyles().getDefaultRunStyle().getFontSize();
        if (System.getProperty("os.name").contains("Windows")) {
            defaultSize = (int) (defaultSize * 96 / 72.0f);
        }
        if (paragraphStyle == null) return defaultSize;
        List<CTHpsMeasure> sizes = paragraphStyle.getSzList();
        if (sizes.isEmpty()) return defaultSize;
        if (!(sizes.get(0).getVal() instanceof BigInteger)) return defaultSize;
        size = ((BigInteger) sizes.get(0).getVal()).intValue() / 2;
        if (System.getProperty("os.name").contains("Windows")) {
            size = (int) (size * 96 / 72.0f);
        }
        return size;
    }

    public static boolean isBold(XWPFRun run) {
        if (run.isBold()) return true;
        CTRPr paragraphStyle = getFontStyle(run);
        if (paragraphStyle == null) return false;
        List<CTOnOff> boldList = paragraphStyle.getBList();
        return !boldList.isEmpty();
    }

    public static boolean isItalic(XWPFRun run) {
        if (run.isItalic()) return true;
        CTRPr paragraphStyle = getFontStyle(run);
        if (paragraphStyle == null) return false;
        List<CTOnOff> italicList = paragraphStyle.getIList();
        return !italicList.isEmpty();
    }

    public static boolean isStrikeThrough(XWPFRun run) {
        if (run.isStrikeThrough()) return true;
        CTRPr paragraphStyle = getFontStyle(run);
        if (paragraphStyle == null) return false;
        List<CTOnOff> strikeThroughList = paragraphStyle.getStrikeList();
        return !strikeThroughList.isEmpty();
    }

    public static boolean isUnderlined(XWPFRun run) {
        if (run.getUnderline() != UnderlinePatterns.NONE) return true;
        CTRPr paragraphStyle = getFontStyle(run);
        if (paragraphStyle == null) return false;
        List<CTUnderline> underlineList = paragraphStyle.getUList();
        return !underlineList.isEmpty();
    }

    private static CTRPr getFontStyle(XWPFRun run) {
        String styleID = null;
        try {
            styleID = run.getStyle();
        } catch (Exception ex) { // May throw ArrayOutOfBoundException
            // Ignore
        }
        if (styleID == null) {
            if (run.getParent() == null || !(run.getParent() instanceof XWPFParagraph)) {
                return null;
            }
            try {
                styleID = ((XWPFParagraph) run.getParent()).getStyleID();
            } catch (Exception ex) {
            }
        }
        if (styleID == null) return null;
        try {
            XWPFStyle style = run.getDocument().getStyles().getStyle(styleID);
            if (style != null) return style.getCTStyle().getRPr();
        } catch (Exception ex) {
        }
        return null;
    }
}
