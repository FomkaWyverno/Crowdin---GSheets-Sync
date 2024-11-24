package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.core.http.HttpClient;
import com.crowdin.client.core.http.HttpRequestConfig;
import ua.wyverno.crowdin.api.Query;

import java.util.Map;
import java.util.Optional;

public class StringTranslationDeleteStringTranslationsQuery implements Query<Void> {
    private final HttpClient crowdinHttpClient;
    private final String crowdinBaseApiURL;
    private final long projectID;

    private Long stringID;
    private String languageID;
    public StringTranslationDeleteStringTranslationsQuery(HttpClient crowdinHttpClient, String crowdinBaseApiURL, long projectID) {
        this.crowdinHttpClient = crowdinHttpClient;
        this.crowdinBaseApiURL = crowdinBaseApiURL;
        this.projectID = projectID;
    }

    /**
     * @param stringID Example: stringId=2<br/>
     * String Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.strings.getMany">List Strings</a>
     * @return {@link StringTranslationDeleteStringTranslationsQuery}
     */
    public StringTranslationDeleteStringTranslationsQuery stringID(Long stringID) {
        this.stringID = stringID;
        return this;
    }

    /**
     * @param languageID Language Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.get">Project Target Languages</a>
     * @return {@link StringTranslationDeleteStringTranslationsQuery}
     */
    public StringTranslationDeleteStringTranslationsQuery languageID(String languageID) {
        this.languageID = languageID;
        return this;
    }

    @Override
    public Void execute() {
        Map<String, Optional<Object>> queryParams = HttpRequestConfig.buildUrlParams(
                "stringId", Optional.ofNullable(this.stringID),
                "languageId", Optional.ofNullable(this.languageID));
        this.crowdinHttpClient.delete(this.crowdinBaseApiURL + "/projects/"+this.projectID+"/translations", new HttpRequestConfig(queryParams), Void.class);
        return null;
    }
}
