package ua.wyverno.localization.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleRow;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.localization.builder.parsers.KeyContextBuilder;
import ua.wyverno.localization.header.TranslationSheetHeader;
import ua.wyverno.localization.header.TranslationSheetHeaderFactory;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.localization.model.key.TranslateKey;
import ua.wyverno.google.sheets.util.RangeA1NotationBuilder;
import ua.wyverno.localization.builder.key.GSheetTranslateKeyBuilder;
import ua.wyverno.localization.config.KeyContextConfig;
import ua.wyverno.localization.model.row.RowData;

import java.util.*;

@Component
public class GSheetTranslateKeyParser {
    private final static Logger logger = LoggerFactory.getLogger(GSheetTranslateKeyParser.class);
    private final static String line = "---------------------------------------------";

    @Autowired
    private KeyContextConfig keyContextConfig;
    @Autowired
    private RowDataExtractor rowDataExtractor;
    @Autowired
    private TranslationSheetHeaderFactory headerFactory;

    /**
     * Парсить таблицю у мапу
     * @param spreadsheet електронна таблиця
     * @return {@link Map}<{@link GoogleSheet}, {@link List}<{@link TranslateKey}>> - ключ - аркуш, значення - лист з усіма ключами перекладу у цьому аркуші
     */
    public Map<GoogleSheet, List<GSheetTranslateKey>> parseSpreadsheet(GoogleSpreadsheet spreadsheet) {
        Map<GoogleSheet, List<GSheetTranslateKey>> keysBySheetName = new HashMap<>();
        logger.info("Start parsing spreadsheet to Map<GoogleSheet, List<GSheetTranslateKey>>");

        spreadsheet.getSheets().forEach(sheet -> keysBySheetName.put(sheet, this.parseSheet(sheet)));

        logger.info("Parsing complete. Total sheets processed: {}, Total keys found: {}", spreadsheet.getSheets().size(), keysBySheetName.values().stream().mapToInt(List::size).sum());
        return keysBySheetName;
    }

