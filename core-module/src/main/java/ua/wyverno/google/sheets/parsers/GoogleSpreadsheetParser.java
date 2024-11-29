package ua.wyverno.google.sheets.parsers;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;

import java.util.List;
import java.util.Map;

@Component
public class GoogleSpreadsheetParser {
    private final static Logger logger = LoggerFactory.getLogger(GoogleSpreadsheetParser.class);
    @Autowired
    private GoogleSheetParser sheetParser;
    public GoogleSpreadsheet parse(String spreadsheetID, String title, Map<Sheet, ValueRange> sheetsDataMap) {
        logger.trace("Start parsing spreadsheet...");
        List<GoogleSheet> sheets = sheetsDataMap.entrySet().stream()
                .map(entry -> {
                    Sheet sheet = entry.getKey();
                    ValueRange value = entry.getValue();
                    Integer sheetID = sheet.getProperties().getSheetId();
                    String sheetName = sheet.getProperties().getTitle();
                    int maxRowCount = sheet.getProperties().getGridProperties().getRowCount();
                    int maxColumnCount = sheet.getProperties().getGridProperties().getColumnCount();
                    List<List<Object>> values = value.getValues();
                    return this.sheetParser.parse(sheetID, sheetName, values, maxRowCount, maxColumnCount);
                }).toList();
        return new GoogleSpreadsheet(spreadsheetID, title, sheets);
    }
}
