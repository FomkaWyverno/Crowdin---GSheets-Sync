package ua.wyverno.localization.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;
import ua.wyverno.localization.model.TranslateRegistryKey;
import ua.wyverno.localization.model.builders.LocationA1KeyBuilder;
import ua.wyverno.localization.model.builders.TranslateRegistryKeyBuilder;
import ua.wyverno.localization.parsers.util.KeyContextBuilder;
import ua.wyverno.localization.parsers.util.KeyContextConfig;
import ua.wyverno.localization.parsers.util.RowDataExtractor;

import java.util.*;

@Component
public class TranslateRegistryKeyParser {
    private final static Logger logger = LoggerFactory.getLogger(TranslateRegistryKey.class);
    private final static String line = "---------------------------------------------";

    @Autowired
    private KeyContextConfig keyContextConfig;

    public Map<String, List<TranslateRegistryKey>> parse(GoogleSpreadsheet spreadsheet) {
        Map<String, List<TranslateRegistryKey>> keysBySheetName = new TreeMap<>();
        logger.info("Start parsing spreadsheet to List<TranslateRegistryKey>");

        spreadsheet.getSheets().forEach(sheet -> keysBySheetName.put(sheet.getSheetName(), this.parseSheet(sheet)));

        logger.info("Parsing complete. Total sheets processed: {}, Total keys found: {}", spreadsheet.getSheets().size(), keysBySheetName.values().stream().mapToInt(List::size).sum());
        return keysBySheetName;
    }

    private List<TranslateRegistryKey> parseSheet(GoogleSheet sheet) {
        String sheetName = sheet.getSheetName();
        logger.debug(line);
        logger.debug("Sheet-name: {}", sheetName);
        logger.debug(line);

        List<TranslateRegistryKey> keys = new ArrayList<>();
        List<GoogleRow> rows = sheet.getRows();
        GoogleSheetHeader sheetHeader = new GoogleSheetHeader(sheet);

        LocationA1KeyBuilder locationA1Builder = new LocationA1KeyBuilder();
        TranslateRegistryKeyBuilder keyBuilder = new TranslateRegistryKeyBuilder();
        // Чи є колонка FormattedText
        boolean hasFormattedColumn = hasFormattedColumn(sheet, sheetHeader);

        for (int i = 1; i < rows.size(); i++) { // TODO: 01.12.2024 Додати переклад до ключів перекладу з таблички
            GoogleRow row = rows.get(i);
            if (row.isEmpty()) continue; // Якщо порожній рядок скіпаємо
            // Створюємо екстрактор рядка для вилучення значень з рядка таблиці
            RowDataExtractor rowExtractor = new RowDataExtractor(sheetHeader, row);
            // Логуємо рядок
            this.logRowDetails(rowExtractor);

            if (this.hasKeyTranslate(rowExtractor.getKey(), rowExtractor.getContainerId())) { // Якщо ключ та контейнер не порожній, означає ми попали на початок нового ключа
                this.saveKeyTranslate(keyBuilder, locationA1Builder, row.getIndex(), row.getColumnCount() - 1, keys);
                // Оновлюємо інформацію про поточний ключ перекладу
                locationA1Builder = this.initializeNewLocationKey(sheetName, row.getIndex());
                keyBuilder = this.initializeNewKey(rowExtractor, hasFormattedColumn);

                if (hasFormattedColumn) { // Якщо існує колонка з Formatted-Text тоді як вихідний рядок буде, ігровий текст з гри, з тегами, а не текст з Original-Text
                    keyBuilder.appendOriginalText(rowExtractor.getGameText());
                }
            }

            if (!hasFormattedColumn) { // Якщо у цьому аркуші немає колонки - Formatted-Text
                keyBuilder.appendOriginalText(rowExtractor.getOriginalText()); // Якщо в аркуші немає колонки Formatted-Text, то беремо текст з Original-Text без тегів
            }
        }

        this.saveKeyTranslate(keyBuilder, locationA1Builder, sheet.getLastRowIndexWithContent(), sheet.getColumnCount()-1, keys); // Зберігаємо останній ключ

        logger.debug("Finished parsing sheet: {}, found {} keys.", sheetName, keys.size());
        return keys;
    }

