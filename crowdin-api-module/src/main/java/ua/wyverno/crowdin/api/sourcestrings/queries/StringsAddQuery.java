package ua.wyverno.crowdin.api.sourcestrings.queries;

import com.crowdin.client.sourcestrings.SourceStringsApi;
import com.crowdin.client.sourcestrings.model.AddSourceStringRequest;
import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.Query;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;

import java.util.Objects;

public class StringsAddQuery implements Query<SourceString> {
    private final SourceStringsApi sourceStringsApi;
    private final long projectID;
    private AddStringRequestBuilder requestBuilder;
    public StringsAddQuery(SourceStringsApi sourceStringsApi, long projectID) {
        this.sourceStringsApi = sourceStringsApi;
        this.projectID = projectID;
    }

    public StringsAddQuery request(AddStringRequestBuilder addStringRequestBuilder) {
        Objects.requireNonNull(addStringRequestBuilder, "addStringRequest cannot be null");
        this.requestBuilder = addStringRequestBuilder;
        return this;
    }

    @Override
    public SourceString execute() {
        AddSourceStringRequest addSourceStringRequest = this.requestBuilder.build();
        return this.sourceStringsApi.addSourceString(this.projectID, addSourceStringRequest).getData();
    }
}
