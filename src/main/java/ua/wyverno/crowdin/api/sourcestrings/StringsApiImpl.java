package ua.wyverno.crowdin.api.sourcestrings;

import com.crowdin.client.sourcestrings.SourceStringsApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.sourcestrings.queries.StringsAddQuery;

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
}
