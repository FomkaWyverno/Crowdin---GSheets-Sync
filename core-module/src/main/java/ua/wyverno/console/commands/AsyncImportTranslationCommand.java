package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;
import ua.wyverno.sync.crowdin.translation.services.AsyncImportTranslationService;

@ConsoleCommand(command = "/async-import", description = "Async import translation from Google Sheets to Crowdin")
@Component
public class AsyncImportTranslationCommand implements Command {
    private final AsyncImportTranslationService asyncImportTranslationService;

    @Autowired
    public AsyncImportTranslationCommand(AsyncImportTranslationService asyncImportTranslationService) {
        this.asyncImportTranslationService = asyncImportTranslationService;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Starting asynchronous importing translation.");
        this.asyncImportTranslationService.importTranslationsToCrowdin();
    }
}
