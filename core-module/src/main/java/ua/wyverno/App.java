package ua.wyverno;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.services.sheets.v4.Sheets;
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

import java.io.IOException;

@SpringBootApplication
public class App implements ApplicationRunner {

    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final CrowdinService crowdinService;
    private final Sheets googleSheets;
    private final String spreadsheetID;
    private final long projectID;

    @Autowired
    public App(CrowdinService crowdinService, GoogleSheetsService googleSheetsService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.googleSheets = googleSheetsService.getService();
        this.projectID = configLoader.getConfig().getProjectID();
        this.spreadsheetID = configLoader.getConfig().getSpreadsheetID();
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    @Override
    public void run(ApplicationArguments args) throws IOException {
        logger.info("Run");
    }
    public String toJSON(Object obj) {
        try {
            return this.writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
