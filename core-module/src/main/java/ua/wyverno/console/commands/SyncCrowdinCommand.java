package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.SynchronizationService;
import ua.wyverno.sync.crowdin.SynchronizationCrowdinService;

@ConsoleCommand(command = "/sync-crowdin", description = "Run synchronization Crowdin to Google Sheets.")
@Component
public class SyncCrowdinCommand implements Command {

    private final SynchronizationService synchronizationService;
    private final SynchronizationCrowdinService synchronizationCrowdinService;

    @Autowired
    public SyncCrowdinCommand(SynchronizationService synchronizationService, SynchronizationCrowdinService synchronizationCrowdinService) {
        this.synchronizationService = synchronizationService;
        this.synchronizationCrowdinService = synchronizationCrowdinService;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Running synchronization Crowdin to Google Sheets...");
        GoogleSpreadsheet translationSpreadsheet = this.synchronizationService.getTranslationSpreadsheet();
        this.synchronizationCrowdinService.synchronizeToCrowdin(translationSpreadsheet);
    }
}
