package ua.wyverno.sync.crowdin.files.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class SheetFileUtils {
    private final static Logger logger = LoggerFactory.getLogger(SheetFileUtils.class);
    private final static String FILE_EXTENSION = ".csv";
    private final static Pattern TITLE_PATTERN = Pattern.compile("(.+)\\(");

    /**
     * Створює на основі SheetId та розширення імя для файлу
     * @param sheet аркуш
     * @return імя файлу для аркуша
     */
    public String getFileNameForSheet(GoogleSheet sheet) {
        return sheet.getSheetId() + FILE_EXTENSION;
    }

    /**
     * Бере виключно заголовок аркуша без категорії<br/>
     * Приклад: Аркуш (Категорія)<br/>
     * Результат буде "Аркуш"
     * @param sheet Аркуш
     * @return Повертає назву аркуша без категорії
     */
    public String getTitleForSheet(GoogleSheet sheet) {
        String sheetName = sheet.getSheetName();
        if (!sheetName.contains("(")) {
            return sheetName;
        }
        Matcher matcher = TITLE_PATTERN.matcher(sheetName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        logger.warn("Sheet Name: {}, can't find title without category.", sheetName);
        return sheetName;
    }
}
