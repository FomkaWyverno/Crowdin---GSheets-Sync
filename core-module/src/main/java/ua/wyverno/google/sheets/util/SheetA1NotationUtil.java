package ua.wyverno.google.sheets.util;

public class SheetA1NotationUtil {
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
}
