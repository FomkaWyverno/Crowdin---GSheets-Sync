package ua.wyverno.crowdin.api.stingtranslation;

import com.crowdin.client.stringtranslations.StringTranslationsApi;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringTranslationLanguageListQuery;

@Component
public class StingsTranslationApiImpl implements StringTranslationAPI {
    private final StringTranslationsApi stringTranslationsApi;

    @Autowired
    public StingsTranslationApiImpl(CrowdinApiClient crowdinApiClient) {
        this.stringTranslationsApi = crowdinApiClient.getCrowdinClient().getStringTranslationsApi();
    }

    @Override
    public StringTranslationLanguageListQuery listLanguageTranslations(long projectID) {
        return new StringTranslationLanguageListQuery(this.stringTranslationsApi, projectID);
    }
}
