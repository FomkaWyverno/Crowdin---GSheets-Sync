package ua.wyverno;


import com.crowdin.client.sourcefiles.model.Directory;
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
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.EditStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.enums.PathEditString;
import ua.wyverno.crowdin.api.util.edit.PatchEditOperation;

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
        List<SourceString> list = this.crowdinService.sourceStrings()
                .list(this.projectID)
                .limitAPI(1)
                .filterAPI("This is new replaced text from JAVA API")
                .scope("text")
                .maxResults(1)
                .execute();
        logger.info(toJSON(list));
        logger.info(toJSON(this.crowdinService.sourceStrings()
                .batch(this.projectID)
                .replacePatch()
                .execute()));
    }
    public String toJSON(Object obj) throws JsonProcessingException {
        return this.writer.writeValueAsString(obj);
    }
}
