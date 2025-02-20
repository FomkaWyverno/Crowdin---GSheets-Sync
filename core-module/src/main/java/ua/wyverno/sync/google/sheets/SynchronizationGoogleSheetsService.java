package ua.wyverno.sync.google.sheets;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.model.CrowdinTranslation;
import ua.wyverno.google.sheets.GoogleSheetsService;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.crowdin.managers.fetcher.CrowdinTranslationFetcher;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.sync.google.sheets.operations.TranslationDiffer;
import ua.wyverno.sync.google.sheets.operations.results.TranslationDiffResult;
import ua.wyverno.utils.json.JSONCreator;

import java.io.IOException;
import java.util.*;

@Service
public class SynchronizationGoogleSheetsService {
    private final static Logger logger = LoggerFactory.getLogger(SynchronizationGoogleSheetsService.class);

    private final CrowdinTranslationFetcher translationFetcher;
    private final GoogleSheetsService googleSheetsService;
    private final TranslationDiffer translationDiffer;

    private final String spreadsheetId;

    private final JSONCreator jsonCreator;

    @Autowired
    public SynchronizationGoogleSheetsService(CrowdinTranslationFetcher translationFetcher,
                                              GoogleSheetsService googleSheetsService,
                                              TranslationDiffer translationDiffer,
                                              ConfigLoader configLoader,
                                              JSONCreator jsonCreator) {
        this.translationFetcher = translationFetcher;
        this.googleSheetsService = googleSheetsService;
        this.translationDiffer = translationDiffer;
        this.spreadsheetId = configLoader.getCoreConfig().getSpreadsheetID();
        this.jsonCreator = jsonCreator;
    }

    public void synchronizeToGoogleSheets(GoogleSpreadsheet spreadsheet) {
        logger.info("Start synchronize to Google Sheets");
        logger.info("Step 1: Downloading translations from Crowdin.");
        List<CrowdinTranslation> crowdinTranslations = this.translationFetcher.fetchTranslations();
        logger.info("Step 2: Creating a request to update Google Sheet for updated translations.");
        TranslationDiffResult transDiffResult = this.translationDiffer.diffTranslations(crowdinTranslations, spreadsheet);

        this.logBadCountValuesTranslations(transDiffResult.badCountValuesTranslations());

        logger.info("Step 3: Calling batch update to Google Sheets API. Keys must be update: {}", transDiffResult.countTranslationKeyChange());
        BatchUpdateValuesRequest batchUpdate = new BatchUpdateValuesRequest()
                .setData(transDiffResult.valueRanges())
                .setValueInputOption("RAW");

        if (!transDiffResult.valueRanges().isEmpty()) {
            logger.info("Start updating Google Sheets...");
            this.updateSheets(batchUpdate);
        } else {
            logger.info("Don't need to update anything in Google Sheets.");
        }

        logger.info("Finish synchronize to Google Sheet.");
    }

    /**
     * Оновлює гугл таблицю значеннями
     * @param batchUpdate запит на оновлення таблиці
     */
    private void updateSheets(BatchUpdateValuesRequest batchUpdate) {
        try {
            BatchUpdateValuesResponse response = this.googleSheetsService
                    .getApi()
                    .spreadsheets()
                    .values()
                    .batchUpdate(this.spreadsheetId, batchUpdate)
                    .execute();
            StringBuilder updatedRange = new StringBuilder("Updated Ranges:\n");

            response.getResponses().forEach(valuesResponse ->
                    updatedRange.append(valuesResponse.getUpdatedRange()).append("\n"));
            updatedRange.deleteCharAt(updatedRange.length() - 1);
            logger.info(updatedRange.toString());
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
    }


    /**
     * Логує мапу з перекладами у Гугл таблиці як ключа, і як значення відповідне у Кроудіні.
     * Які мають не таку кількість рядків як у Гугл Таблиці.
     * Якщо мапа порожня, нічого не виведеться
     * @param badCountValuesTranslations Мапа з не відповідними за кількістю рядків у Кроудіні.
     */
    private void logBadCountValuesTranslations(Map<GSheetTranslateKey, CrowdinTranslation> badCountValuesTranslations) {
        if (!badCountValuesTranslations.isEmpty()) {
            StringBuilder warnMessageBuilder = new StringBuilder("Wrong number of lines in Crowdin translations:\n");
            badCountValuesTranslations.forEach((sheetTranslation, crowdinTranslation) -> {
                warnMessageBuilder.append("Identifier: ")
                        .append(sheetTranslation.identifier().toString())
                        .append("\nLocation A1: ")
                        .append(sheetTranslation.locationA1().toString())
                        .append("\nLines Sheets: ")
                        .append(sheetTranslation.originalText().lines().count())
                        .append("\nLines Crowdin Translation: ")
                        .append(crowdinTranslation.getTranslation().lines().count())
                        .append("\nGoogle Sheet Original Text:\n")
                        .append(sheetTranslation.originalText())
                        .append("\nCrowdin Translation:\n")
                        .append(crowdinTranslation.getTranslation())
                        .append("\n");
            });

            warnMessageBuilder.deleteCharAt(warnMessageBuilder.length() - 1);

            logger.warn(warnMessageBuilder.toString());
        } else {
            logger.info("No translations that have the wrong number of lines");
        }
    }
}
