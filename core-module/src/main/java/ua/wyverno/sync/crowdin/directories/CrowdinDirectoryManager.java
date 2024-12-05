package ua.wyverno.sync.crowdin.directories;

import com.crowdin.client.sourcefiles.model.Directory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.DirectoryEditQuery;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit.PatchDirRequestBuilder;

import java.util.List;
import java.util.Objects;

@Component
public class CrowdinDirectoryManager {
    private final static Logger logger = LoggerFactory.getLogger(CrowdinDirectoryManager.class);

    private final CrowdinService crowdinService;
    private final long projectId;

    private final ObjectMapper mapper;

    @Autowired
    public CrowdinDirectoryManager(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.projectId = configLoader.getCoreConfig().getProjectID();

        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Створює директорію у Кроудіні
     * @param directoryId айді директорії де має бути розташована директорія, якщо це коренева директорія має бути null
     * @param directoryName ім'я директорії
     * @param directoryTitle заголовок директорії може бути null, щоб не встановлювати загаловок
     * @return {@link Directory} створена директорія на Кроудіні
     */
    protected Directory createDirectory(@Nullable Long directoryId, String directoryName, @Nullable String directoryTitle) {
        Objects.requireNonNull(directoryName, "Directory name can't be null!");
        logger.debug("Creating directories: {}, Title: {}, in directoryId: {}", directoryName, directoryTitle, directoryId);
        return this.crowdinService.directories() // Створюємо директорію у Кроудіні
                .createDirectory(this.projectId)
                .directoryID(directoryId)
                .name(directoryName)
                .title(directoryTitle)
                .execute();
    }

    protected Directory editDirectory(Long directoryId, List<PatchDirRequestBuilder> patchDirRequests) {
        try {
            logger.debug("Edit directory with request: {}",this.mapper.writeValueAsString(patchDirRequests));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DirectoryEditQuery query = this.crowdinService.directories()
                .editDirectory(this.projectId)
                .directoryID(directoryId);
        patchDirRequests.forEach(query::addPatchRequest);
        return query.execute();
    }
}
