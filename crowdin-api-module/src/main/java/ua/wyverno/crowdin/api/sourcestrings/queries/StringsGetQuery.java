package ua.wyverno.crowdin.api.sourcestrings.queries;

import com.crowdin.client.sourcestrings.SourceStringsApi;
import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.Query;

public class StringsGetQuery implements Query<SourceString> {
    private final SourceStringsApi sourceStringsApi;
    private final long projectID;
    private long stringID;
    public StringsGetQuery(SourceStringsApi sourceStringsApi, long projectID) {
        this.sourceStringsApi = sourceStringsApi;
        this.projectID = projectID;
    }

    /**
     * @param stringID айді вихідного рядка, який потрібно отримати
     * @return {@link StringsGetQuery}
     */
    public StringsGetQuery stringID(long stringID) {
        this.stringID = stringID;
        return this;
    }

    @Override
    public SourceString execute() {
        return this.sourceStringsApi.getSourceString(this.projectID, this.stringID).getData();
    }
}
