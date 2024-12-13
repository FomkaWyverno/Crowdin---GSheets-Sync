package ua.wyverno.google.sheets.util;

public record A1RangeNotation(
        String A1Notation,
        String sheetName,
        int startColumnIndex,
        int endColumnIndex,
        Integer startRowIndex,
        Integer endRowIndex) {
    @Override
    public String toString() {
        return this.A1Notation;
    }
}
