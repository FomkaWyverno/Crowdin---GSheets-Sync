package ua.wyverno.sync.google.sheets;

import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SynchronizationGoogleSheetsService {
    private final static Logger logger = LoggerFactory.getLogger(SynchronizationGoogleSheetsService.class);

    private final GSpreadsheetUtil spreadsheetSyncUtils;
    private final CrowdinTranslationFetcher translationFetcher;
    private final GoogleSheetsService googleSheetsService;
    private final LocalizationNameColumns localizationNameColumns;
    private final JSONCreator jsonCreator;

    @Autowired
    public SynchronizationGoogleSheetsService(GSpreadsheetUtil spreadsheetSyncUtils,
                                              CrowdinTranslationFetcher translationFetcher,
                                              GoogleSheetsService googleSheetsService,
                                              LocalizationNameColumns localizationNameColumns,
                                              JSONCreator jsonCreator) {
        this.spreadsheetSyncUtils = spreadsheetSyncUtils;
        this.translationFetcher = translationFetcher;
        this.googleSheetsService = googleSheetsService;
        this.localizationNameColumns = localizationNameColumns;
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

        logger.info("Step 2:"); //TODO: Додати візуальний метасимвол "\n" до перекладу, а також оновити рядки парсинг рядків, та підрахунок рядків

        List<ValueRange> valueRanges = new ArrayList<>();
        for (CrowdinTranslation crowdinTranslation : translationList) {
            if (!keyTranslateById.containsKey(crowdinTranslation.getSourceString().getIdentifier())) {
                String errorMessage = String.format("In Google Sheet not has translation key - identifier: %s%nJSON: %s",
                        crowdinTranslation.getSourceString().getIdentifier(),
                        this.jsonCreator.toJSON(crowdinTranslation));
                throw new GoogleSheetNoHasTranslationException(errorMessage);
            }

            GSheetTranslateRegistryKey sheetTranslation = keyTranslateById.get(crowdinTranslation.getSourceString().getIdentifier());

            String strSheetTranslation = sheetTranslation.translate();
            String strCrowdinTranslation = crowdinTranslation.getTranslation();

            if (!this.isCountLineMatch(sheetTranslation.originalText(), strCrowdinTranslation)) {
                String errorMessage = String.format("""
                        The number of rows translated in Crowdin does not match the number of rows in Google Sheet.
                        Crowdin translation count lines: %d
                        Google Sheet translation count lines: %d
                        Location translation: %s""",
                        strCrowdinTranslation.lines().count(),
                        strSheetTranslation.lines().count(),
                        sheetTranslation.sheetLocationA1());
                throw new NoMatchCountLinesException(errorMessage);
            }

            if (!strSheetTranslation.equals(strCrowdinTranslation)) {
                A1RangeNotation a1 = SheetA1NotationUtil.fromA1RangeNotation(sheetTranslation.sheetLocationA1());
                if (!sheetHeaderByName.containsKey(a1.sheetName()))
                    throw new GoogleSheetNoHasTranslationException("No exists in sheetHeaderByName header by SheetName: " + a1.sheetName());
                GoogleSheetHeader sheetHeader = sheetHeaderByName.get(a1.sheetName());

                if (sheetHeader.containsColumn(this.localizationNameColumns.getFormattedText())) { // Якщо існує колонка для форматування
                    int formattedColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getFormattedText());
                    String range = SheetA1NotationUtil.rangeToA1Notation( // Рейндж створюємо лише для однієї комірки
                            a1.sheetName(),
                            a1.startRowIndex(),
                            formattedColumnIndex,
                            a1.startRowIndex(),
                            formattedColumnIndex);

                    // Створюємо значення
                    List<List<Object>> values = Collections.singletonList(Collections.singletonList(crowdinTranslation.getTranslation()));

                    valueRanges.add(new ValueRange()
                            .setRange(range)
                            .setValues(values));
                } else { // Якщо не існує Formatted-Text колонки тоді має бути лише Translate-Text та Edit-Text
                    int editColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getEditText());
                    int translateColumnIndex = sheetHeader.getColumnIndex(this.localizationNameColumns.getTranslateText());

                    String rangeTranslate = SheetA1NotationUtil.rangeToA1Notation(
                            a1.sheetName(),
                            a1.startRowIndex(),
                            translateColumnIndex,
                            a1.endRowIndex(),
                            translateColumnIndex);
                    List<List<Object>> valuesTranslate = crowdinTranslation.getTranslation().lines()
                            .map(line -> Collections.singletonList((Object) line))
                            .toList();
                    valueRanges.add(new ValueRange()
                            .setRange(rangeTranslate)
                            .setValues(valuesTranslate));

                    if (crowdinTranslation.isApprove()) {
                        String rangeApprove = SheetA1NotationUtil.rangeToA1Notation(
                                a1.sheetName(),
                                a1.startRowIndex(),
                                editColumnIndex,
                                a1.endRowIndex(),
                                editColumnIndex);
                        List<List<Object>> valuesApprove = crowdinTranslation.getTranslation().lines()
                                .map(line -> Collections.singletonList((Object) line))
                                .toList();
                        valueRanges.add(new ValueRange()
                                .setRange(rangeApprove)
                                .setValues(valuesApprove));
                    }
                }
            }
        }

        logger.info(this.jsonCreator.toJSON(valueRanges));
    }

    /**
     * Перевіряє чи за кількістю лінії збігається рядок
     * @param str1 перший рядок
     * @param str2 другий рядок
     * @return true якщо однакова кількість рядків, інакше false
     */
    private boolean isCountLineMatch(String str1, String str2) {
        return str1.lines().count() == str2.lines().count();
    }


}
