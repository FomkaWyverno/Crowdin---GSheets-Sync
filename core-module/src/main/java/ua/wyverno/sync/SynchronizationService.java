package ua.wyverno.sync;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.google.sheets.GoogleSheetsService;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.crowdin.SynchronizationCrowdinService;
import ua.wyverno.sync.google.sheets.SynchronizationGoogleSheetsService;

import java.io.IOException;
import java.util.List;

@Service
public class SynchronizationService {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationService.class);

    private final String spreadsheetId;
    private final GoogleSheetsService googleSheetsService;

    private final SynchronizationCrowdinService synchronizationCrowdinService;
    private final SynchronizeSheetManager synchronizeSheetManager;

    private final SynchronizationGoogleSheetsService synchronizationGoogleSheetsService;


    @Autowired
    public SynchronizationService(SynchronizationCrowdinService synchronizationCrowdinService,
                                  SynchronizeSheetManager synchronizeSheetManager,
                                  SynchronizationGoogleSheetsService synchronizationGoogleSheetsService,
                                  GoogleSheetsService googleSheetsService,
                                  ConfigLoader configLoader) {
        this.synchronizationCrowdinService = synchronizationCrowdinService;
        this.synchronizeSheetManager = synchronizeSheetManager;
        this.synchronizationGoogleSheetsService = synchronizationGoogleSheetsService;
        this.googleSheetsService = googleSheetsService;
        this.spreadsheetId = configLoader.getCoreConfig().getSpreadsheetID();
    }

    public void synchronizeTranslations() {
        GoogleSpreadsheet translationSpreadsheet = this.getTranslationSpreadsheet();
        this.synchronizationCrowdinService.synchronizeToCrowdin(translationSpreadsheet);
        this.synchronizationGoogleSheetsService.synchronizeToGoogleSheets(translationSpreadsheet);
    }

    /**
     * @return Повертає електронну таблицю лише з аркушами які мають перекладаи
     */
    public GoogleSpreadsheet getTranslationSpreadsheet() {
        try {
            logger.info("Getting spreadsheet metadata from Google Sheets API.");
            Spreadsheet spreadsheetMetadata = this.googleSheetsService.getSpreadsheetMetadata(this.spreadsheetId);

            logger.info("Starting filtering sheets by translation only.");
            List<Sheet> sheets = spreadsheetMetadata.getSheets().stream()
                    .filter(sheet -> !this.synchronizeSheetManager.shouldSkipSheetSynchronize(sheet))
                    .toList();
            logger.info("Getting spreadsheet with content only with filtered sheets.");
            return this.googleSheetsService.getSpreadsheetData(this.spreadsheetId, sheets);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
