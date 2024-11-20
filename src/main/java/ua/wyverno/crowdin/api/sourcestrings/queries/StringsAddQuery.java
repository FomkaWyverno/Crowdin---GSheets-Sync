package ua.wyverno.crowdin.api.sourcestrings.queries;

import com.crowdin.client.sourcestrings.SourceStringsApi;
import com.crowdin.client.sourcestrings.model.AddSourceStringRequest;
import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.Query;
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.AddSourceStringRequestBuilder;

import java.util.List;

public class StringsAddQuery implements Query<SourceString> {
    private final SourceStringsApi sourceStringsApi;
    private long projectID;
    private AddSourceStringRequestBuilder requestBuilder;
    public StringsAddQuery(SourceStringsApi sourceStringsApi, long projectID) {
        this.sourceStringsApi = sourceStringsApi;
        this.projectID = projectID;
    }

    public StringsAddQuery text(String text) {
        this.requestBuilder.text(text);
        return this;
    }

    public StringsAddQuery fileID(long fileID) {
        this.requestBuilder.fileID(fileID);
        return this;
    }

    public StringsAddQuery identifier(String identifier) {
        this.requestBuilder.identifier(identifier);
        return this;
    }

    public StringsAddQuery context(String context) {
        this.requestBuilder.context(context);
        return this;
    }

    public StringsAddQuery hidden(Boolean hidden) {
        this.requestBuilder.hidden(hidden);
        return this;
    }

    public StringsAddQuery maxLength(Integer maxLength) {
        this.requestBuilder.maxLength(maxLength);
        return this;
    }

    public StringsAddQuery labelIds(List<Long> labelIds) {
        this.requestBuilder.labelIds(labelIds);
        return this;
    }

    @Override
    public SourceString execute() {
        AddSourceStringRequest addSourceStringRequest = this.requestBuilder.build();
        return this.sourceStringsApi.addSourceString(this.projectID, addSourceStringRequest).getData();
    }
}
