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
import ua.wyverno.localization.model.builders.GSheetTranslateRegistryKeyBuilder;
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

    /**
     * Парсить таблицю у мапу
     * @param spreadsheet електронна таблиця
     * @return {@link Map}<{@link GoogleSheet}, {@link List}<{@link TranslateRegistryKey}>> - ключ - аркуш, значення - лист з усіма ключами перекладу у цьому аркуші
     */
    public Map<GoogleSheet, List<TranslateRegistryKey>> parse(GoogleSpreadsheet spreadsheet) {
        Map<GoogleSheet, List<TranslateRegistryKey>> keysBySheetName = new HashMap<>();
        logger.info("Start parsing spreadsheet to Map<GoogleSheet, List<TranslateRegistryKey>>");

        spreadsheet.getSheets().forEach(sheet -> keysBySheetName.put(sheet, this.parseSheet(sheet)));

        logger.info("Parsing complete. Total sheets processed: {}, Total keys found: {}", spreadsheet.getSheets().size(), keysBySheetName.values().stream().mapToInt(List::size).sum());
        return keysBySheetName;
    }

    /**
     * Парсить аркуш у лист з ключами перекладу
     * @param sheet аркуш
     * @return лист з ключами перекладу з аркуша
     */
    private List<TranslateRegistryKey> parseSheet(GoogleSheet sheet) {
        String sheetName = sheet.getSheetName();
        logger.debug(line);
        logger.debug("Sheet-name: {}", sheetName);
        logger.debug(line);

        List<TranslateRegistryKey> keys = new ArrayList<>();
        List<GoogleRow> rows = sheet.getRows();
        GoogleSheetHeader sheetHeader = new GoogleSheetHeader(sheet);

        LocationA1KeyBuilder locationA1Builder = new LocationA1KeyBuilder();
        GSheetTranslateRegistryKeyBuilder keyBuilder = new GSheetTranslateRegistryKeyBuilder();
        // Чи є колонка FormattedText
        boolean hasFormattedColumn = hasFormattedColumn(sheet, sheetHeader);

        for (int i = 1; i < rows.size(); i++) {
            GoogleRow row = rows.get(i);
            if (row.isEmpty()) continue; // Якщо порожній рядок скіпаємо
            // Створюємо екстрактор рядка для вилучення значень з рядка таблиці
            RowDataExtractor rowExtractor = new RowDataExtractor(sheetHeader, row);
            // Логуємо рядок
            this.logRowDetails(rowExtractor);

            if (this.isValidKeyTranslate(rowExtractor.getKey(), rowExtractor.getContainerId())) { // Якщо ключ та контейнер не порожній, означає ми попали на початок нового ключа
                this.saveKeyTranslate(keyBuilder, locationA1Builder, row.getIndex() - 1, row.getColumnCount() - 1, keys);
                // Оновлюємо інформацію про поточний ключ перекладу
                locationA1Builder = this.initializeNewLocationKey(sheetName, row.getIndex());
                keyBuilder = this.initializeNewKey(rowExtractor, hasFormattedColumn);

                if (hasFormattedColumn) { // Якщо існує колонка з Formatted-Text тоді як вихідний рядок буде, ігровий текст з гри, з тегами, а не текст з Original-Text
                    keyBuilder.appendOriginalText(rowExtractor.getGameText()); // Додаємо ігровий текст як оригінальний текст
                    if (rowExtractor.getFormattedText() != null && !rowExtractor.getFormattedText().isEmpty()) { // Якщо у колонці Formatted-Text існує та не порожній
                        keyBuilder.appendTranslateText(rowExtractor.getFormattedText()); // Додаємо як переклад
                    }
                }
            }

            if (!hasFormattedColumn) { // Якщо у цьому аркуші немає колонки - Formatted-Text
                keyBuilder.appendOriginalText(rowExtractor.getOriginalText()); // Беремо текст з Original-Text без тегів
                this.appendTranslationForKey(keyBuilder, rowExtractor);
            }
        }

        this.saveKeyTranslate(keyBuilder, locationA1Builder, sheet.getLastRowIndexWithContent(), sheet.getColumnCount()-1, keys); // Зберігаємо останній ключ

        logger.debug("Finished parsing sheet: {}, found {} keys.", sheetName, keys.size());
        return keys;
    }

    /**
     * Створює новий ключ перекладу
     * @param extractor екстрактор рядка
     * @param hasFormattedColumn чи має аркуш колонку Formatted-Text
     * @return новий білдер Ключа перекладу
     */
    private GSheetTranslateRegistryKeyBuilder initializeNewKey(RowDataExtractor extractor, boolean hasFormattedColumn) {
        GSheetTranslateRegistryKeyBuilder keyBuilder = new GSheetTranslateRegistryKeyBuilder() // Створюємо нового білдера для ключа перекладу
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

    /**
     * Якщо є у рядку переклад, то додаємо переклад.
     * Залежно від рядка, якщо у рядку є переклад у колонці Edit-Text, то додаємо з цієї колонки переклад та ставимо, що цей ключ має переклад та він затверджений,
     * якщо у рядку є переклад у Translate-Text, то додаємо з цієї колонки переклад та ставимо лише, що цей ключ має переклад.
     * @param keyBuilder білдер ключа перекладу
     * @param extractor екстрактор рядка
     */
    private void appendTranslationForKey(GSheetTranslateRegistryKeyBuilder keyBuilder, RowDataExtractor extractor) {
        if (Objects.nonNull(extractor.getEditText()) && !extractor.getEditText().isEmpty()) {
            keyBuilder.appendTranslateText(extractor.getEditText())
                      .setIsApprove(true);
        } else if (Objects.nonNull(extractor.getTranslateText()) && !extractor.getTranslateText().isEmpty()) {
            keyBuilder.appendTranslateText(extractor.getTranslateText());
        }
    }
    /**
     * Зберігаємо ключ перекладу
     * @param keyBuilder білдер ключа перекладу
     * @param locationA1Builder розташування в А1 нотації ключа перекладу в таблиці
     * @param endRowIndex індекс кінцевого рядка де знаходиться ключ перекладу
     * @param endColumnIndex індекс кінцевої колонки де знаходиться
     * @param keys лист з ключами перекладу
     */
    private void saveKeyTranslate(GSheetTranslateRegistryKeyBuilder keyBuilder, LocationA1KeyBuilder locationA1Builder, int endRowIndex, int endColumnIndex, List<TranslateRegistryKey> keys) {
        if (this.isValidKeyTranslate(keyBuilder.getKey(), keyBuilder.getContainerId())) { // Якщо KeyBuilder має ключ та контейнер айді зберігаємо це як ключ перекладу
            keyBuilder.sheetLocationA1(locationA1Builder // Встановлюємо локацію у таблиці
                    .endRowIndex(endRowIndex) // Встановлюємо індекс попереднього рядка як останній рядок де знаходиться ключ перекладу
                    .endColumnIndex(endColumnIndex) // Встановлюємо останню колонку
                    .build());

            keys.add(keyBuilder.build()); // Додаємо до списку ключ перекладу
        }
    }

    private boolean hasFormattedColumn(GoogleSheet sheet, GoogleSheetHeader sheetHeader) {
        return sheetHeader.getValueIfExists(sheet.getRow(0), "Formatted-Text") != null;
    }

    /**
     * Перевіряє чи ключ валідний ключ перекладу
     * @param key ключ перекладу
     * @param containerId контейнер айді ключа перекладу
     * @return true - ключ валідний, false - ключ не валідний
     */
    private boolean isValidKeyTranslate(String key, String containerId) {
        return Objects.nonNull(key) && !key.isEmpty() &&
                Objects.nonNull(containerId) && !containerId.isEmpty();
    }


    /**
     * Логуємо рядок у таблиці
     * @param extractor екстрактор рядка
     */
    private void logRowDetails(RowDataExtractor extractor) {
        if (logger.isTraceEnabled()) {
            logger.trace("Container-ID: {}\tKey: {}\tActor: {}\tGame-Text: {}\tOriginal Text: {}\tTranslate-Text: {}\tEdit-Text: {}\tContext: {}\tTiming: {}\tVoice: {}\tDub: {}\tFormatted-Text: {}",
                    truncateString(extractor.getContainerId(), 5), truncateString(extractor.getKey(), 10), truncateString(extractor.getActor(), 10),
                    truncateString(extractor.getGameText(), 10), truncateString(extractor.getOriginalText(), 15), truncateString(extractor.getTranslateText(), 10), truncateString(extractor.getEditText(), 10),
                    truncateString(extractor.getContext(), 10), truncateString(extractor.getTiming(), 5),
                    truncateString(extractor.getVoice(), 5), truncateString(extractor.getDub(), 5), truncateString(extractor.getFormattedText(), 15));
        }
    }

    /**
     * Обрізає рядок за певною довжиною рядка
     * @param string рядок
     * @param maxLength максимальна довжина рядка
     * @return обрізаний рядок
     */
    private String truncateString(String string, int maxLength) {
        return string != null && string.length() > maxLength
                ? string.substring(0, maxLength)+"..."
                : string;
    }
}
