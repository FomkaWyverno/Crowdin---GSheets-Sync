package ua.wyverno.google.sheets.model;

import java.util.Collections;
import java.util.List;

public class GoogleSheet {
    private final Integer sheetId;
    private final String sheetName;
    private final List<GoogleRow> rows;

    public GoogleSheet(Integer sheetId, String sheetName, List<GoogleRow> rows) {
        this.sheetId = sheetId;
        this.sheetName = sheetName;
        this.rows = Collections.unmodifiableList(rows);
    }

    public Integer getSheetId() {
        return sheetId;
    }

    public String getSheetName() {
        return sheetName;
    }

    public List<GoogleRow> getRows() {
        return rows;
    }

    public GoogleRow getRow(int index) {
        return this.rows.get(index);
    }

    @Override
    public String toString() {
        return this.sheetName;
    }
}
