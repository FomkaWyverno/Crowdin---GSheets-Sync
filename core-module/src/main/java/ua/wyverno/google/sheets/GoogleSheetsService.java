package ua.wyverno.google.sheets;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.google.sheets.parsers.GoogleSpreadsheetParser;
import ua.wyverno.google.sheets.util.SheetA1NotationUtil;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GoogleSheetsService {
    private static final Logger logger = LoggerFactory.getLogger(GoogleSheetsService.class);
    private static final String APPLICATION_NAME = "Google Sheets API Java Crowdin-Sync";
    private final Sheets service;
    @Autowired
    private GoogleSpreadsheetParser spreadsheetParser;

    @Autowired
    public GoogleSheetsService(GoogleSheetsAuth sheetsAuth) throws GeneralSecurityException, IOException {
        Credential credential = sheetsAuth.authorize();
        this.service = new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(), GsonFactory.getDefaultInstance(), credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Sheets getApi() {
        return service;
    }

    public GoogleSpreadsheet getSpreadsheetData(String spreadsheetId) throws IOException {
        logger.trace("Method: getSpreadsheetData() with param spreadsheetId = {}", spreadsheetId);
        Spreadsheet spreadsheet = this.service.spreadsheets() // Отримуємо інформацію з про електронну таблицю
                .get(spreadsheetId)
                .execute();
        logger.trace("Get spreadsheet from API");

        List<Sheet> sheets = spreadsheet.getSheets().stream() // Беремо лише аркуші з типом "GRID"
                .filter(sheet -> sheet.getProperties().getSheetType().equals("GRID"))
                .toList();
        logger.trace("Filtering sheets by SheetType == Grid");

        List<String> ranges = sheets.stream()
                .map(sheet -> { // Створюємо рейндж для отримання даних з всіх аркушів
                    SheetProperties sheetProperties = sheet.getProperties();
                    GridProperties gridProperties = sheetProperties.getGridProperties();
                    return String.format("'%s'!A1:%s", sheetProperties.getTitle(),
                            SheetA1NotationUtil.toA1Notation(gridProperties.getRowCount()-1, gridProperties.getColumnCount()-1));
                }).toList();
        logger.trace("Created ranges for API Spreadsheet.BatchGet");
        // Отримуємо батч запитом всі дані з аркушів
        BatchGetValuesResponse response = this.service.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges).execute();

        // Групуємо значення які прийшли за назвами аркушів
        Map<String, List<ValueRange>> sheetNameValueMap = response.getValueRanges().stream()
                .collect(Collectors.groupingBy(value -> this.getSheetNameFromRange(value.getRange())));
        logger.trace("Response ValueRange grouping by sheet name");

        Map<Sheet, ValueRange> sheetDataMap = new HashMap<>();
        sheets.forEach(sheet -> { // Групуємо дані додаючи як ключ інформацію про аркуш, з першого запиту, і як значення вміст аркуша, який ми отримали з другого запиту
            String sheetName = sheet.getProperties().getTitle();
            List<ValueRange> values = sheetNameValueMap.get(sheetName);
            if (values.size() != 1) { // Якщо для одного аркуша більше, або менше значень, щось пішло не так.
                logger.error("Sheet Name: {}, not has 1 single sheet! ValueRanges: {}", sheetName, values);
                return;
            }
            sheetDataMap.put(sheet, values.get(0)); // Кладемо як ключ Аркуш, як значення, вміст таблиці
        });
        logger.trace("ValueRange grouping by Sheet object");

        return this.spreadsheetParser.parse(spreadsheetId, spreadsheet.getProperties().getTitle(), sheetDataMap);
    }

    private String getSheetNameFromRange(String range) {
        int separateIndex = range.lastIndexOf('!');
        if (separateIndex == -1) {
            String errorMessage = "Invalid range format: " + range;
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        String sheetName = range.substring(0, separateIndex);

        if (sheetName.startsWith("'") && sheetName.endsWith("'")) {
            sheetName = sheetName.substring(1, sheetName.length()-1);
        }

        return sheetName;
    }
}