package ua.wyverno.sync.translation.managers;

import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.google.sheets.GoogleSheetsService;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.localization.parsers.GSheetTranslateRegistryKeyParser;
import ua.wyverno.sync.SynchronizeSheetManager;
import ua.wyverno.utils.json.JSONCreator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class GoogleSheetsTranslationManager {

    private final GoogleSheetsService sheetsService;
    private final SynchronizeSheetManager sheetManager;
    private final GSheetTranslateRegistryKeyParser sheetParser;
    private final JSONCreator jsonCreator;

    private final String spreadsheetId;

    @Autowired
    public GoogleSheetsTranslationManager(GoogleSheetsService sheetsService,
                                          SynchronizeSheetManager sheetManager,
                                          GSheetTranslateRegistryKeyParser sheetParser,
                                          JSONCreator jsonCreator,
                                          ConfigLoader configLoader) {
        this.sheetsService = sheetsService;
        this.sheetManager = sheetManager;
        this.sheetParser = sheetParser;
        this.jsonCreator = jsonCreator;
        this.spreadsheetId = configLoader.getCoreConfig().getSpreadsheetID();
    }

    /**
     * Завантажує гугл табличку та парсить її, та повертає всі ключі.
     *
     * @return Мапа де ключ це айді ключа, а значення сам ключ перекладу
     */
    public Map<String, GSheetTranslateRegistryKey> getTranslationsKeys() {
        try {
            Spreadsheet spreadsheet = this.sheetsService.getSpreadsheetMetadata(this.spreadsheetId);
            List<Sheet> sheets = spreadsheet.getSheets().stream() // Фільтруємо всі аркуші які повині пропустити
                    .filter(sheet -> !this.sheetManager.shouldSkipSheetSynchronize(sheet))
                    .toList();
            GoogleSpreadsheet googleSpreadsheet = this.sheetsService.getSpreadsheetData(this.spreadsheetId, sheets); // Отримуємо вміст аркушів
            Map<GoogleSheet, List<GSheetTranslateRegistryKey>> keysMap = this.sheetParser.parseSpreadsheet(googleSpreadsheet); // Отримуємо мапу з ключів з таблиці

            return keysMap.values().stream() // Перетворюємо мапу де ключ це айді ключа перекладу, а значення саме значення
                    .flatMap(List::stream) // Об'єднуємо всі листи ключів у один потік
                    .collect(Collectors.toMap(
                            key -> key.identifier().toString(), // встановлюємо ключ як Айді у текстовому варіанті
                            Function.identity(),
                            (exists, duplicate) -> {
                                throw new IllegalStateException(
                                        String.format("Duplicate key detected:%n%s%nOriginal:%n%s",
                                                this.jsonCreator.toJSON(duplicate),
                                                this.jsonCreator.toJSON(exists)));
                            })); // Значення сам ключ з гугл таблички
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
