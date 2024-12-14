package ua.wyverno.google.sheets.util;

import com.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.localization.model.key.GSheetTranslateRegistryKey;
import ua.wyverno.localization.parsers.GSheetTranslateRegistryKeyParser;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class GSpreadsheetUtil {

    private final GSheetTranslateRegistryKeyParser keyParser;

    @Autowired
    public GSpreadsheetUtil(GSheetTranslateRegistryKeyParser keyParser) {
        this.keyParser = keyParser;
    }

    /**
     * Перетворює вміст електронної таблиці на мапу де ключ це індифікатор ключа перекладу, а значення сам ключ перекладу
     * @param spreadsheet електронна таблиця з вмістом
     * @return мапу де ключ це індифікатор ключа перекладу, а значення сам ключ перекладу
     */
    public Map<String, GSheetTranslateRegistryKey> toTranslateKeyMap(GoogleSpreadsheet spreadsheet) {
        return this.keyParser.parseSpreadsheet(spreadsheet)
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toMap(
                        key -> key.identifier().toString(),
                        Functions.identity()));
    }
}
