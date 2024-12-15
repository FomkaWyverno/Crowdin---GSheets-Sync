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
        logger.info("Step 3: Calling batch update to Google Sheets API. Keys updated: {}", transDiffResult.countTranslationKeyChange());
        BatchUpdateValuesRequest batchUpdate = new BatchUpdateValuesRequest()
                .setData(transDiffResult.valueRanges())
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




}
