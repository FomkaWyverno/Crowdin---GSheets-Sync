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
import ua.wyverno.console.ConsoleCommandEvent;

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
    private final AppState appState;
    @Autowired
    public App(ApplicationEventPublisher applicationEventPublisher, AppState appState) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.appState = appState;
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    @Override
    public void run(ApplicationArguments args) throws IOException {
        logger.info("Run");

        System.out.println("Console application started. Type /help for the list of commands.");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            while (this.appState.isRunning()) {
                System.out.print("> ");
                String input = reader.readLine();
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
