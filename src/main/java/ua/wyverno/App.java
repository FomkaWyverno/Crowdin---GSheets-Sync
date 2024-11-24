package ua.wyverno;


import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcestrings.model.SourceString;
import com.crowdin.client.stringtranslations.model.Approval;
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
                .limitAPI(1)
                .filterApi("test-java.csv")
                .maxResults(1)
                .execute().get(0);
        logger.info("---File---");
        logger.info(toJSON(file));
        List<Approval> approvals = this.crowdinService.string_translations()
                .listTranslationApprovals(this.projectID)
                .fileId(file.getId())
                .languageId("uk")
                .execute();
        logger.info("---Approvals---");
        logger.info(toJSON(approvals));
        logger.info("---Get-Approvals");
        approvals.forEach(approval -> {
            try {
                logger.info(toJSON(this.crowdinService.string_translations()
                        .getApproval(this.projectID)
                        .approvalId(approval.getId())
                        .execute()));
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
            }
        });

    }
    public String toJSON(Object obj) throws JsonProcessingException {
        return this.writer.writeValueAsString(obj);
    }
}
