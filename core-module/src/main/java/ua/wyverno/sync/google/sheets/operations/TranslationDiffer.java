package ua.wyverno.sync.google.sheets.operations;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.model.CrowdinTranslation;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.google.sheets.util.A1RangeNotation;
import ua.wyverno.google.sheets.util.GSpreadsheetUtil;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;
import ua.wyverno.google.sheets.util.SheetA1NotationUtil;
import ua.wyverno.localization.config.LocalizationNameColumns;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.localization.parsers.GSheetTranslateKeyParser;
import ua.wyverno.sync.google.sheets.exceptions.NoMatchCountValuesException;
import ua.wyverno.sync.google.sheets.operations.results.TranslationDiffResult;
import ua.wyverno.utils.json.JSONCreator;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TranslationDiffer {
    private final static Logger logger = LoggerFactory.getLogger(TranslationDiffer.class);

    private final GSheetTranslateKeyParser keyParser;
    private final LocalizationNameColumns localizationNameColumns;
    private final JSONCreator jsonCreator;

    @Autowired
    public TranslationDiffer(GSheetTranslateKeyParser keyParser, LocalizationNameColumns localizationNameColumns, JSONCreator jsonCreator) {
        this.keyParser = keyParser;
        this.localizationNameColumns = localizationNameColumns;
        this.jsonCreator = jsonCreator;
    }

    public TranslationDiffResult diffTranslations(List<CrowdinTranslation> crowdinTranslations, GoogleSpreadsheet spreadsheet) {
        logger.trace("Converting Spreadsheet to map KeyById");
        List<GSheetTranslateKey> sheetTranslations = this.keyParser.parseSpreadsheet(spreadsheet).values().stream()
                .flatMap(List::stream)
                .toList();
        logger.trace("Sheets to sheet headers by name map.");
        Map<String, GoogleSheetHeader> sheetHeaderByName = GSpreadsheetUtil.getSheetHeaderBySheetName(spreadsheet);
        logger.trace("Converting List with Crowdin Translations to Map crowdinTranslationByIdentifier");
        Map<String, CrowdinTranslation> crowdinTranslationByIdentifier = crowdinTranslations.stream()
                .collect(Collectors.toMap(trans -> trans.getSourceString().getIdentifier(), Function.identity()));
        int countTranslationKeyChange = 0;
        List<ValueRange> valueRanges = new ArrayList<>();

        // Перебираємо всі ключі перекладу
        for (GSheetTranslateKey sheetTranslation : sheetTranslations) {
            this.requiredContainsSheetHeader(sheetHeaderByName, sheetTranslation);
            GoogleSheetHeader sheetHeader = sheetHeaderByName.get(sheetTranslation.locationA1().sheetName());
            // Перевіряємо чи існує переклад для певного ключа перекладу
            if (!crowdinTranslationByIdentifier.containsKey(sheetTranslation.identifier().toString())) {
                logger.debug("Key Identifier: {} - no has translation in Crowdin.", sheetTranslation.identifier());
                if (!sheetTranslation.translation().isEmpty()) { // Якщо у таблиці є переклад видаляємо весь переклад для ключа
                    countTranslationKeyChange++;
                    logger.debug("Key Identifier: {} - No has translation in Crowdin but has in Sheets Location: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
                    valueRanges.addAll(this.removeTranslationData(sheetTranslation, sheetHeader));
                }
                continue;
            }

            CrowdinTranslation crowdinTranslation = crowdinTranslationByIdentifier.get(sheetTranslation.identifier().toString());

            // Ділимо рядок перекладу, на лінії, одна лінія еквівалентна одній комірці
            List<String> sheetTransLines = this.strTranslationToLines(sheetTranslation.translation());
            List<String> crowdinTransLines = this.strTranslationToLines(crowdinTranslation.getTranslation());

            if (!this.matchTranslations(crowdinTransLines, sheetTransLines, sheetTranslation)) {
                countTranslationKeyChange++;
                valueRanges.addAll(this.insertTranslationData(crowdinTransLines, sheetHeader, sheetTranslation));
            } else if (crowdinTranslation.isApprove() && !sheetTranslation.isApprove() && !this.isFormatted(sheetHeader)) {
                logger.debug("Translation in Crowdin approved: {}, A1: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
                ValueRange approveValueRange = this.createColumnValueRange(crowdinTransLines,
                        sheetHeader.getColumnIndex(this.localizationNameColumns.getEditText()),
                        sheetTranslation.locationA1());
                valueRanges.add(approveValueRange);
            } else if (!crowdinTranslation.isApprove() && sheetTranslation.isApprove() && !this.isFormatted(sheetHeader)) {
                logger.debug("Translation in Crowdin is no longer approved: {}, A1: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
                valueRanges.add(this.removeApproveTranslation(sheetTranslation, sheetHeader));
            }
        }

        logger.debug("Differ completed: {} translation keys must be change.", countTranslationKeyChange);
        return new TranslationDiffResult(countTranslationKeyChange, valueRanges);
    }

    private void requiredContainsSheetHeader(Map<String, GoogleSheetHeader> sheetHeaderByName, GSheetTranslateKey sheetTranslation) {
        if (!sheetHeaderByName.containsKey(sheetTranslation.locationA1().sheetName()))
            throw new IllegalStateException("No exists SheetHeader for " + sheetTranslation.locationA1().sheetName());
    }

    /**
     * Створює рейнджи для видалення перекладу, для звичайного перекладу, та за потреби створює для затвердженого перекладу
     * @param sheetTranslation ключ перекладу у гугл таблиці
     * @param sheetHeader заголовок аркуша де міститься ключ перекладу
     * @return Лист з рейнджами для видалення перекладу
     */
    private List<ValueRange> removeTranslationData(GSheetTranslateKey sheetTranslation, GoogleSheetHeader sheetHeader) {
        if (this.isFormatted(sheetHeader)) {
            logger.trace("Creating value range for remove formatted translation: {}, A1: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
            return Collections.singletonList(this.removeFormattedTranslation(sheetTranslation, sheetHeader));
        }

        List<ValueRange> valueRanges = new ArrayList<>();
        logger.trace("Creating value range for remove translation: {}, A1: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
        valueRanges.add(this.removeTranslation(sheetTranslation, sheetHeader));
        if (sheetTranslation.isApprove()) {
            logger.trace("Creating value range for remove approve: {}, A1: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
            valueRanges.add(this.removeApproveTranslation(sheetTranslation, sheetHeader));
        }

        return valueRanges;
    }

    private boolean isFormatted(GoogleSheetHeader header) {
        return header.containsColumn(this.localizationNameColumns.getFormattedText());
    }

    /**
     * Встановлює нове значення для ключа перекладу в аркуші таблиці
     * @param crowdinTransLines рядки з кроудіна які потрібно вставити у гугл таблицю
     * @param header заголовок аркуша, де знаходиться ключ перекладу
     * @param sheetTranslation ключ перекладу в гугл таблиці
     * @return Повертає лист з {@link ValueRange} з новими значеннями для аркуша
     */
    private List<ValueRange> insertTranslationData(List<String> crowdinTransLines, GoogleSheetHeader header, GSheetTranslateKey sheetTranslation) {
        A1RangeNotation a1 = sheetTranslation.locationA1();
        if (this.isFormatted(header)) {
            logger.trace("Insert new formatted translation: {}", sheetTranslation.identifier());
            return Collections.singletonList(this
                    .createCellValueRange(crowdinTransLines.get(0), header.getColumnIndex(this.localizationNameColumns.getFormattedText()), a1));

        }
        List<ValueRange> valueRanges = new ArrayList<>();
        logger.trace("Insert new translation: {}", sheetTranslation.identifier());
        ValueRange transValue = this.createColumnValueRange(crowdinTransLines,
                        header.getColumnIndex(this.localizationNameColumns.getTranslateText()), a1);
        valueRanges.add(transValue);
        if (sheetTranslation.isApprove()) {
            logger.trace("Insert new approve translation: {}", sheetTranslation.identifier());
            ValueRange approveValue = this.createColumnValueRange(crowdinTransLines,
                    header.getColumnIndex(this.localizationNameColumns.getEditText()), a1);
            valueRanges.add(approveValue);
        }
        return valueRanges;
    }

    /**
     * Створює рейндж для видалення у гугл таблиці - ключа перекладу звичайний переклад
     * @param sheetTranslation ключ перекладу
     * @param header заголовок аркуша де знаходиться ключ
     * @return Рейндж з порожніми значеннями для очищення звичайного перекладу для ключа перекладу
     */
    private ValueRange removeTranslation(GSheetTranslateKey sheetTranslation, GoogleSheetHeader header) {
        A1RangeNotation a1 = sheetTranslation.locationA1();
        List<String> values = IntStream.range(0, a1.endColumnIndex() - a1.startRowIndex() + 1)
                .mapToObj(i -> "") // Створюємо лист з порожніми рядками
                .toList();

        return this.createColumnValueRange(values, header.getColumnIndex(this.localizationNameColumns.getTranslateText()), a1);
    }
    /**
     * Створює рейндж для видалення у гугл таблиці - ключа перекладу затвердженого перекладу
     * @param sheetTranslation ключ перекладу
     * @param header заголовок аркуша де знаходиться ключ
     * @return Рейндж з порожніми значеннями для очищення затвердженого перекладу для ключа перекладу
     */
    private ValueRange removeApproveTranslation(GSheetTranslateKey sheetTranslation, GoogleSheetHeader header) {
        A1RangeNotation a1 = sheetTranslation.locationA1();
        List<String> values = IntStream.range(0, a1.endColumnIndex() - a1.startRowIndex() + 1)
                .mapToObj(i -> "")
                .toList();

        return this.createColumnValueRange(values, header.getColumnIndex(this.localizationNameColumns.getEditText()), a1);
    }

    private ValueRange removeFormattedTranslation(GSheetTranslateKey sheetTranslation, GoogleSheetHeader header) {
        int formattedIndex = header.getColumnIndex(this.localizationNameColumns.getFormattedText());
        return this.createCellValueRange("", formattedIndex, sheetTranslation.locationA1());
    }
    /**
     * Створює рейндж зі значеннями для колонки, де кожен наступний елемент значень має бути у наступному рядку.
     * @param values значення які потрібно вписати в рейндж
     * @param columnIndex колонка в які мають бути ці значення
     * @param a1RangeTranslateKey А1 Нотація Рейнджа ключа перекладу
     * @return об'єкт {@link ValueRange} відображає рейндж зі значеннями
     */
    private ValueRange createColumnValueRange(List<String> values, int columnIndex, A1RangeNotation a1RangeTranslateKey) {
        int rowsRange = a1RangeTranslateKey.endColumnIndex() - a1RangeTranslateKey.startRowIndex() + 1;
        if (rowsRange != values.size())
            throw new NoMatchCountValuesException("A1RangeTranslation the number of rows does not match the size of the values that were passed to the method. " +
                    "RowsRange: " + rowsRange + " Values count: " + values.size());

        String range = SheetA1NotationUtil.rangeToA1Notation(
                a1RangeTranslateKey.sheetName(),
                a1RangeTranslateKey.startRowIndex(),
                columnIndex,
                a1RangeTranslateKey.endRowIndex(),
                columnIndex);

        return new ValueRange()
                .setRange(range)
                .setValues(values.stream()
                        .map(value -> Collections.singletonList((Object) value))
                        .toList());
    }

    /**
     * Створює рейндж зі значеннями для першої комірки для рейнджа ключа перекладу.
     * @param value значення яке потрібно створити
     * @param columnIndex колонка в які має бути це значення
     * @param a1RangeTranslateKey А1 Нотація Рейнджа ключа перекладу
     * @return об'єкт {@link ValueRange} відображає рейндж зі значеннями
     */
    private ValueRange createCellValueRange(String value, int columnIndex, A1RangeNotation a1RangeTranslateKey) {
        String range = SheetA1NotationUtil.rangeToA1Notation(
                a1RangeTranslateKey.sheetName(),
                a1RangeTranslateKey.startRowIndex(),
                columnIndex,
                a1RangeTranslateKey.startRowIndex(),
                columnIndex);

        return new ValueRange()
                .setRange(range)
                .setValues(Collections.singletonList(Collections.singletonList(value)));
    }

    /**
     * Перетворює один рядок на лінії, де одна лінія відповідно це один рядок у Гугл таблиці
     * @param str рядок перекладу
     * @return Лист з лініями рядками для комірок
     */
    private List<String> strTranslationToLines(String str) {
        return Arrays.stream(str.split("\\\\n"))
                .map(s -> s.replaceAll("\\n", ""))
                .toList();
    }

    /**
     * Перевіряє чи переклади однакові, чи ні.
     * @param crowdinTransLines лінії перекладу у Кроудіні
     * @param sheetTransLines лінії перекладу у Таблиці
     * @param sheetTranslation переклад з Таблиці
     * @return true якщо переклад однаковий, false якщо переклад має
     */
    private boolean matchTranslations(List<String> crowdinTransLines, List<String> sheetTransLines, GSheetTranslateKey sheetTranslation) {
        if (sheetTransLines.size() != crowdinTransLines.size()) {
            String errorMessage = String.format("""
                        The number of rows translated in Crowdin does not match the number of rows in Google Sheet.
                        Crowdin translation count lines: %d
                        Google Sheet translation count lines: %d
                        Location translation in sheet: %s
                        Crowdin Identifier: %s""",
                    crowdinTransLines.size(),
                    sheetTransLines.size(),
                    sheetTranslation.locationA1(),
                    sheetTranslation.identifier().toString());
            throw new NoMatchCountValuesException(errorMessage);
        }

        boolean isMatching = IntStream.range(0, sheetTransLines.size())
                .allMatch(i -> sheetTransLines.get(i).equals(crowdinTransLines.get(i)));

        if (!isMatching) {
            logger.debug("Translation: {} not matches Crowdin With Google Sheet key!", sheetTranslation.identifier().toString());
            logger.trace("""
                            No matches Translation Identifier -> {}
                            Crowdin:
                            {}
                            Google Sheets:
                            {}""",
                    sheetTranslation.identifier().toString(),
                    String.join("\n", crowdinTransLines),
                    String.join("\n", sheetTransLines));
        }

        return isMatching;
    }
}
