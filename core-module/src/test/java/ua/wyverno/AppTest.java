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
import ua.wyverno.config.CoreConfig;
import ua.wyverno.crowdin.CrowdinService;

import java.util.List;

@SpringBootApplication
public class AppTest implements ApplicationRunner {
    private final static Logger logger = LoggerFactory.getLogger(AppTest.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(AppTest.class, args);
    }

    private final CrowdinService crowdinService;
    private final CoreConfig coreConfig;

    private final ObjectMapper mapper = new ObjectMapper();
    private final ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

    @Autowired
    public AppTest(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.coreConfig = configLoader.getCoreConfig();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<FileInfo> files = this.crowdinService.files()
                .list(this.coreConfig.getProjectID())
                .execute();
        logger.info(toJSON(files));
    }

    public String toJSON(Object obj) {
        try {
            return this.writer.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
