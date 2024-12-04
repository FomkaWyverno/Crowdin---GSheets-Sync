package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;
import ua.wyverno.sync.SynchronizationService;

@ConsoleCommand(command = "/sync", description = "Run synchronization Crowdin with Google Sheets.")
@Component
public class SyncCommand implements Command {
    private final SynchronizationService synchronizationService;

    @Autowired
    public SyncCommand(SynchronizationService synchronizationService) {
        this.synchronizationService = synchronizationService;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Starting running synchronization Crowdin with Google Sheets...");
        this.synchronizationService.synchronizeWithGoogleSheets();;
    }
}
