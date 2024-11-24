package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.stringtranslations.StringTranslationsApi;
import com.crowdin.client.stringtranslations.model.AddStringTranslationRequest;
import com.crowdin.client.stringtranslations.model.PluralCategoryName;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import ua.wyverno.crowdin.api.Query;

public class StringTranslationAddQuery implements Query<StringTranslation> {
    private final StringTranslationsApi stringTranslationsApi;
    private final long projectID;
    private Long stringId;
    private String languageId;
    private String text;
    private PluralCategoryName pluralCategoryName;

    public StringTranslationAddQuery(StringTranslationsApi stringTranslationsApi, long projectID) {
        this.stringTranslationsApi = stringTranslationsApi;
        this.projectID = projectID;
    }

    /**
     * @param stringId String Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.strings.getMany">List Strings</a>
     * @return {@link StringTranslationAddQuery}
     */
    public StringTranslationAddQuery stringId(Long stringId) {
        this.stringId = stringId;
        return this;
    }

    /**
     * @param languageId Language Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.get">Project Target Languages</a>
     * @return {@link StringTranslationAddQuery}
     */
    public StringTranslationAddQuery languageId(String languageId) {
        this.languageId = languageId;
        return this;
    }

    /**
     * @param text Translation text
     * @return {@link StringTranslationAddQuery}
     */
    public StringTranslationAddQuery text(String text) {
        this.text = text;
        return this;
    }

    /**
     * @param pluralCategoryName Plural form. Acceptable keys are zero, one, two, few, many, and other<br/><br/>
     * Note: Will be saved only if the source string has plurals and pluralCategoryName is equal to the one available for the language you add translations to
     * @return {@link StringTranslationAddQuery}
     */
    public StringTranslationAddQuery pluralCategoryName(PluralCategoryName pluralCategoryName) {
        this.pluralCategoryName = pluralCategoryName;
        return this;
    }

    @Override
    public StringTranslation execute() {
        AddStringTranslationRequest request = new AddStringTranslationRequest();
        request.setStringId(this.stringId);
        request.setLanguageId(this.languageId);
        request.setText(this.text);
        return this.stringTranslationsApi.addTranslation(this.projectID, request).getData();
    }
}
