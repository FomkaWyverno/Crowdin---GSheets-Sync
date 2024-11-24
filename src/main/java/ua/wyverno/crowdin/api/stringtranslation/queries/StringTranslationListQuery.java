package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.core.http.HttpClient;
import com.crowdin.client.core.http.HttpRequestConfig;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import com.crowdin.client.stringtranslations.model.StringTranslationResponseList;
import com.crowdin.client.stringtranslations.model.StringTranslationResponseObject;
import ua.wyverno.crowdin.api.ListQuery;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class StringTranslationListQuery extends ListQuery<StringTranslation, StringTranslationListQuery> {
    private final HttpClient crowdinHttpClient;
    private final String crowdinBaseApiURL;

    private final long projectID;
    private Long stringId;
    private String languageId;
    private String orderBy;
    private Integer denormalizePlaceholders;
    public StringTranslationListQuery(HttpClient crowdinHttpClient, String crowdinBaseApiURL, long projectID) {
        this.crowdinHttpClient = crowdinHttpClient;
        this.crowdinBaseApiURL = crowdinBaseApiURL;
        this.projectID = projectID;
    }

    /**
     * @param stringId String Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.strings.getMany">List Strings<a/>
     * @return {@link StringTranslationListQuery}
     */
    public StringTranslationListQuery stringId(Long stringId) {
        this.stringId = stringId;
        return this;
    }

    /**
     * @param languageId Language Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.get">Project Target Languages<a/>
     * @return {@link StringTranslationListQuery}
     */
    public StringTranslationListQuery languageId(String languageId) {
        this.languageId = languageId;
        return this;
    }

    /**
     * @param orderBy Default: "id"<br/>
     * Enum: "id" "text" "rating" "createdAt"<br/>
     * Example: orderBy=createdAt desc,name,priority<br/>
     * Read more about <a href="https://support.crowdin.com/developer/api/v2/#section/Introduction/Sorting">sorting rules<a/>
     * @return {@link StringTranslationListQuery}
     */
    public StringTranslationListQuery orderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    /**
     * @param denormalizePlaceholders Default: false<br/>
     * Enable denormalize placeholders
     * @return {@link StringTranslationListQuery}
     */
    public StringTranslationListQuery denormalizePlaceholders(Boolean denormalizePlaceholders) {
        if (denormalizePlaceholders == null) {
            this.denormalizePlaceholders = null;
            return this;
        }
        this.denormalizePlaceholders = denormalizePlaceholders ? 1 : 0;
        return this;
    }

    @Override
    protected List<StringTranslation> fetchFromAPI(int limitAPI, int offset) {
        Map<String, Optional<Object>> queryParams = HttpRequestConfig.buildUrlParams(
                "stringId", Optional.ofNullable(this.stringId),
                "languageId", Optional.ofNullable(this.languageId),
                "orderBy",Optional.ofNullable(this.orderBy),
                "denormalizePlaceholders", Optional.ofNullable(this.denormalizePlaceholders),
                "limit", Optional.of(limitAPI),
                "offset", Optional.of(offset)
        );
        StringTranslationResponseList stringTranslationResponseList = this.crowdinHttpClient
                .get(this.crowdinBaseApiURL + "/projects/" + this.projectID + "/translations",
                        new HttpRequestConfig(queryParams), StringTranslationResponseList.class);
        return stringTranslationResponseList.getData().stream()
                .map(StringTranslationResponseObject::getData)
                .toList();
    }

    @Override
    public List<StringTranslation> execute() {
        return this.listWithPagination();
    }
}
