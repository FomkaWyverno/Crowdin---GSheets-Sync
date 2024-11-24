package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.stringtranslations.StringTranslationsApi;
import ua.wyverno.crowdin.api.Query;

public class StringTranslationDeleteTranslationQuery implements Query<Void> {
    private final StringTranslationsApi stringTranslationsApi;
    private final long projectID;
    private Long translationId;
    public StringTranslationDeleteTranslationQuery(StringTranslationsApi stringTranslationsApi, long projectID) {
        this.stringTranslationsApi = stringTranslationsApi;
        this.projectID = projectID;
    }

    /**
     * @param translationId Translation Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.translations.getMany">List String Translations</a>
     * @return {@link StringTranslationDeleteTranslationQuery}
     */
    public StringTranslationDeleteTranslationQuery translationId(Long translationId) {
        this.translationId = translationId;
        return this;
    }

    @Override
    public Void execute() {
        this.stringTranslationsApi.deleteStringTranslation(this.projectID, this.translationId);
        return null;
    }
}
