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

    public String build() {
        return SheetA1NotationUtil.rangeToA1Notation(this.sheetName, this.startRowIndex, 0, this.endRowIndex, this.endColumnIndex);
    }

    @Override
    public String toString() {
        return this.build();
    }
}
