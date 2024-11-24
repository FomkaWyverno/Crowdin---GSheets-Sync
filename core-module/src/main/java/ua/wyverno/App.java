package ua.wyverno;


import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcestrings.model.SourceString;
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
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsBatchQuery;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.EditBatchStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.enums.PathEditString;

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
    public void run(ApplicationArguments args) {
        logger.info("Run");
        logger.info(toJSON(this.crowdinService.string_translations()
                .listLanguageTranslations(this.projectID)
                .languageId("uk").execute()));
        logger.info(toJSON(this.crowdinService.string_translations().deleteTranslation(this.projectID).translationId(19208L).execute()));
    }
    public String toJSON(Object obj) {
        try {
            return this.writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
