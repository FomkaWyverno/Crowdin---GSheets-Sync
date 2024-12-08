package ua.wyverno.csv.parsers;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.localization.parsers.GSheetTranslateRegistryKeyParser;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

@Component
public class GoogleSheetToCSVParser {
    private final static Logger logger = LoggerFactory.getLogger(GoogleSheetToCSVParser.class);
    private final static String[] HEADERS = new String[]{"Id","Text","Context","Translate"};

    private final GSheetTranslateRegistryKeyParser translationParser;

    @Autowired
    public GoogleSheetToCSVParser(GSheetTranslateRegistryKeyParser translationParser) {
        this.translationParser = translationParser;
    }

    public String parseSheet(GoogleSheet sheet) {
        StringWriter sw = new StringWriter();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build();
        List<GSheetTranslateRegistryKey> keys = this.translationParser.parseSheet(sheet);
        try (final CSVPrinter printer = new CSVPrinter(sw, csvFormat)) {
            for (GSheetTranslateRegistryKey key : keys) {
                printer.printRecord(key.identifier().toString(), key.originalText(), key.context());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sw.toString().replace("\r\n", "\n");
    }
}
