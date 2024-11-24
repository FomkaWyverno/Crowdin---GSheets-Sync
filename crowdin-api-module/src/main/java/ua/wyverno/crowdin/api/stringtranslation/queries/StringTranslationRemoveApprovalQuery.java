package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.stringtranslations.StringTranslationsApi;
import ua.wyverno.crowdin.api.Query;

public class StringTranslationRemoveApprovalQuery implements Query<Void> {
    private final StringTranslationsApi stringTranslationsApi;
    private final long projectID;
    private Long approvalId;
    public StringTranslationRemoveApprovalQuery(StringTranslationsApi stringTranslationsApi, long projectID) {
        this.stringTranslationsApi = stringTranslationsApi;
        this.projectID = projectID;
    }

    /**
     * @param approvalId Approval Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.approvals.getMany">List Translation Approvals</a>
     * @return {@link StringTranslationRemoveApprovalQuery}
     */
    public StringTranslationRemoveApprovalQuery approvalId(Long approvalId) {
        this.approvalId = approvalId;
        return this;
    }

    @Override
    public Void execute() {
        this.stringTranslationsApi.removeApproval(this.projectID, this.approvalId);
        return null;
    }
}
