package ua.wyverno.crowdin.api.stringtranslation;

import ua.wyverno.crowdin.api.sourcestrings.queries.StringsListQuery;
import ua.wyverno.crowdin.api.stringtranslation.queries.*;

public interface StringTranslationAPI {
    /**
     * Створює запит до Crowdin API - List Language Translations<br/><br/>
     * Обов'язкові параметри -<br/>
     * {@link StringTranslationLanguageListQuery#languageId(String languageId)}
     * @param projectID айді проєкта, де потрібно отримати рядки перекладу
     * @return {@link StringsListQuery}
     */
    StringTranslationLanguageListQuery listLanguageTranslations(long projectID);

    /**
     * Створює запит до Crowdin API - List Translations Approvals<br/>
     * - Дає список затверджених перекладів<br/>
     * Примітка: Потрібен або translationId, або fileId, або labelId, або excludeLabelId з languageId, або stringId з languageId
     * @param projectID айді проєкта, де потрібно отримати рядки затвердженого перекладу
     * @return {@link StringTranslationApprovalsListQuery}
     */
    StringTranslationApprovalsListQuery listTranslationApprovals(long projectID);

    /**
     * Створює запит до Crowdin API - List String Translations<br/>
     * Дає список всіх запропонованих перекладів для певного рядка<br/><br/>
     *
     *
     * Обов'язкові параметри -<br/>
     * {@link StringTranslationListQuery#stringId(Long)}<br/>
     * {@link StringTranslationListQuery#languageId(String)}
     * @param projectID айді проєкта, де потрібно отримати рядки перекладу
     * @return {@link StringTranslationListQuery}
     */
    StringTranslationListQuery listTranslation(long projectID);

    /**
     * Створює запит до Crowdin API - Get Translation<br/><br/>
     * Обов'язкові параметри -<br/>
     * {@link StringTranslationGetQuery#translationId(Long)}
     * @param projectID айді проєкта де потрібно взяти цей переклад
     * @return {@link StringTranslationGetQuery}
     */
    StringTranslationGetQuery getTranslation(long projectID);

    /**
     * Створює запит до Crowdin API - Get Approval
     * @param projectID айді проєкта, де потрібно отримати затверджений переклад
     * @return {@link StringTranslationGetApprovalQuery}
     */
    StringTranslationGetApprovalQuery getApproval(long projectID);
}
