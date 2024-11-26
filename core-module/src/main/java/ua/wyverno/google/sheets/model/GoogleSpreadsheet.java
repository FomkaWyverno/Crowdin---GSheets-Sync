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
}
