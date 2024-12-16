package ua.wyverno.sync.google.sheets.operations;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.model.CrowdinTranslation;
import ua.wyverno.google.sheets.util.A1RangeNotation;
import ua.wyverno.google.sheets.util.SheetA1NotationUtil;
import ua.wyverno.localization.header.TranslationSheetHeader;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.sync.google.sheets.exceptions.NoMatchCountValuesException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Component
public class TranslationValueRangeProcessor {
    private final static Logger logger = LoggerFactory.getLogger(TranslationValueRangeProcessor.class);

    /**
     * Створює рейнджи для видалення перекладу, для звичайного перекладу, та за потреби створює для затвердженого перекладу
     * @param sheetTranslation ключ перекладу у гугл таблиці
     * @param sheetHeader заголовок аркуша де міститься ключ перекладу
     * @return Лист з рейнджами для видалення перекладу
     */
    protected List<ValueRange> removeTranslationData(GSheetTranslateKey sheetTranslation, TranslationSheetHeader sheetHeader) {
        if (sheetHeader.hasFormattedColumn()) {
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

    /**
     * Встановлює нове значення для ключа перекладу в аркуші таблиці
     * @param crowdinTransLines рядки з кроудіна які потрібно вставити у гугл таблицю
     * @param header заголовок аркуша, де знаходиться ключ перекладу
     * @param sheetTranslation ключ перекладу в гугл таблиці
     * @return Повертає лист з {@link ValueRange} з новими значеннями для аркуша
     */
    protected List<ValueRange> insertTranslationData(List<String> crowdinTransLines, TranslationSheetHeader header, GSheetTranslateKey sheetTranslation, CrowdinTranslation crowdinTranslation) {
        A1RangeNotation a1 = sheetTranslation.locationA1();
        if (header.hasFormattedColumn()) {
            logger.trace("Insert new formatted translation: {}", sheetTranslation.identifier());
            return Collections.singletonList(this
                    .createCellValueRange(crowdinTransLines.get(0), header.getFormattedColumnIndex(), a1));
        }
        List<ValueRange> valueRanges = new ArrayList<>();
        logger.trace("Insert new translation: {}", sheetTranslation.identifier());
        ValueRange transValue = this.createColumnValueRange(crowdinTransLines,
                header.getTranslateColumnIndex(), a1);
        valueRanges.add(transValue);
        if (crowdinTranslation.isApprove()) {
            logger.trace("Insert new approve translation: {}", sheetTranslation.identifier());
            ValueRange approveValue = this.createColumnValueRange(crowdinTransLines,
                    header.getEditColumnIndex(), a1);
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
    protected ValueRange removeTranslation(GSheetTranslateKey sheetTranslation, TranslationSheetHeader header) {
        A1RangeNotation a1 = sheetTranslation.locationA1();
        List<String> values = IntStream.range(0, a1.endRowIndex() - a1.startRowIndex() + 1)
                .mapToObj(i -> "") // Створюємо лист з порожніми рядками
                .toList();

        return this.createColumnValueRange(values, header.getTranslateColumnIndex(), a1);
    }
    /**
     * Створює рейндж для видалення у гугл таблиці - ключа перекладу затвердженого перекладу
     * @param sheetTranslation ключ перекладу
     * @param header заголовок аркуша де знаходиться ключ
     * @return Рейндж з порожніми значеннями для очищення затвердженого перекладу для ключа перекладу
     */
    protected ValueRange removeApproveTranslation(GSheetTranslateKey sheetTranslation, TranslationSheetHeader header) {
        A1RangeNotation a1 = sheetTranslation.locationA1();
        List<String> values = IntStream.range(0, a1.endRowIndex() - a1.startRowIndex() + 1)
                .mapToObj(i -> "")
                .toList();

        return this.createColumnValueRange(values, header.getEditColumnIndex(), a1);
    }

    protected ValueRange removeFormattedTranslation(GSheetTranslateKey sheetTranslation, TranslationSheetHeader header) {
        int formattedIndex = header.getFormattedColumnIndex();
        return this.createCellValueRange("", formattedIndex, sheetTranslation.locationA1());
    }
    /**
     * Створює рейндж зі значеннями для колонки, де кожен наступний елемент значень має бути у наступному рядку.
     * @param values значення які потрібно вписати в рейндж
     * @param columnIndex колонка в які мають бути ці значення
     * @param a1RangeTranslateKey А1 Нотація Рейнджа ключа перекладу
     * @return об'єкт {@link ValueRange} відображає рейндж зі значеннями
     */
    protected ValueRange createColumnValueRange(List<String> values, int columnIndex, A1RangeNotation a1RangeTranslateKey) {
        int rowsRange = a1RangeTranslateKey.endRowIndex()    - a1RangeTranslateKey.startRowIndex() + 1;
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
    protected ValueRange createCellValueRange(String value, int columnIndex, A1RangeNotation a1RangeTranslateKey) {
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
}
