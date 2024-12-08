package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcestrings.model.SourceString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsBatchQuery;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.EditBatchStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.RemoveBatchStringRequestBuilder;

import java.util.Collections;
import java.util.List;

@Component
public class CrowdinSourceStringsManager {
    private final static Logger logger = LoggerFactory.getLogger(CrowdinSourceStringsManager.class);

    private final CrowdinService crowdinService;
    private final long projectId;

    @Autowired
    public CrowdinSourceStringsManager(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.projectId = configLoader.getCoreConfig().getProjectID();
    }

    public List<SourceString> listSourceStrings() {
        return this.crowdinService.sourceStrings()
                .list(this.projectId)
                .execute();
    }

    public List<SourceString> batchSourceStrings(List<AddStringRequestBuilder> requestsAdd,
                                                 List<RemoveBatchStringRequestBuilder> requestsRemove,
                                                 List<EditBatchStringRequestBuilder> requestsEdit) {
        if (!requestsAdd.isEmpty() || !requestsRemove.isEmpty() || !requestsEdit.isEmpty()) {
            logger.info("String Batch Query starting...");
            StringsBatchQuery query = this.crowdinService.sourceStrings()
                    .batch(this.projectId);

           requestsAdd.forEach(query::addPatch);
           requestsRemove.forEach(query::removePatch);
           requestsEdit.forEach(query::replacePatch);

            return query.execute();
        }
        logger.info("Strings Batch Query not start because of requests empty.");
        return Collections.emptyList();
    }
}
