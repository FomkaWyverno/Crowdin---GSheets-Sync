package ua.wyverno.google.sheets.util;

import ua.wyverno.google.sheets.util.execptions.BadA1RangeNotationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SheetA1NotationUtil {

    private final static Pattern A1_RANGE_NOTATION_PATTERN = Pattern.compile("^'(.+)'!([A-Z]+)(\\d+)+:([A-Z]+)(\\d+)$");

    public static String columnToLetter(int indexColumn) {
        StringBuilder columnName = new StringBuilder();

        while (indexColumn >= 0) {
            columnName.insert(0, (char) ('A' + (indexColumn % 26)));
            indexColumn = indexColumn / 26 - 1;
        }

        return columnName.toString();
    }

    public static int rowToNumber(int rowIndex) {
        return rowIndex + 1;
    }

    public static String toA1Notation(int rowIndex, int columnIndex) {
        return columnToLetter(columnIndex)+rowToNumber(rowIndex);
    }

    public static String rangeToA1Notation(String sheetName, int startRowIndex, int startColumnIndex, int endRowIndex, int endColumnIndex) {
        return "'" + sheetName + "'!" +
                toA1Notation(startRowIndex, startColumnIndex) +
                ":" +
                toA1Notation(endRowIndex, endColumnIndex);
    }

    public static int letterToColumnIndex(String letter) {
        char[] chars = letter.toCharArray();
        int columnIndex = 0;

        for (Character c : chars) {
            columnIndex = columnIndex * 26 + (c - 'A' + 1);
        }

        return columnIndex - 1;
    }

    public static Integer rowNumberToRowIndex(String rowNumber) {
        if (rowNumber.isEmpty()) return null;
        return Integer.parseInt(rowNumber) - 1;
    }

    public static A1RangeNotation fromA1RangeNotation(String a1RangeNotation) {
        Matcher matcher = A1_RANGE_NOTATION_PATTERN.matcher(a1RangeNotation);
        if (matcher.find()) {
            return new A1RangeNotation(a1RangeNotation,
                    matcher.group(1), // Назва аркуша
                    letterToColumnIndex(matcher.group(2)), // Індекс колонки початка рейнджа
                    letterToColumnIndex(matcher.group(4)), // Індекс колонки закінчення рейнжа
                    rowNumberToRowIndex(matcher.group(3)), // Індекс рядка початка рейнджа
                    rowNumberToRowIndex(matcher.group(5))); // Індекс рядка закінчення рейнджа
        } else {
            throw new BadA1RangeNotationException("A1RangeNotation is not correct: \""+ a1RangeNotation +"\"");
        }
    }
}
