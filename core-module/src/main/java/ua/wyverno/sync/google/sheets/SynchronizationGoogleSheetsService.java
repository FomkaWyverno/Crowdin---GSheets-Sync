package ua.wyverno.sync.google.sheets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.google.sheets.util.GoogleSheetHeader;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.crowdin.managers.fetcher.CrowdinTranslationFetcher;
import ua.wyverno.google.sheets.util.GSpreadsheetUtil;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SynchronizationGoogleSheetsService {
    private final static Logger logger = LoggerFactory.getLogger(SynchronizationGoogleSheetsService.class);

    private final GSpreadsheetUtil spreadsheetSyncUtils;
    private final CrowdinTranslationFetcher translationFetcher;

    @Autowired
    public SynchronizationGoogleSheetsService(GSpreadsheetUtil spreadsheetSyncUtils,
                                              CrowdinTranslationFetcher translationFetcher) {
        this.spreadsheetSyncUtils = spreadsheetSyncUtils;
        this.translationFetcher = translationFetcher;
    }

    public void synchronizeToGoogleSheets(GoogleSpreadsheet spreadsheet) {
        logger.info("Step 1: Downloading translations from Crowdin.");
        this.translationFetcher.fetchTranslations();

        logger.trace("Converting Spreadsheet to map KeyById");
        Map<String, GSheetTranslateRegistryKey> keyTranslateById = this.spreadsheetSyncUtils.toTranslateKeyMap(spreadsheet);
        logger.trace("Sheets to sheet headers by name map.");
        Map<String, GoogleSheetHeader> sheetHeaderByName = spreadsheet.getSheets().stream()
                        .collect(Collectors.toMap(
                                GoogleSheet::getSheetName,
                                GoogleSheetHeader::new));

        logger.info("Step 2:");

    }
}
