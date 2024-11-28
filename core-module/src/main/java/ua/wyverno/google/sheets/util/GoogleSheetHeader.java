package ua.wyverno.google.sheets.util;

import ua.wyverno.google.sheets.model.GoogleCell;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.util.execptions.GoogleSheetHeaderIncorrectException;

import java.util.HashMap;
import java.util.Map;

public class GoogleSheetHeader {
    private final String sheetName;
    private final Map<String, Integer> headerMap;

    public GoogleSheetHeader(GoogleSheet sheet) {
        GoogleRow headerRow = sheet.getRow(0);
        this.headerMap = new HashMap<>();
        this.sheetName = sheet.getSheetName();

        for (GoogleCell cell : headerRow.getCells()) {
            if (!cell.isEmpty()) {
                String cellValue = cell.getValue();
                if (this.headerMap.containsKey(cellValue)) {
                    String errorMessage = String.format("%s - sheet has duplicate header column!", this.sheetName);
                    throw new GoogleSheetHeaderIncorrectException(errorMessage);
                }
                this.headerMap.put(cellValue, cell.getIndex());
            }
        }
    }

    public int getColumnIndex(String headerName) {
        if (!this.containsColumn(headerName)) throw new GoogleSheetHeaderIncorrectException(headerName + " - column header not has in sheet: " + this.sheetName);
        return this.headerMap.get(headerName);
    }
    public boolean containsColumn(String headerName) {
        return this.headerMap.containsKey(headerName);
    }

    public String getValue(GoogleRow row, String headerName) {
        return row.getCell(this.getColumnIndex(headerName)).getValue();
    }

    public int getValueAsInteger(GoogleRow row, String headerName) {
        return Integer.parseInt(row.getCell(this.getColumnIndex(headerName)).getValue());
    }

    public long getValueAsLong(GoogleRow row, String headerName) {
        return Long.parseLong(row.getCell(this.getColumnIndex(headerName)).getValue());
    }

    public String getValueIfExists(GoogleRow row, String headerName) {
        if (!this.containsColumn(headerName)) return null;
        return this.getValue(row, headerName);
    }

    public Integer getValueAsIntegerIfExists(GoogleRow row, String headerName) {
        if (!this.containsColumn(headerName)) return null;
        return this.getValueAsInteger(row, headerName);
    }

    public Long getValueAsLongIfExists(GoogleRow row, String headerName) {
        if (!this.containsColumn(headerName)) return null;
        return this.getValueAsLong(row, headerName);
    }
}
