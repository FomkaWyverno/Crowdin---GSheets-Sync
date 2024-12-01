package ua.wyverno.google.sheets.model;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class GoogleSpreadsheet {
    private final String spreadsheetId;
    private final String title;
    private final List<GoogleSheet> sheets;

    public GoogleSpreadsheet(String spreadsheetId, String title, List<GoogleSheet> sheets) {
        this.spreadsheetId = spreadsheetId;
        this.title = title;
        this.sheets = Collections.unmodifiableList(sheets);
    }

    public String getSpreadsheetId() {
        return spreadsheetId;
    }

    public String getTitle() {
        return title;
    }

    public List<GoogleSheet> getSheets() {
        return sheets;
    }

    public GoogleSheet getSheet(int index) {
        return this.sheets.get(index);
    }

    public Optional<GoogleSheet> getSheetByName(String sheetName) {
        return this.sheets.stream()
                .filter(sheet -> sheet.getSheetName().equals(sheetName))
                .findFirst();
    }

    @Override
    public String toString() {
        return this.title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoogleSpreadsheet that = (GoogleSpreadsheet) o;

        if (!spreadsheetId.equals(that.spreadsheetId)) return false;
        if (!title.equals(that.title)) return false;
        return sheets.equals(that.sheets);
    }

    @Override
    public int hashCode() {
        int result = spreadsheetId.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + sheets.hashCode();
        return result;
    }
}