    private TranslateRegistryKeyBuilder initializeNewKey(RowDataExtractor extractor, boolean hasFormattedColumn) {
        TranslateRegistryKeyBuilder keyBuilder = new TranslateRegistryKeyBuilder() // Створюємо нового білдера для ключа перекладу
                .containerId(extractor.getContainerId()) // Встановлюємо контейнер айді
                .key(extractor.getKey()); // Встановлюємо ключ перекладу

        return keyBuilder.context(new KeyContextBuilder(this.keyContextConfig) // Створюємо контекст для ключа перекладу
                .actor(extractor.getActor())
                .context(extractor.getContext())
                .timing(extractor.getTiming())
                .voice(extractor.getVoice())
                .dub(extractor.getDub())
                .hasFormattedColumn(hasFormattedColumn)
                .build());
    }
    private LocationA1KeyBuilder initializeNewLocationKey(String sheetName, int rowIndex) {
        return new LocationA1KeyBuilder() // Створюємо новий білдер локації ключа перекладу
                .sheetName(sheetName) // Встановлюємо назву аркуша
                .startRowIndex(rowIndex); // Встановлюємо індекс початка рядка ключа перекладу
    }

    private void saveKeyTranslate(TranslateRegistryKeyBuilder keyBuilder, LocationA1KeyBuilder locationA1Builder, int endRowIndex, int endColumnIndex, List<TranslateRegistryKey> keys) {
        if (this.hasKeyTranslate(keyBuilder.getKey(), keyBuilder.getContainerId())) { // Якщо KeyBuilder має ключ та контейнер айді зберігаємо це як ключ перекладу
            keyBuilder.locationA1(locationA1Builder // Встановлюємо локацію у таблиці
                    .endRowIndex(endRowIndex) // Встановлюємо індекс попереднього рядка як останній рядок де знаходиться ключ перекладу
                    .endColumnIndex(endColumnIndex) // Встановлюємо останню колонку
                    .build());

            keys.add(keyBuilder.build()); // Додаємо до списку ключ перекладу
        }
    }

    private boolean hasFormattedColumn(GoogleSheet sheet, GoogleSheetHeader sheetHeader) {
        return sheetHeader.getValueIfExists(sheet.getRow(0), "Formatted-Text") != null;
    }

    private boolean hasKeyTranslate(String key, String containerId) {
        return key != null && !key.isEmpty() &&
                containerId != null && !containerId.isEmpty();
    }



    private void logRowDetails(RowDataExtractor extractor) {
        if (logger.isTraceEnabled()) {
            logger.trace("Container-ID: {}\tKey: {}\tActor: {}\tGame-Text: {}\tOriginal Text: {}\tTranslate-Text: {}\tEdit-Text: {}\tContext: {}\tTiming: {}\tVoice: {}\tDub: {}\tFormatted-Text: {}",
                    truncateString(extractor.getContainerId(), 5), truncateString(extractor.getKey(), 10), truncateString(extractor.getActor(), 10),
                    truncateString(extractor.getGameText(), 10), truncateString(extractor.getOriginalText(), 15), truncateString(extractor.getTranslateText(), 10), truncateString(extractor.getEditText(), 10),
                    truncateString(extractor.getContext(), 10), truncateString(extractor.getTiming(), 5),
                    truncateString(extractor.getVoice(), 5), truncateString(extractor.getDub(), 5), truncateString(extractor.getFormattedText(), 15));
        }
    }

    private String truncateString(String string, int maxLength) {
        return string != null && string.length() > maxLength
                ? string.substring(0, maxLength)+"..."
                : string;
    }
}
