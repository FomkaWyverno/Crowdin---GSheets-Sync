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
    private final Sheets service;
    @Autowired
    private GoogleSpreadsheetParser spreadsheetParser;

    @Autowired
    public GoogleSheetsService(GoogleSheetsAuth sheetsAuth) throws GeneralSecurityException, IOException {
        this.service = sheetsAuth.getService();
    }

    public Sheets getApi() {
        return service;
    }

    /**
     * Надсилає запит до Google Sheets API - spreadsheets.get()
     * @param spreadsheetId айді електронної таблиці
     * @return {@link Spreadsheet} містить метаінформацію про електронну таблицю, які аркуша містить, який у них розмір, але не містить їх вмісту.
     * @throws IOException у разі помилки при запуску запиту до API
     */
    public Spreadsheet getSpreadsheetMetadata(String spreadsheetId) throws IOException {
        return this.service.spreadsheets().get(spreadsheetId).execute();
    }

    /**
     * Бере всю електронну таблицю з вмістом. Виключно всі аркуша з типом GRID
     * @param spreadsheetId айді електронної таблиці
     * @return {@link GoogleSpreadsheet} - вміст електронної таблиці
     * @throws IOException помилка при виконанні запиту до АПІ
     */
    public GoogleSpreadsheet getSpreadsheetData(String spreadsheetId) throws IOException {
        Spreadsheet spreadsheet = this.getSpreadsheetMetadata(spreadsheetId);
        return this.getSpreadsheetData(spreadsheet,
                spreadsheet.getSheets().stream()
                        .filter(sheet -> sheet.getProperties().getSheetType().equals("GRID"))
                        .toList());
    }

    /**
     * Повертає електронну таблицю з вмістом,<br/>
     * Отримує за допомогою spreadsheetId {@link Spreadsheet} та викликає {@link GoogleSheetsService#getSpreadsheetData(Spreadsheet, List)}
     * @param spreadsheetId айді таблиці
     * @param sheets лист з метаінформацією аркушів у вигляді {@link Sheet} з яких потрібно витягти весь їх вміст та помістити в результат.<br/>
     *               Рекомендація викликати спочатку {@link GoogleSheetsService#getSpreadsheetMetadata(String)} отримати всю інформацію про таблицю<br/>
     *               після цього відфільтрувати лише потрібні аркуші {@link Sheet},<br/>
     *               та зібрати з цього лист, щоб отримати вміст цих всіх аркушів у єдиному об'єкті {@link GoogleSpreadsheet}
     * @return {@link GoogleSpreadsheet} - вміст електронної таблиці
     * @throws IOException помилка при виконанні запиту до АПІ
     */
    public GoogleSpreadsheet getSpreadsheetData(String spreadsheetId, List<Sheet> sheets) throws IOException {
        return this.getSpreadsheetData(this.getSpreadsheetMetadata(spreadsheetId), sheets);
    }

    /**
     * Повертає електронну таблицю з вмістом,<br/>
     * Перетворює метадані на рейнджи, щоб отримати весь їх вміст, та викликає {@link GoogleSheetsService#getSpreadsheetDataByRanges(String, List)}
     * @param spreadsheet метаінформація про таблицю
     * @param sheets лист з метаінформацією аркушів у вигляді {@link Sheet} з яких потрібно витягти весь їх вміст та помістити в результат.<br/>
     *               Рекомендація викликати спочатку {@link GoogleSheetsService#getSpreadsheetMetadata(String)} отримати всю інформацію про таблицю<br/>
     *               після цього відфільтрувати лише потрібні аркуші {@link Sheet},<br/>
     *               та зібрати з цього лист, щоб отримати вміст цих всіх аркушів у єдиному об'єкті {@link GoogleSpreadsheet}
     * @return {@link GoogleSpreadsheet} - вміст електронної таблиці
     * @throws IOException помилка при виконанні запиту до АПІ
     */
    public GoogleSpreadsheet getSpreadsheetData(Spreadsheet spreadsheet, List<Sheet> sheets) throws IOException {
        List<String> ranges = generateRanges(sheets);
        logger.trace("Created ranges for API Spreadsheet.BatchGet");
        return this.getSpreadsheetDataByRanges(spreadsheet, ranges);
    }

    /**
     * Повертає електронну таблицю з вмістом,<br/>
     * Отримує за допомогою spreadsheetId {@link Spreadsheet} та викликає {@link GoogleSheetsService#getSpreadsheetDataByRanges(Spreadsheet, List)}
     * @param spreadsheetId айді таблиці
     * @param ranges рейнджи які потрібно взяти з таблиці
     * @return {@link GoogleSpreadsheet} - вміст електронної таблиці
     * @throws IOException помилка при виконанні запиту до АПІ
     */
    public GoogleSpreadsheet getSpreadsheetDataByRanges(String spreadsheetId, List<String> ranges) throws IOException {
        return this.getSpreadsheetDataByRanges(this.getSpreadsheetMetadata(spreadsheetId), ranges);
    }

    /**
     * Повертає електронну таблицю з вмістом, який був вказаний в листу з рейнджами для таблиці
     * @param spreadsheet метадані таблиці
     * @param ranges рейнджи які потрібно взяти з таблиці
     * @return {@link GoogleSpreadsheet} - вміст електронної таблиці
     * @throws IOException помилка при виконанні запиту до АПІ
     */

    public GoogleSpreadsheet getSpreadsheetDataByRanges(Spreadsheet spreadsheet, List<String> ranges) throws IOException {
        String spreadsheetId = spreadsheet.getSpreadsheetId();
        BatchGetValuesResponse response = this.service.spreadsheets().values().batchGet(spreadsheetId).setRanges(ranges).execute();

        // Групуємо значення які прийшли за назвами аркушів
        Map<String, List<ValueRange>> sheetNameValueMap = response.getValueRanges().stream()
                .collect(Collectors.groupingBy(value -> this.getSheetNameFromRange(value.getRange())));
        logger.trace("Response ValueRange grouping by sheet name");

        Map<Sheet, ValueRange> sheetDataMap = new HashMap<>();
        spreadsheet.getSheets().forEach(sheet -> { // Групуємо дані додаючи як ключ інформацію про аркуш, з першого запиту, і як значення вміст аркуша, який ми отримали з другого запиту
            String sheetName = sheet.getProperties().getTitle();
            List<ValueRange> values = sheetNameValueMap.get(sheetName);
            if (values == null) {return;} // Якщо з цієї таблиці немає значень, означає що вона не була зазначена, тому просто пропускаємо цей цикл.
            if (values.size() > 1) { // Якщо для одного аркуша більше, або менше значень, щось пішло не так.
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

    /**
     * Створює рейнджи для аркушів
     * @param sheets аркуші для яких потрібно створити рейндж
     * @return {@link List}<{@link String}> лист з рейнджами для аркушів
     */
    private List<String> generateRanges(List<Sheet> sheets) {
        return sheets.stream()
                .map(sheet -> { // Створюємо рейндж для отримання даних з всіх аркушів
                    SheetProperties sheetProperties = sheet.getProperties();
                    GridProperties gridProperties = sheetProperties.getGridProperties();
                    return String.format("'%s'!A1:%s", sheetProperties.getTitle(),
                            SheetA1NotationUtil.toA1Notation(gridProperties.getRowCount() - 1, gridProperties.getColumnCount() - 1));
                }).toList();
    }
}