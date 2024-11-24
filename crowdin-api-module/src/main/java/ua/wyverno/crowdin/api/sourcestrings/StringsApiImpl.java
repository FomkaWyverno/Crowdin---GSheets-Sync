package ua.wyverno.crowdin.api.sourcestrings;

import com.crowdin.client.sourcestrings.SourceStringsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.sourcestrings.queries.*;
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsBatchQuery;

@Component
public class StringsApiImpl implements StringsAPI {
    private final SourceStringsApi sourceStringsApi;
    @Autowired
    public StringsApiImpl(CrowdinApiClient crowdinApiClient) {
        this.sourceStringsApi = crowdinApiClient.getCrowdinClient().getSourceStringsApi();
    }


    @Override
    public StringsAddQuery add(long projectID) {
        return new StringsAddQuery(this.sourceStringsApi, projectID);
    }

    @Override
    public StringsGetQuery get(long projectID) {
        return new StringsGetQuery(this.sourceStringsApi, projectID);
    }

    @Override
    public StringsListQuery list(long projectID) {
        return new StringsListQuery(this.sourceStringsApi, projectID);
    }

    @Override
    public StringsEditQuery edit(long projectID) {
        return new StringsEditQuery(this.sourceStringsApi, projectID);
    }

    @Override
    public StringsDeleteQuery delete(long projectID) {
        return new StringsDeleteQuery(this.sourceStringsApi, projectID);
    }

    @Override
    public StringsBatchQuery batch(long projectID) {
        return new StringsBatchQuery(this.sourceStringsApi, projectID);
    }
}
