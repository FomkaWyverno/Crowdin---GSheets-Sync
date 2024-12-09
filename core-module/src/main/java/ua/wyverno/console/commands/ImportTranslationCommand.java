package ua.wyverno.console.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.console.Command;
import ua.wyverno.console.ConsoleCommand;
import ua.wyverno.sync.translation.ImportTranslationService;

@ConsoleCommand(command = "/import-translation", description = "Importing translation from sheets to Crowdin")
@Component
public class ImportTranslationCommand implements Command {

    private final ImportTranslationService importTranslationService;

    @Autowired
    public ImportTranslationCommand(ImportTranslationService importTranslationService) {
        this.importTranslationService = importTranslationService;
    }

    @Override
    public void execute(String[] args) {
        System.out.println("Starting importing translation.");
        this.importTranslationService.importTranslationsToCrowdin();
    }
}
