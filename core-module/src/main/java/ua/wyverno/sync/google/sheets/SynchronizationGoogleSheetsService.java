package ua.wyverno.sync.google.sheets;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.model.CrowdinTranslation;
import ua.wyverno.google.sheets.GoogleSheetsService;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.google.sheets.util.A1RangeNotation;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;
import ua.wyverno.google.sheets.util.SheetA1NotationUtil;
import ua.wyverno.localization.model.key.GSheetTranslateRegistryKey;
import ua.wyverno.crowdin.managers.fetcher.CrowdinTranslationFetcher;
import ua.wyverno.google.sheets.util.GSpreadsheetUtil;
import ua.wyverno.localization.config.LocalizationNameColumns;
import ua.wyverno.sync.google.sheets.exceptions.GoogleSheetNoHasTranslationException;
import ua.wyverno.sync.google.sheets.exceptions.NoMatchCountLinesException;
import ua.wyverno.utils.json.JSONCreator;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SynchronizationGoogleSheetsService {
    private final static Logger logger = LoggerFactory.getLogger(SynchronizationGoogleSheetsService.class);

    private final GSpreadsheetUtil spreadsheetSyncUtils;
    private final CrowdinTranslationFetcher translationFetcher;
    private final GoogleSheetsService googleSheetsService;
    private final LocalizationNameColumns localizationNameColumns;

    private final String spreadsheetId;

    private final JSONCreator jsonCreator;

    @Autowired
    public SynchronizationGoogleSheetsService(GSpreadsheetUtil spreadsheetSyncUtils,
                                              CrowdinTranslationFetcher translationFetcher,
                                              GoogleSheetsService googleSheetsService,
                                              LocalizationNameColumns localizationNameColumns,
                                              ConfigLoader configLoader,
                                              JSONCreator jsonCreator) {
        this.spreadsheetSyncUtils = spreadsheetSyncUtils;
        this.translationFetcher = translationFetcher;
        this.googleSheetsService = googleSheetsService;
        this.localizationNameColumns = localizationNameColumns;
        this.spreadsheetId = configLoader.getCoreConfig().getSpreadsheetID();
        this.jsonCreator = jsonCreator;
    }

    public void synchronizeToGoogleSheets(GoogleSpreadsheet spreadsheet) {
        logger.info("Step 1: Downloading translations from Crowdin.");
        List<CrowdinTranslation> translationList = this.translationFetcher.fetchTranslations();

        logger.trace("Converting Spreadsheet to map KeyById");
        Map<String, GSheetTranslateRegistryKey> keyTranslateById = this.spreadsheetSyncUtils.toTranslateKeyMap(spreadsheet);
        logger.trace("Sheets to sheet headers by name map.");
        Map<String, GoogleSheetHeader> sheetHeaderByName = spreadsheet.getSheets().stream()
                        .collect(Collectors.toMap(
                                GoogleSheet::getSheetName,
                                GoogleSheetHeader::new));

        logger.info("Step 2: Creating a request to update Google Sheet for updated translations."); //TODO: Додати візуальний метасимвол "\n" до перекладу, а також оновити рядки парсинг рядків, та підрахунок рядків

        int countTranslationKeyChange = 0;
        List<ValueRange> valueRanges = new ArrayList<>(); // Створюємо лист для значення оновлень
        for (CrowdinTranslation crowdinTranslation : translationList) { // Беремо кожен елемент перекладу на Кроудін
            if (!keyTranslateById.containsKey(crowdinTranslation.getSourceString().getIdentifier())) { // Перевіряємо чи є ключ перекладу у Гугл Таблиці, перекладу Кроудін,
                                                                                                        // не має бути помилок, якщо всі об'єкти з одного проєкту Кроудін
                String errorMessage = String.format("In Google Sheet not has translation key - identifier: %s%nJSON: %s",
                        crowdinTranslation.getSourceString().getIdentifier(),
                        this.jsonCreator.toJSON(crowdinTranslation));
                throw new GoogleSheetNoHasTranslationException(errorMessage);
            }
            // Дістаємо ключ перекладу з Гугл таблиці
            GSheetTranslateRegistryKey sheetTranslation = keyTranslateById.get(crowdinTranslation.getSourceString().getIdentifier());

            // Ділимо рядок переклад, на лінії, де кожна наступній лінії, це еквівалентно одній наступній комірці
            List<String> sheetTranslLines = this.strTranslationToLines(sheetTranslation.translate());
            List<String> crowdinTransLines = this.strTranslationToLines(crowdinTranslation.getTranslation());


            if (!this.matchTranslations(crowdinTransLines, sheetTranslLines, sheetTranslation)) { // Порівнюємо переклади
                countTranslationKeyChange++; // Якщо переклади різні, тоді збільшуємо кількість змінених перекладів
                A1RangeNotation a1 = SheetA1NotationUtil.fromA1RangeNotation(sheetTranslation.sheetLocationA1()); // Створюємо А1 нотацію на основі ключа перекладу з гугл таблиці, та його розташування у таблиці
                if (!sheetHeaderByName.containsKey(a1.sheetName())) // якщо хедера немає для таблиці токи викидуємо виключення
                    throw new GoogleSheetNoHasTranslationException("No exists in sheetHeaderByName header by SheetName: " + a1.sheetName());
                GoogleSheetHeader sheetHeader = sheetHeaderByName.get(a1.sheetName()); // Беремо хедер

                if (sheetHeader.containsColumn(this.localizationNameColumns.getFormattedText())) { // Якщо існує колонка для форматування
                    int formattedColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getFormattedText()); // Беремо індекс колонки форматування
                    String range = SheetA1NotationUtil.rangeToA1Notation( // Рейндж створюємо лише для однієї комірки
                            a1.sheetName(), // Назва аркуша
                            a1.startRowIndex(), // Індекс початку рядка, для рейнджу
                            formattedColumnIndex, // Індекс початкової колонки, для рейнджу
                            a1.startRowIndex(), // Індекс кінцевого рядка, для рейнджу
                            formattedColumnIndex); // Індекс кінцевої колонки для рейнджу

                    // Встановлюємо значення для комірки форматованого тексту
                    List<List<Object>> values = Collections.singletonList(Collections.singletonList(crowdinTranslation.getTranslation()));

                    // Створюємо об'єкт рейнджа, встановлюємо рейндж у вигляді А1 Нотації, та значення для нього.
                    valueRanges.add(new ValueRange()
                            .setRange(range)
                            .setValues(values));
                } else { // Якщо не існує Formatted-Text колонки тоді має бути лише Translate-Text та Edit-Text
                    int editColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getEditText()); // Індекс колонки Edit Text
                    int translateColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getTranslateText()); // Індекс колонки Translate-Text

                    String rangeTranslate = SheetA1NotationUtil.rangeToA1Notation(
                            a1.sheetName(), // Назва аркуша
                            a1.startRowIndex(), // Індекс початку рядка, для рейнджу
                            translateColumnIndex, // Індекс початкової колонки, для рейнджу
                            a1.endRowIndex(), // Індекс кінцевого рядка, для рейнджу
                            translateColumnIndex); // Індекс кінцевої колонки для рейнджу
                    List<List<Object>> valuesTranslate = crowdinTransLines.stream() // Встановлюємо значення для рейнджа
                            .map(line -> Collections.singletonList((Object) line))
                            .toList();

                    valueRanges.add(new ValueRange() // Створюємо об'єкт рейнджа, встановлюємо рейндж у вигляді А1 Нотації, та значення для нього.
                            .setRange(rangeTranslate)
                            .setValues(valuesTranslate));

                    if (crowdinTranslation.isApprove()) { // Якщо він затверджений
                        String rangeApprove = SheetA1NotationUtil.rangeToA1Notation(
                                a1.sheetName(), // Назва аркуша
                                a1.startRowIndex(), // Індекс початку рядка, для рейнджу
                                editColumnIndex, // Індекс початкової колонки, для рейнджу
                                a1.endRowIndex(), // Індекс кінцевого рядка, для рейнджу
                                editColumnIndex); // Індекс кінцевої колонки для рейнджу
                        List<List<Object>> valuesApprove = crowdinTransLines.stream() // Встановлюємо значення для рейнджа
                                .map(line -> Collections.singletonList((Object) line))
                                .toList();
                        valueRanges.add(new ValueRange() // Створюємо об'єкт рейнджа, встановлюємо рейндж у вигляді А1 Нотації, та значення для нього.
                                .setRange(rangeApprove)
                                .setValues(valuesApprove));
                    }
                }
            } else if (crowdinTranslation.isApprove() && !sheetTranslation.isApprove()) { // Якщо у Кроудіні затверджений, але в аркуші він не затверджений, тоді робимо, щоб цей переклад був у Аркуші затвердженим
                countTranslationKeyChange++;
                A1RangeNotation a1 = SheetA1NotationUtil.fromA1RangeNotation(sheetTranslation.sheetLocationA1());
                if (!sheetHeaderByName.containsKey(a1.sheetName()))
                    throw new GoogleSheetNoHasTranslationException("No exists in sheetHeaderByName header by SheetName: " + a1.sheetName());
                GoogleSheetHeader sheetHeader = sheetHeaderByName.get(a1.sheetName());
                int editColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getEditText());

                String rangeApprove = SheetA1NotationUtil.rangeToA1Notation(
                        a1.sheetName(),
                        a1.startRowIndex(),
                        editColumnIndex,
                        a1.endRowIndex(),
                        editColumnIndex);
                List<List<Object>> valuesApprove = crowdinTransLines.stream()
                        .map(line -> Collections.singletonList((Object) line))
                        .toList();
                valueRanges.add(new ValueRange()
                        .setRange(rangeApprove)
                        .setValues(valuesApprove));
            } else if (!crowdinTranslation.isApprove() && sheetTranslation.isApprove()) { // Якщо у Кроудіні не затверджено, але затверджено у Гугл таблицях, прибираємо затвердження у таблиці
                countTranslationKeyChange++;
                A1RangeNotation a1 = SheetA1NotationUtil.fromA1RangeNotation(sheetTranslation.sheetLocationA1());
                if (!sheetHeaderByName.containsKey(a1.sheetName()))
                    throw new GoogleSheetNoHasTranslationException("No exists in sheetHeaderByName header by SheetName: " + a1.sheetName());
                GoogleSheetHeader sheetHeader = sheetHeaderByName.get(a1.sheetName());
                int editColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getEditText());

                String rangeApprove = SheetA1NotationUtil.rangeToA1Notation(
                        a1.sheetName(),
                        a1.startRowIndex(),
                        editColumnIndex,
                        a1.endRowIndex(),
                        editColumnIndex);
                List<List<Object>> valuesApprove = crowdinTransLines.stream()
                        .map(line -> Collections.singletonList((Object) ""))
                        .toList();
                valueRanges.add(new ValueRange()
                        .setRange(rangeApprove)
                        .setValues(valuesApprove));
            }
        }

        logger.info("Step 3: Calling batch update to Google Sheets API. Keys updated: {}", countTranslationKeyChange);
        BatchUpdateValuesRequest batchUpdate = new BatchUpdateValuesRequest()
                .setData(valueRanges)
                .setValueInputOption("RAW");

        try {
            BatchUpdateValuesResponse response = this.googleSheetsService
                    .getApi()
                    .spreadsheets()
                    .values()
                    .batchUpdate(this.spreadsheetId, batchUpdate)
                    .execute();
            logger.info(this.jsonCreator.toJSON(response));
        } catch (GoogleJsonResponseException ex) {
            GoogleJsonError error = ex.getDetails();
            if (error.getCode() == 404) {
                logger.error("Spreadsheet not found with id: {}", this.spreadsheetId);
            } else {
                throw new RuntimeException(ex);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        logger.info("Finish synchronize to Google Sheet.");
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
    private boolean matchTranslations(List<String> crowdinTransLines, List<String> sheetTransLines, GSheetTranslateRegistryKey sheetTranslation) {
        if (sheetTransLines.size() != crowdinTransLines.size()) {
            String errorMessage = String.format("""
                        The number of rows translated in Crowdin does not match the number of rows in Google Sheet.
                        Crowdin translation count lines: %d
                        Google Sheet translation count lines: %d
                        Location translation in sheet: %s
                        Crowdin Identifier: %s""",
                    crowdinTransLines.size(),
                    sheetTransLines.size(),
                    sheetTranslation.sheetLocationA1(),
                    sheetTranslation.identifier().toString());
            throw new NoMatchCountLinesException(errorMessage);
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
