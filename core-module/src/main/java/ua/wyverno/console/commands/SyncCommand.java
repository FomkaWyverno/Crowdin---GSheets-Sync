package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;
import ua.wyverno.sync.SynchronizationService;

@ConsoleCommand(command = "/sync", description = "Run synchronization Crowdin to Google Sheets and Google Sheets to Crowdin translations.")
@Component
public class SyncCommand implements Command {
    private final SynchronizationService synchronizationService;

    @Autowired
    public SyncCommand(SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Running full synchronization Crowdin Google Sheets...");
        this.synchronizationService.synchronizeTranslations();;
    }
}
