package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.core.http.HttpClient;
import com.crowdin.client.core.http.HttpRequestConfig;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import com.crowdin.client.stringtranslations.model.StringTranslationResponseObject;
import ua.wyverno.crowdin.api.Query;

import java.util.Map;
import java.util.Optional;

public class StringTranslationGetQuery implements Query<StringTranslation> {
    private final HttpClient crowdinHttpClient;
    private final String crowdinBaseApiURL;
    private final long projectID;
    private Long translationId;
    private Integer denormalizePlaceholders;
    public StringTranslationGetQuery(HttpClient crowdinHttpClient, String crowdinBaseApiURL, long projectID) {
        this.crowdinHttpClient = crowdinHttpClient;
        this.crowdinBaseApiURL = crowdinBaseApiURL;
        this.projectID = projectID;
    }

    /**
     * @param translationId Translation Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.translations.getMany">List String Translations<a/>
     * @return {@link StringTranslationGetQuery}
     */
    public StringTranslationGetQuery translationId(Long translationId) {
        this.translationId = translationId;
        return this;
    }

    /**
     * @param denormalizePlaceholders Default: false<br/>
     * Enable denormalize placeholders
     * @return {@link StringTranslationGetQuery}
     */
    public StringTranslationGetQuery denormalizePlaceholders(Boolean denormalizePlaceholders) {
        if (denormalizePlaceholders == null) {
            this.denormalizePlaceholders = null;
            return this;
        }
        this.denormalizePlaceholders = denormalizePlaceholders ? 1 : 0;
        return this;
    }

    @Override
    public StringTranslation execute() {
        String builtURL = String.format("%s/projects/%s/translations/%s", this.crowdinBaseApiURL, this.projectID, this.translationId);
        Map<String, Optional<Object>> queryParams = HttpRequestConfig.buildUrlParams(
                "denormalizePlaceholders", Optional.ofNullable(this.denormalizePlaceholders));
        StringTranslationResponseObject response = this.crowdinHttpClient.get(builtURL, new HttpRequestConfig(queryParams), StringTranslationResponseObject.class);
        return response.getData();
    }
}
