package ua.wyverno.crowdin.api.stingtranslation.queries;

import com.crowdin.client.core.http.HttpClient;
import com.crowdin.client.core.http.HttpRequestConfig;
import com.crowdin.client.stringtranslations.model.Approval;
import com.crowdin.client.stringtranslations.model.ApprovalResponseList;
import com.crowdin.client.stringtranslations.model.ApprovalResponseObject;
import ua.wyverno.crowdin.api.ListQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Note: Either translationId OR fileId OR labelIds OR excludeLabelIds with languageId OR stringId with languageId are required
 */
public class StringTranslationApprovalsListQuery extends ListQuery<Approval, StringTranslationApprovalsListQuery> {
    private final HttpClient crowdinHttpClient;
    private final String crowdinBaseApiURL;
    private final long projectID;
    private String orderBy;
    private Long fileId;
    private Long stringId;
    private String languageId;
    private Long translationId;
    private String labelIds;
    private String excludeLabelIds;
    public StringTranslationApprovalsListQuery(HttpClient crowdinHttpClient, String crowdinBaseApiURL, long projectID) {
        this.crowdinHttpClient = crowdinHttpClient;
        this.crowdinBaseApiURL = crowdinBaseApiURL;
        this.projectID = projectID;
    }

    /**
     * @param orderBy Default: "id"<br/>
     * Enum: "id" "createdAt"<br/>
     * Example: orderBy=createdAt desc,id<br/>
     * Read more about <a href="https://support.crowdin.com/developer/api/v2/#section/Introduction/Sorting">sorting rules<a/>
     * @return {@link StringTranslationApprovalsListQuery}
     */
    public StringTranslationApprovalsListQuery orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * @param fileId File Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.files.getMany">List Files</a><br/>
     * Note: Must be used together with languageId
     * @return {@link StringTranslationApprovalsListQuery}
     */
    public StringTranslationApprovalsListQuery fileId(Long fileId) {
        this.fileId = fileId;
        return this;
    }

    /**
     * @param labelIds Example: labelIds=1,2,3,4,5<br/>
     * Label Identifiers. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.labels.getMany">List Labels</a>
     * @return {@link StringTranslationApprovalsListQuery}
     */
    public StringTranslationApprovalsListQuery labelIds(String labelIds) {
        this.labelIds = labelIds;
        return this;
    }

    /**
     * @param excludeLabelIds Label Identifiers. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.labels.getMany">List Labels</a>
     * @return {@link StringTranslationApprovalsListQuery}
     */
    public StringTranslationApprovalsListQuery excludeLabelIds(String excludeLabelIds) {
        this.excludeLabelIds = excludeLabelIds;
        return this;
    }

    /**
     * @param stringId String Identifier. Get via
     * <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.strings.getMany">List Strings<a/><br/>
     * Note: Must be used together with languageId
     * @return {@link StringTranslationApprovalsListQuery}
     */
    public StringTranslationApprovalsListQuery stringId(Long stringId) {
        this.stringId = stringId;
        return this;
    }

    /**
     * @param languageId Language Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.get">Project Target Languages<a/><br/>
     * Note: Must be used together with stringId or fileId
     * @return {@link StringTranslationApprovalsListQuery}
     */
    public StringTranslationApprovalsListQuery languageId(String languageId) {
        this.languageId = languageId;
        return this;
    }

    /**
     * @param translationId Translation Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.translations.getMany">List String Translations</a><br/>
     * Note: If specified, fileId, stringId and languageId are ignored
     * @return {@link StringTranslationApprovalsListQuery}
     */
    public StringTranslationApprovalsListQuery translationId(Long translationId) {
        this.translationId = translationId;
        return this;
    }

    @Override
    protected List<Approval> fetchFromAPI(int limitAPI, int offset) {
        Map<String, Optional<Object>> queryParams = HttpRequestConfig.buildUrlParams(
                "orderBy", Optional.ofNullable(this.orderBy),
                "fileId", Optional.ofNullable(this.fileId),
                "stringId", Optional.ofNullable(this.stringId),
                "languageId", Optional.ofNullable(this.languageId),
                "translationId", Optional.ofNullable(this.translationId),
                "labelIds", Optional.ofNullable(this.labelIds),
                "excludeLabelIds", Optional.ofNullable(this.excludeLabelIds),
                "limit", Optional.of(limitAPI),
                "offset", Optional.of(offset)
        );
        ApprovalResponseList approvalResponseList = this.crowdinHttpClient.get(this.crowdinBaseApiURL + "/projects/" + this.projectID + "/approvals", new HttpRequestConfig(queryParams), ApprovalResponseList.class);
        return approvalResponseList.getData()
                .stream()
                .map(ApprovalResponseObject::getData)
                .toList();
    }

    @Override
    public List<Approval> execute() {
        return this.listWithPagination();
    }
}
