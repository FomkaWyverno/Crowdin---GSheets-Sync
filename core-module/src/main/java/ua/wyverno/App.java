package ua.wyverno;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.console.ConsoleCommandEvent;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.google.sheets.GoogleSheetsService;
import ua.wyverno.localization.parsers.TranslateRegistryKeyParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootApplication
public class App implements ApplicationRunner {

    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final ApplicationEventPublisher applicationEventPublisher;
    private final CrowdinService crowdinService;
    private final GoogleSheetsService googleSheets;
    private final TranslateRegistryKeyParser parser;
    private final String spreadsheetID;
    private final long projectID;

    @Autowired
    public App(ApplicationEventPublisher applicationEventPublisher, CrowdinService crowdinService, GoogleSheetsService googleSheetsService, TranslateRegistryKeyParser parser, ConfigLoader configLoader) {
        this.applicationEventPublisher = applicationEventPublisher;
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

        System.out.println("Console application started. Type /help for the list of commands.");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print("> ");
                String input = reader.readLine();
                if (input.equalsIgnoreCase("/exit")) {
                    System.out.println("Exiting application...");
                    break;
                }
                this.applicationEventPublisher.publishEvent(new ConsoleCommandEvent(input));
            }
        }

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
