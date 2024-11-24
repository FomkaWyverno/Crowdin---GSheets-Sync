package ua.wyverno.crowdin.api.stringtranslation.queries;

import com.crowdin.client.stringtranslations.StringTranslationsApi;
import com.crowdin.client.stringtranslations.model.Approval;
import ua.wyverno.crowdin.api.Query;

public class StringTranslationGetApprovalQuery implements Query<Approval> {
    private final StringTranslationsApi stringTranslationsApi;
    private final long projectID;
    private Long approvalId;
    public StringTranslationGetApprovalQuery(StringTranslationsApi stringTranslationsApi, long projectID) {
        this.stringTranslationsApi = stringTranslationsApi;
        this.projectID = projectID;
    }

    /**
     * @param approvalId Approval Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.approvals.getMany">List Translation Approvals</a>
     * @return {@link StringTranslationGetApprovalQuery}
     */
    public StringTranslationGetApprovalQuery approvalId(Long approvalId) {
        this.approvalId = approvalId;
        return this;
    }

    @Override
    public Approval execute() {
        return this.stringTranslationsApi.getApproval(this.projectID, this.approvalId).getData();
    }
}
