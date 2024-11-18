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
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class App implements ApplicationRunner {

    private final static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    private final CrowdinService crowdinService;
    private final long projectID;

    @Autowired
    public App(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.projectID = configLoader.getConfig().getProjectID();
    }

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
    @Override
    public void run(ApplicationArguments args) throws JsonProcessingException {
        logger.info("Run");
        logger.info("----------------------LIST DIRECTORIES----------------------------------");
        logger.info(toJSON(this.crowdinService.listAllDirectories(this.projectID)));
        logger.info("----------------------LIST FILES----------------------------------");
        logger.info(toJSON(this.crowdinService.listAllFiles(this.projectID)));
        logger.info("----------------------FIND DIRECTORY----------------------------------");
        logger.info(toJSON(this.crowdinService.findDirectories(this.projectID, List.of("in-in-testick", "test2", "tes3"))));
        logger.info("----------------------FIND FILES----------------------------------");
        logger.info(toJSON((this.crowdinService.findFiles(this.projectID, List.of("interface.csv", "test.csv")))));
    }

    public String toJSON(Object obj) throws JsonProcessingException {
        return this.writer.writeValueAsString(obj);
    }
}
