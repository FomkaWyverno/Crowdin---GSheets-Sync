package ua.wyverno.crowdin.api.sourcestrings.queries;

import com.crowdin.client.core.model.PatchRequest;
import com.crowdin.client.sourcestrings.SourceStringsApi;
import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.Query;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.EditStringRequestBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StringsEditQuery implements Query<SourceString> {
    private final SourceStringsApi sourceStringsApi;
    private final long projectID;
    private Long stringID;
    private final List<PatchRequest> requestList = new ArrayList<>();
    public StringsEditQuery(SourceStringsApi sourceStringsApi, long projectID) {
        this.sourceStringsApi = sourceStringsApi;
        this.projectID = projectID;
    }

    /**
     * String Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.strings.getMany">List Strings</a>
     * @param stringID айді вихідного рядка який потрібно редагувати
     * @return {@link StringsEditQuery}
     */
    public StringsEditQuery stringID(Long stringID) {
        this.stringID = stringID;
        return this;
    }

    /**
     * Приймає, та кладе до списку змін вихідного рядка, будівельника який створює PatchRequest для змін вихідного рядка
     * @param editStringRequest Будівельник для запиту змін
     * @return {@link StringsEditQuery}
     */
    public StringsEditQuery putEditStringRequest(EditStringRequestBuilder editStringRequest) {
        Objects.requireNonNull(editStringRequest, "editStringRequest cannot be null");
        this.requestList.add(editStringRequest.build());
        return this;
    }

    @Override
    public SourceString execute() {
        return this.sourceStringsApi.editSourceString(this.projectID, this.stringID, this.requestList).getData();
    }
}
