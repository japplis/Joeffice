package org.joeffice.desktop.ui;

import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface for finding text within the document.
 * 
 * @author Anthony Goubard - Japplis
 */
public interface Findable {
    
    public enum FindOption {MATCH_CASE, WHOLE_WORD, REG_EXP};

    boolean find(String search, EnumSet<FindOption> options);
    
    boolean findNext(boolean forward);
    
    default int find(String text, String search, int from, EnumSet<FindOption> options) {
        if (options.contains(FindOption.MATCH_CASE) && options.size() == 1) {
            return text.indexOf(search, from);
        }
        String searchRegExp = search;
        if (!options.contains(FindOption.REG_EXP)) {
            searchRegExp = search.replaceAll("[\\$\\^\\.\\?\\[\\{]", "\\$1");
        }
        if (options.contains(FindOption.WHOLE_WORD)) {
            searchRegExp = "\\b" + searchRegExp + "\\b";
        }
        if (!options.contains(FindOption.MATCH_CASE) && !search.contains("(?i)")) {
            searchRegExp = "(?i)" + searchRegExp;
        }
        String searchText = text.substring(from);
        Pattern searchPattern = Pattern.compile(searchRegExp);
        Matcher searchMatcher = searchPattern.matcher(searchText);
        if (searchMatcher.find()) {
            return from + searchMatcher.start();
        }
        return -1;
    }
}
