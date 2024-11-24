package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.core.http.HttpClient;
import com.crowdin.client.core.http.HttpRequestConfig;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.crowdin.client.stringtranslations.model.LanguageTranslationsResponseList;
import com.crowdin.client.stringtranslations.model.LanguageTranslationsResponseObject;
import ua.wyverno.crowdin.api.ListQuery;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Note: For instant translation delivery to your mobile, web, server, or desktop apps, it is recommended to use <a href="https://support.crowdin.com/content-delivery/">OTA</a>.
 */
public class StringTranslationLanguageListQuery extends ListQuery<LanguageTranslations, StringTranslationLanguageListQuery> {
    private final HttpClient crowdinHttpClient;
    private final String crowdinBaseApiURL;
    private final long projectID;
    private String languageId;
    private String orderBy;
    private String stringIds;
    private String labelIds;
    private Long fileId;
    private Long branchId;
    private Long directoryId;
    private Integer approvedOnly;
    private String croql;
    private Integer denormalizePlaceholders;
    public StringTranslationLanguageListQuery(HttpClient crowdinHttpClient, String crowdinBastApiURL, long projectID) {
        this.crowdinHttpClient = crowdinHttpClient;
        this.crowdinBaseApiURL = crowdinBastApiURL;
        this.projectID = projectID;
    }

    /**
     * @param languageId Language Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.get">Project Target Languages</a>
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery languageId(String languageId) {
        this.languageId = languageId;
        return this;
    }

    /**
     * @param orderBy Default: "stringId"<br/>
     * Enum: "text" "stringId" "translationId" "createdAt"<br/>
     * Example: orderBy=createdAt desc,text<br/>
     * Read more about <a href="https://support.crowdin.com/developer/api/v2/#section/Introduction/Sorting">sorting rules<a/>
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * @param stringIds Example: stringIds=1,2,3,4,5<br/>
     * Filter translations by stringIds. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.strings.getMany">List Strings</a>
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery stringIds(String stringIds) {
        this.stringIds = stringIds;
        return this;
    }

    /**
     * @param labelIds Example: labelIds=1,2,3,4,5<br/>
     * Filter translations by labelIds. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.labels.getMany">List Labels</a>
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery labelIds(String labelIds) {
        this.labelIds = labelIds;
        return this;
    }

    /**
     * @param fileId Filter translations by fileId. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.files.getMany">List Files</a><br/>
     * <br/>
     * Note: Can't be used with branchId or directoryId in the same request
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery fileId(Long fileId) {
        this.fileId = fileId;
        return this;
    }

    /**
     * @param branchId Branch Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.branches.getMany">List Branches<a/><br/>
     * Note: Can't be used with fileId or directoryId in the same request<br/>
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery branchId(Long branchId) {
        this.branchId = branchId;
        return this;
    }

    /**
     * @param directoryId Directory Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.directories.getMany">List Directories</a><br/>
     * <br/>
     * Note: Can't be used with fileId or branchId in same request
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery directoryId(Long directoryId) {
        this.directoryId = directoryId;
        return this;
    }

    /**
     * @param approvedOnly Default: false<br/>
     * Only approved translations
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery approvedOnly(Boolean approvedOnly) {
        if (approvedOnly == null) {
            this.approvedOnly = null;
            return this;
        }
        this.approvedOnly = approvedOnly ? 1 : 0;
        return this;
    }

    /**
     * @param croql Filter translations by <a href="https://developer.crowdin.com/croql/">CroQL<a/><br/>
     * <br/>
     * Note: Can't be used with stringIds, labelIds, fileId or approvedOnly in same request
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery croql(String croql) {
        this.croql = URLEncoder.encode(croql, StandardCharsets.UTF_8);
        return this;
    }

    /**
     * @param denormalizePlaceholders Default: false<br/>
     * Enable denormalize placeholders
     * @return {@link StringTranslationLanguageListQuery}
     */
    public StringTranslationLanguageListQuery denormalizePlaceholders(Boolean denormalizePlaceholders) {
        if (denormalizePlaceholders == null) {
            this.denormalizePlaceholders = null;
            return this;
        }
        this.denormalizePlaceholders = denormalizePlaceholders ? 1 : 0;
        return this;
    }

    @Override
    protected List<LanguageTranslations> fetchFromAPI(int limitAPI, int offset) {
        String builtUrl = String.format("%s/projects/%d/languages/%s/translations", this.crowdinBaseApiURL, this.projectID, this.languageId);
        Map<String, Optional<Object>> queryParams = HttpRequestConfig.buildUrlParams(
                "orderBy", Optional.ofNullable(this.orderBy),
                "stringIds", Optional.ofNullable(this.stringIds),
                "labelIds", Optional.ofNullable(this.labelIds),
                "fileId", Optional.ofNullable(this.fileId),
                "branchId", Optional.ofNullable(this.branchId),
                "directoryId", Optional.ofNullable(this.directoryId),
                "approvedOnly", Optional.ofNullable(this.approvedOnly),
                "croql", Optional.ofNullable(this.croql),
                "denormalizePlaceholders", Optional.ofNullable(this.denormalizePlaceholders),
                "limit", Optional.of(limitAPI),
                "offset", Optional.of(offset)
        );
        LanguageTranslationsResponseList languageTranslationsResponseList = this.crowdinHttpClient.get(builtUrl, new HttpRequestConfig(queryParams), LanguageTranslationsResponseList.class);
        return languageTranslationsResponseList
                .getData()
                .stream()
                .map(LanguageTranslationsResponseObject::getData)
                .toList();
    }


    @Override
    public List<LanguageTranslations> execute() {
        return this.listWithPagination();
    }
}
