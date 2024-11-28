package ua.wyverno;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.google.sheets.GoogleSheetsService;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.localization.model.SourceRegistryKey;
import ua.wyverno.localization.parsers.SourceRegistryKeyParser;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class App implements ApplicationRunner {

    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final CrowdinService crowdinService;
    private final GoogleSheetsService googleSheets;
    private final SourceRegistryKeyParser parser;
    private final String spreadsheetID;
    private final long projectID;

    @Autowired
    public App(CrowdinService crowdinService, GoogleSheetsService googleSheetsService, SourceRegistryKeyParser parser, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.googleSheets = googleSheetsService;
        this.parser = parser;
        this.projectID = configLoader.getConfig().getProjectID();
        this.spreadsheetID = configLoader.getConfig().getSpreadsheetID();
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    @Override
    public void run(ApplicationArguments args) throws IOException {
        logger.info("Run");
        Spreadsheet spreadsheet = this.googleSheets.getSpreadsheetMetadata(this.spreadsheetID);
        List<Sheet> sheets = spreadsheet.getSheets().stream()
                .filter(sheet -> sheet.getProperties().getSheetType().equals("GRID"))
                .filter(sheet -> !sheet.getProperties().getTitle().equals("Статистика"))
                .filter(sheet -> !sheet.getProperties().getTitle().equals("Глосарій"))
                .toList();
        GoogleSpreadsheet googleSpreadsheet = this.googleSheets.getSpreadsheetData(spreadsheet, sheets);
        Map<String, SourceRegistryKey> keyMap = this.parser.parse(googleSpreadsheet);
        logger.info("End");
    }
    public String toJSON(Object obj) {
        try {
            return this.writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
