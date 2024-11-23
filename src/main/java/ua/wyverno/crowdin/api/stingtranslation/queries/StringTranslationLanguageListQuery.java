package ua.wyverno.crowdin.api.stingtranslation.queries;

import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.stringtranslations.StringTranslationsApi;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import ua.wyverno.crowdin.api.ListQuery;

import java.util.List;

public class StringTranslationLanguageListQuery extends ListQuery<LanguageTranslations, StringTranslationLanguageListQuery> {
    private final StringTranslationsApi stringTranslationsApi;
    private final long projectID;
    public StringTranslationLanguageListQuery(StringTranslationsApi stringTranslationsApi, long projectID) {
        this.stringTranslationsApi = stringTranslationsApi;
        this.projectID = projectID;
    }

    @Override
    protected List<LanguageTranslations> fetchFromAPI(int limitAPI, int offset) {
        return null;
    }

    // TODO: 23.11.2024 Написати код для збору листа з перекладами
    @Override
    public List<LanguageTranslations> execute() {
        return this.stringTranslationsApi
                .listLanguageTranslations(this.projectID, "uk", null, null,
                        null, null, null, null, null,null, 0)
                .getData().stream()
                .map(ResponseObject::getData)
                .toList();
    }
}
