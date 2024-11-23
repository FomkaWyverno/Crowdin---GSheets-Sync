package ua.wyverno.crowdin.api.sourcestrings.queries.batch;

import com.crowdin.client.core.model.PatchRequest;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcestrings.SourceStringsApi;
import com.crowdin.client.sourcestrings.model.SourceString;
import ua.wyverno.crowdin.api.Query;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.EditBatchStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.enums.PathEditString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Абстракція запиту до <a href="https://support.crowdin.com/developer/api/v2/#tag/Source-Strings/operation/api.projects.strings.batchPatch">Crowdin API String Batch Operations</a>
 */
public class StringsBatchQuery implements Query<List<SourceString>> {
    private final SourceStringsApi sourceStringsApi;
    private final long projectID;
    private final List<PatchRequest> requestList;
    public StringsBatchQuery(SourceStringsApi sourceStringsApi, long projectID) {
        this.sourceStringsApi = sourceStringsApi;
        this.projectID = projectID;
        this.requestList = new ArrayList<>();
    }

    protected void addPatchRequest(PatchRequest patchRequest) {
        this.requestList.add(patchRequest);
    }

    /**
     * Створює операцію add
     * @param addStringRequest тіло запиту додавання рядка
     * @return {@link StringsBatchQuery}
     * @throws IllegalArgumentException у випадку якщо addStringRequest переданий як null
     */
    public StringsBatchQuery addPatch(AddStringRequestBuilder addStringRequest) {
        Objects.requireNonNull(addStringRequest,"addStringRequest cannot be null");
        this.requestList.add(new StringsAddPatch(addStringRequest).getPatchRequest());
        return this;
    }

    /**
     * Створює операцію replace
     * @param editBatchStringRequest тіло запиту редагування рядка<br/><br/>
     *                               Обов'язкові параметри -<br/>
     *                               {@link EditBatchStringRequestBuilder#stringID(Long)} - айді рядка який має бути відредагований<br/>
     *                               {@link EditBatchStringRequestBuilder#path(PathEditString)} - що саме редагувати потрібно в цьому рядку<br/>
     *                               {@link EditBatchStringRequestBuilder#value(Object)} - нове значення
     * @return {@link StringsBatchQuery}
     */
    public StringsBatchQuery replacePatch(EditBatchStringRequestBuilder editBatchStringRequest) {
        Objects.requireNonNull(editBatchStringRequest, "editBatchStringRequest cannot be null");
        this.requestList.add(editBatchStringRequest.build());
        return this;
    }

    @Override
    public List<SourceString> execute() {
        return this.sourceStringsApi.stringBatchOperations(this.projectID, this.requestList)
                .getData()
                .stream()
                .map(ResponseObject::getData)
                .toList();
    }
}
