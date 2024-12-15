package ua.wyverno.google.sheets.util;

import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;

import java.util.Map;
import java.util.stream.Collectors;

public class GSpreadsheetUtil {

    /**
     * Формує мапу де ключ це назва аркуша, а значення це його утилітарний класс для заголовка
     * @param spreadsheet електронна таблиця зі вмістом
     * @return Мапа де ключ це назва аркуша, а значення Заголовок аркуша
     */
    public static Map<String, GoogleSheetHeader> getSheetHeaderBySheetName(GoogleSpreadsheet spreadsheet) {
        return spreadsheet.getSheets().stream()
                .collect(Collectors.toMap(
                        GoogleSheet::getSheetName,
                        GoogleSheetHeader::new));
    }
}
