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
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsAddPatch;
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsBatchQuery;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;

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
        FileInfo file = this.crowdinService.files()
                .list(this.projectID)
                .filterApi("java.json")
                .maxResults(1)
                .execute().get(0);
        StringsBatchQuery query = this.crowdinService.sourceStrings()
                .batch(this.projectID);

        for (int i = 0; i < 1500; i++) {
            AddStringRequestBuilder request = new AddStringRequestBuilder();
            request.text("Very big patch java test #1")
                    .fileID(file.getId())
                    .identifier("big.patch.id#"+i)
                    .context("Big patch context#"+i);

            query.addPatch(request);
        }

        logger.info(toJSON(query.execute()));
    }

    public String toJSON(Object obj) throws JsonProcessingException {
        return this.writer.writeValueAsString(obj);
    }
}
