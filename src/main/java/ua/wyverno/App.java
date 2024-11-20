package ua.wyverno;


import com.crowdin.client.sourcefiles.model.FileInfo;
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
import ua.wyverno.crowdin.api.sourcefiles.PatchSourceFilesOperation;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.edit.EditFilePath;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.edit.PatchFileRequestBuilder;

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
//        Directory directory = this.crowdinService.directories()
//                .list(this.projectID)
//                .maxResults(1)
//                .filterApi("Java-Directory-Edit")
//                .execute().get(0);
//        logger.info(toJSON(directory));
        FileInfo file = this.crowdinService.files()
                .list(this.projectID)
                .maxResults(1)
                .filterApi("test-java.csv")
                .execute().get(0);
        logger.info(toJSON(file));
        logger.info(toJSON(this.crowdinService.sourceStrings()
                .add(this.projectID)
                .fileID(file.getId())
                .text("it is text from Crowdin API")
                .context("Java context test")
                .identifier("java-id-test")
                .execute()));
    }

    public String toJSON(Object obj) throws JsonProcessingException {
        return this.writer.writeValueAsString(obj);
    }
}
