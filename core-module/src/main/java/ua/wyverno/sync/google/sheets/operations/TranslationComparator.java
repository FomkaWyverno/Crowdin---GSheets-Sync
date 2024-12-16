package ua.wyverno.sync.google.sheets.operations;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.model.CrowdinTranslation;
import ua.wyverno.localization.header.TranslationSheetHeader;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.sync.google.sheets.exceptions.NoMatchCountValuesException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class TranslationComparator {
    private final static Logger logger = LoggerFactory.getLogger(TranslationComparator.class);

    private final TranslationValueRangeProcessor valueRangeProcessor;

    @Autowired
    public TranslationComparator(TranslationValueRangeProcessor valueRangeProcessor) {
        this.valueRangeProcessor = valueRangeProcessor;
    }

    /**
     * Перетворює один рядок на лінії, де одна лінія відповідно це один рядок у Гугл таблиці.
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
        List<String> sheetOriginalLines = this.strTranslationToLines(sheetTranslation.originalText());
        if (sheetOriginalLines.size() != crowdinTransLines.size()) {
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

        boolean isMatching = sheetTransLines.equals(crowdinTransLines);

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

    /**
     * Порівнює два перекладу з Кроудіна та Гугл Таблиці
     * @param crowdinTranslation Переклад з Кроудіна
     * @param sheetTranslation Переклад з Гугл Таблиці
     * @param sheetHeader Заголовок таблиці
     * @return Повертає Лист з {@link ValueRange}, список може бути порожнім, якщо переклад однаковий, якщо ні, то поверне список, того що потрібно змінити.
     */
    public List<ValueRange> compareTranslation(CrowdinTranslation crowdinTranslation,
                                               GSheetTranslateKey sheetTranslation,
                                               TranslationSheetHeader sheetHeader) {
        // Перевіряємо чи існує переклад у Кроудіні
        if (crowdinTranslation == null) { // Якщо переклад не існує, то у Таблиці також нічого не має бути, тому видаляємо весь переклад
            logger.debug("Key Identifier: {} - no has translation in Crowdin.", sheetTranslation.identifier());
            if (!sheetTranslation.translation().isEmpty()) { // Якщо у таблиці є переклад видаляємо весь переклад для ключа
                logger.debug("Key Identifier: {} - No has translation in Crowdin but has in Sheets Location: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
                return this.valueRangeProcessor.removeTranslationData(sheetTranslation, sheetHeader);
            }
            return Collections.emptyList();
        }

        List<ValueRange> valueRanges = new ArrayList<>();

        // Ділимо рядок перекладу, на лінії, одна лінія еквівалентна одній комірці
        List<String> sheetTransLines = this.strTranslationToLines(sheetTranslation.translation());
        List<String> crowdinTransLines = this.strTranslationToLines(crowdinTranslation.getTranslation());

        if (!this.matchTranslations(crowdinTransLines, sheetTransLines, sheetTranslation)) {
            valueRanges.addAll(this.valueRangeProcessor.insertTranslationData(crowdinTransLines, sheetHeader, sheetTranslation, crowdinTranslation));
        } else if (crowdinTranslation.isApprove() && !sheetTranslation.isApprove() && !sheetHeader.hasFormattedColumn()) {
            logger.debug("Translation in Crowdin approved: {}, A1: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
            ValueRange approveValueRange = this.valueRangeProcessor.createColumnValueRange(crowdinTransLines,
                    sheetHeader.getEditColumnIndex(),
                    sheetTranslation.locationA1());
            valueRanges.add(approveValueRange);
        } else if (!crowdinTranslation.isApprove() && sheetTranslation.isApprove() && !sheetHeader.hasFormattedColumn()) {
            logger.debug("Translation in Crowdin is no longer approved: {}, A1: {}", sheetTranslation.identifier(), sheetTranslation.locationA1());
            valueRanges.add(this.valueRangeProcessor.removeApproveTranslation(sheetTranslation, sheetHeader));
        }

        return valueRanges;
    }
}
