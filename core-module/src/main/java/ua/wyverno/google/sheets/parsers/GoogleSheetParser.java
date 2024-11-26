package ua.wyverno.google.sheets.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.model.GoogleSheet;

import java.util.ArrayList;
import java.util.List;

@Component
public class GoogleSheetParser {
    @Autowired
    private GoogleRowParser rowParser;
    public GoogleSheet parse(Integer sheetId, String sheetName, List<List<Object>> values) {
        List<GoogleRow> rows = new ArrayList<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            GoogleRow row = this.rowParser.parse(values.get(i), i);
            rows.add(row);
        }
        return new GoogleSheet(sheetId, sheetName, rows);
    }
}
