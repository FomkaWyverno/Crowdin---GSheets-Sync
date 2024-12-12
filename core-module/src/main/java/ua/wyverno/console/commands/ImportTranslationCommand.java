package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;
import ua.wyverno.sync.crowdin.translation.services.ImportTranslationService;

@ConsoleCommand(command = "/import", description = "Import translation from Google Sheets to Crowdin in one thread")
@Component
public class ImportTranslationCommand implements Command {

    private final ImportTranslationService importTranslationService;

    @Autowired
    public ImportTranslationCommand(ImportTranslationService importTranslationService) {
        this.importTranslationService = importTranslationService;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Starting importing translation in one thread.");
        this.importTranslationService.importTranslationsToCrowdin();
    }
}
