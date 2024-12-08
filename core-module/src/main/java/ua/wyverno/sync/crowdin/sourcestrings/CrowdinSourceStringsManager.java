package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcestrings.model.SourceString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsBatchQuery;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Queue;

@Component
public class CrowdinSourceStringsManager {
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

    public List<SourceString> createBatchSourceStrings(List<AddStringRequestBuilder> addStringList) {
        if (!addStringList.isEmpty()) {
            StringsBatchQuery query = this.crowdinService.sourceStrings()
                    .batch(this.projectId);

           addStringList.forEach(query::addPatch);
            return query.execute();
        }
        return Collections.emptyList();
    }
}
