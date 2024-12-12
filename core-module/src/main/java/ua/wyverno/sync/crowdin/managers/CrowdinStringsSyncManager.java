package ua.wyverno.sync.crowdin.managers;

import com.crowdin.client.sourcestrings.model.SourceString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;

import java.util.List;

@Component
public class CrowdinStringsSyncManager {
    private final static Logger logger = LoggerFactory.getLogger(CrowdinStringsSyncManager.class);

    private final CrowdinService crowdinService;
    private final long projectId;

    @Autowired
    public CrowdinStringsSyncManager(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.projectId = configLoader.getCoreConfig().getProjectID();
    }

    /**
     * Отримання всіх вихідних рядків
     *
     * @return Лист з усіма вихідними рядками
     */
    public List<SourceString> getListSourceString() {
        logger.trace("Getting list source strings from Crowdin Project.");
        return this.crowdinService.sourceStrings()
                .list(this.projectId)
                .execute();
    }
}
