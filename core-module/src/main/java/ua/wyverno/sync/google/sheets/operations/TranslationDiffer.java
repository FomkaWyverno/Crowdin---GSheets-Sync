package ua.wyverno.sync.google.sheets.operations;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.model.CrowdinTranslation;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.localization.header.TranslationSheetHeader;
import ua.wyverno.localization.header.TranslationSheetHeaderFactory;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.localization.parsers.GSheetTranslateKeyParser;
import ua.wyverno.sync.google.sheets.exceptions.NoMatchCountValuesException;
import ua.wyverno.sync.google.sheets.operations.results.TranslationDiffResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TranslationDiffer {
    private final static Logger logger = LoggerFactory.getLogger(TranslationDiffer.class);

    private final GSheetTranslateKeyParser keyParser;
    private final TranslationSheetHeaderFactory headerFactory;
    private final TranslationComparator translationComparator;

    @Autowired
    public TranslationDiffer(GSheetTranslateKeyParser keyParser,
                             TranslationSheetHeaderFactory headerFactory,
                             TranslationComparator translationComparator) {
        this.keyParser = keyParser;
        this.headerFactory = headerFactory;
        this.translationComparator = translationComparator;
    }

    public TranslationDiffResult diffTranslations(List<CrowdinTranslation> crowdinTranslations, GoogleSpreadsheet spreadsheet) {
        logger.trace("Converting Spreadsheet to map KeyById");
        List<GSheetTranslateKey> sheetTranslations = this.keyParser.parseSpreadsheet(spreadsheet).values().stream()
                .flatMap(List::stream)
                .toList();
        logger.trace("Sheets to sheet headers by name map.");
        Map<String, TranslationSheetHeader> sheetHeaderByName = spreadsheet.getSheets().stream()
                .collect(Collectors.toMap(GoogleSheet::getSheetName, this.headerFactory::create));
        logger.trace("Converting List with Crowdin Translations to Map crowdinTranslationByIdentifier");
        Map<String, CrowdinTranslation> crowdinTranslationByIdentifier = crowdinTranslations.stream()
                .collect(Collectors.toMap(trans -> trans.getSourceString().getIdentifier(), Function.identity()));
        AtomicInteger countTranslationKeyChange = new AtomicInteger();

        Map<GSheetTranslateKey, CrowdinTranslation> badCountValuesTranslations = new HashMap<>();
        // Перебираємо всі ключі перекладу
        List<ValueRange> valueRanges = sheetTranslations.stream()
                .flatMap(sheetTranslation -> {
                    this.requiredContainsSheetHeader(sheetHeaderByName, sheetTranslation); // Перевіряємо чи існує заголовок для певного аркуша, якщо не існує викине виключення
                    TranslationSheetHeader sheetHeader = sheetHeaderByName.get(sheetTranslation.locationA1().sheetName()); // Беремо заголовок аркуша
                    CrowdinTranslation crowdinTranslation = crowdinTranslationByIdentifier.get(sheetTranslation.identifier().toString()); // Беремо переклад з Кроудіну по ідентифікатору
                    try { // Порівнюємо переклад Кроудіну з Перекладом у Гугл Таблиці
                        List<ValueRange> compareResult = this.translationComparator.compareTranslation(crowdinTranslation, sheetTranslation, sheetHeader);
                        if (!compareResult.isEmpty()) countTranslationKeyChange.getAndIncrement(); // Якщо якісь зміни є, підвищуємо кількість перекладів які будуть змінені у Гугл Таблиці
                        return compareResult.stream(); // Повертаємо стрим з результатом порівняння
                    } catch (NoMatchCountValuesException e) { // Якщо переклад має не правильну кількість рядків для рейнджа.
                        logger.warn(e.getMessage());
                        logger.trace("",e); // Відправляємо попередження
                        badCountValuesTranslations.put(sheetTranslation, crowdinTranslation); // Додаємо до мапи де всі погані переклади не мають правильну кількість рядків
                        return Stream.empty(); // Повертаємо порожній стрим
                    }
                }).toList();

        logger.debug("Differ completed: {} translation keys must be change.", countTranslationKeyChange);
        return new TranslationDiffResult(countTranslationKeyChange.get(), valueRanges, badCountValuesTranslations);
    }

    private void requiredContainsSheetHeader(Map<String, TranslationSheetHeader> sheetHeaderByName, GSheetTranslateKey sheetTranslation) {
        if (!sheetHeaderByName.containsKey(sheetTranslation.locationA1().sheetName()))
            throw new IllegalStateException("No exists SheetHeader for " + sheetTranslation.locationA1().sheetName());
    }
}