    /**
     * Парсить аркуш у лист з ключами перекладу
     * @param sheet аркуш
     * @return лист з ключами перекладу з аркуша
     */
    public List<GSheetTranslateKey> parseSheet(GoogleSheet sheet) {
        String sheetName = sheet.getSheetName();
        logger.debug(line);
        logger.debug("Parsing to List<GSheetTranslateKey> - Sheet-name: {}", sheetName);
        logger.debug(line);

        List<GSheetTranslateKey> keys = new ArrayList<>();
        List<GoogleRow> rows = sheet.getRows();
        TranslationSheetHeader sheetHeader = this.headerFactory.create(sheet);

        RangeA1NotationBuilder locationA1Builder = new RangeA1NotationBuilder();
        GSheetTranslateKeyBuilder keyBuilder = new GSheetTranslateKeyBuilder();
        // ПРИМІТКА ПРО РЕЙНДЖИ
        // Останній рядок у якому є вміст ключа.
        // Якщо у таблиці ключ після якого є порожні рядки
        // Приклад:
        // 1. English Text1
        // 2. English Text2
        // 3. English Text3
        // 4. (порожній рядок)
        // 5. (порожній рядок)
        // Фактично 3-й рядок це є кінцевим рядком ключа, тому цей рейндж має закінчуватись на ньому (3), а не на п'ятому рядку (5)
        // !Примітка: Якщо після порожнього рядка буде значення для поточного ключа це призведе до виключення у
        // ua.wyverno.sync.google.sheets.operations.TranslationValueRangeProcessor у методі createColumnValueRange(...)
        // Так як він буде дивитись скільки у рейнджі рядків, й скільки всього рядків у ключі перекладу, оскільки буде порожній рядок між рядками
        // Ключ для перекладу буде містити на стільки менше рядків, скільки й самих порожніх рядків.
        // Тому потрібно, щоб обов'язково після порожніх рядків був саме новий ключ!

        // Чи є колонка FormattedText
        boolean hasFormattedColumn = sheetHeader.hasFormattedColumn();

        for (int i = 1; i < rows.size(); i++) {
            GoogleRow row = rows.get(i);
            if (row.isEmpty()) continue; // Якщо порожній рядок скіпаємо
            // Створюємо екстрактор рядка для вилучення значень з рядка таблиці
            RowData rowExtractor = this.rowDataExtractor.extract(sheetHeader, row);
            // Логуємо рядок
            this.logRowDetails(rowExtractor);

            if (this.isValidKeyTranslate(rowExtractor.getKey(), rowExtractor.getContainerId())) { // Якщо ключ та контейнер не порожній, означає ми попали на початок нового ключа
                this.saveKeyTranslate(keyBuilder, locationA1Builder, keys);
                // Оновлюємо інформацію про поточний ключ перекладу
                locationA1Builder = this.initializeNewLocationKey(sheetName, row.getIndex(), sheet.getColumnCount() - 1);
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
            locationA1Builder.endRowIndex(i);
        }

        this.saveKeyTranslate(keyBuilder, locationA1Builder, keys); // Зберігаємо останній ключ

        logger.debug("Finished parsing sheet: {}, found {} keys.", sheetName, keys.size());
        return keys;
    }

    /**
     * Створює новий ключ перекладу
     * @param extractor екстрактор рядка
     * @param hasFormattedColumn чи має аркуш колонку Formatted-Text
     * @return новий білдер Ключа перекладу
     */
    private GSheetTranslateKeyBuilder initializeNewKey(RowData extractor, boolean hasFormattedColumn) {
        GSheetTranslateKeyBuilder keyBuilder = new GSheetTranslateKeyBuilder() // Створюємо нового білдера для ключа перекладу
                .containerId(extractor.getContainerId()) // Встановлюємо контейнер айді
                .key(extractor.getKey()); // Встановлюємо ключ перекладу
        KeyContextBuilder contextBuilder = new KeyContextBuilder(this.keyContextConfig) // Створюємо контекст для ключа перекладу
                .actor(extractor.getActor())
                .context(extractor.getContext())
                .timing(extractor.getTiming())
                .voice(extractor.getVoice())
                .dub(extractor.getDub())
                .hasFormattedColumn(hasFormattedColumn);
        if (hasFormattedColumn) contextBuilder.originalText(extractor.getGameText());
        return keyBuilder.context(contextBuilder.build());
    }
    private RangeA1NotationBuilder initializeNewLocationKey(String sheetName, int rowIndex, int endColumnIndex) {
        return new RangeA1NotationBuilder() // Створюємо новий білдер локації ключа перекладу
                .sheetName(sheetName) // Встановлюємо назву аркуша
                .startRowIndex(rowIndex) // Встановлюємо індекс початка рядка ключа перекладу
                .endColumnIndex(endColumnIndex); // Встановлюємо індекс останньої колонки
    }

    /**
     * Якщо є у рядку переклад, то додаємо переклад.
     * Залежно від рядка, якщо у рядку є переклад у колонці Edit-Text, то додаємо з цієї колонки переклад та ставимо, що цей ключ має переклад та він затверджений,
     * якщо у рядку є переклад у Translate-Text, то додаємо з цієї колонки переклад та ставимо лише, що цей ключ має переклад.
     * @param keyBuilder білдер ключа перекладу
     * @param extractor екстрактор рядка
     */
    private void appendTranslationForKey(GSheetTranslateKeyBuilder keyBuilder, RowData extractor) {
        if (Objects.nonNull(extractor.getEditText()) && !extractor.getEditText().isEmpty()) {
            keyBuilder.appendTranslateText(extractor.getEditText())
                      .setIsApprove(true);
        } else if (Objects.nonNull(extractor.getTranslateText()) && !extractor.getTranslateText().isEmpty()) {
            keyBuilder.appendTranslateText(extractor.getTranslateText());
        } else if (!keyBuilder.getTranslateText().toString().isEmpty()) {
            keyBuilder.appendTranslateText("");
        }
    }
    /**
     * Зберігаємо ключ перекладу
     * @param keyBuilder білдер ключа перекладу
     * @param locationA1Builder розташування в А1 нотації ключа перекладу в таблиці
     * @param keys лист з ключами перекладу
     */
    private void saveKeyTranslate(GSheetTranslateKeyBuilder keyBuilder, RangeA1NotationBuilder locationA1Builder, List<GSheetTranslateKey> keys) {
        if (this.isValidKeyTranslate(keyBuilder.getKey(), keyBuilder.getContainerId())) { // Якщо KeyBuilder має ключ та контейнер айді зберігаємо це як ключ перекладу
            keyBuilder.sheetLocationA1(locationA1Builder // Встановлюємо локацію у таблиці
                    .build());

            keys.add(keyBuilder.build()); // Додаємо до списку ключ перекладу
        }
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
    private void logRowDetails(RowData extractor) {
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
