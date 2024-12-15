package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.SynchronizationService;
import ua.wyverno.sync.google.sheets.SynchronizationGoogleSheetsService;

@ConsoleCommand(command = "/sync-google-sheets", description = "Run synchronization Google Sheets to Crowdin translations.")
@Component
public class SyncGoogleSheetsCommand implements Command {

    private final SynchronizationService synchronizationService;
    private final SynchronizationGoogleSheetsService synchronizationGoogleSheetsService;

    @Autowired
    public SyncGoogleSheetsCommand(SynchronizationService synchronizationService, SynchronizationGoogleSheetsService synchronizationGoogleSheetsService) {
        this.synchronizationService = synchronizationService;
        this.synchronizationGoogleSheetsService = synchronizationGoogleSheetsService;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Running synchronization Google Sheets to Crowdin translations...");
        GoogleSpreadsheet translationSpreadsheet = this.synchronizationService.getTranslationSpreadsheet();
        this.synchronizationGoogleSheetsService.synchronizeToGoogleSheets(translationSpreadsheet);
    }
}
