package ua.wyverno.localization.model.builders;

import ua.wyverno.google.sheets.util.SheetA1NotationUtil;

public class LocationA1KeyBuilder {
    private String sheetName;
    private int startRowIndex;
    private int endRowIndex;
    private int endColumnIndex;

    public LocationA1KeyBuilder sheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public LocationA1KeyBuilder startRowIndex(int startRowIndex) {
        this.startRowIndex = startRowIndex;
        return this;
    }

    public LocationA1KeyBuilder endRowIndex(int endRowIndex) {
        this.endRowIndex = endRowIndex;
        return this;
    }

    public LocationA1KeyBuilder endColumnIndex(int endColumnIndex) {
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
