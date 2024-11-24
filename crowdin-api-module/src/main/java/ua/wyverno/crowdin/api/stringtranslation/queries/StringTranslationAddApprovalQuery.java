package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.stringtranslations.StringTranslationsApi;
import com.crowdin.client.stringtranslations.model.AddApprovalRequest;
import com.crowdin.client.stringtranslations.model.Approval;
import ua.wyverno.crowdin.api.Query;

public class StringTranslationAddApprovalQuery implements Query<Approval> {
    private final StringTranslationsApi stringTranslationsApi;
    private final long projectID;
    private long translationId;

    public StringTranslationAddApprovalQuery(StringTranslationsApi stringTranslationsApi, long projectID) {
        this.stringTranslationsApi = stringTranslationsApi;
        this.projectID = projectID;
    }

    /**
     * @param translationId Translation Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.translations.getMany">List String Translations</a>
     * @return {@link StringTranslationAddApprovalQuery}
     */
    public StringTranslationAddApprovalQuery translationId(long translationId) {
        this.translationId = translationId;
        return this;
    }

    @Override
    public Approval execute() {
        AddApprovalRequest request = new AddApprovalRequest();
        request.setTranslationId(this.translationId);
        return this.stringTranslationsApi.addApproval(this.projectID, request).getData();
    }
}
