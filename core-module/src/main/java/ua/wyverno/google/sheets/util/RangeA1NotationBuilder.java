package ua.wyverno.google.sheets.util;

public class RangeA1NotationBuilder {
    private String sheetName;
    private int startRowIndex;
    private int endRowIndex;
    private int endColumnIndex;

    public RangeA1NotationBuilder sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public RangeA1NotationBuilder startRowIndex(int startRowIndex) {
        this.startRowIndex = startRowIndex;
        return this;
    }

    public RangeA1NotationBuilder endRowIndex(int endRowIndex) {
        this.endRowIndex = endRowIndex;
        return this;
    }

    public RangeA1NotationBuilder endColumnIndex(int endColumnIndex) {
        this.endColumnIndex = endColumnIndex;
        return this;
    }

    public A1RangeNotation build() {
        String a1Notation = SheetA1NotationUtil.rangeToA1Notation(this.sheetName, this.startRowIndex, 0, this.endRowIndex, this.endColumnIndex);
        return new A1RangeNotation(a1Notation, this.sheetName, 0, this.endColumnIndex, this.startRowIndex, this.endRowIndex);
    }

    @Override
    public String toString() {
        return this.build().A1Notation();
    }
}
