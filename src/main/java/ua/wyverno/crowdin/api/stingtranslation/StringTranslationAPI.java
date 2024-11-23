package ua.wyverno.crowdin.api.stingtranslation;

import ua.wyverno.crowdin.api.sourcestrings.queries.StringsListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringsTranslationApprovalsListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringsTranslationLanguageListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringsTranslationListQuery;

public interface StringTranslationAPI {
    /**
     * Створює запит до Crowdin API - List Language Translations<br/><br/>
     * Обов'язкові параметри -<br/>
     * {@link StringsTranslationLanguageListQuery#languageId(String languageId)}
     * @param projectID айді проєкта, де потрібно отримати рядки перекладу
     * @return {@link StringsListQuery}
     */
    StringsTranslationLanguageListQuery listLanguageTranslations(long projectID);

    /**
     * Створює запит до Crowdin API - List Translations Approvals<br/>
     * - Дає список затверджених перекладів<br/>
     * Примітка: Потрібен або translationId, або fileId, або labelId, або excludeLabelId з languageId, або stringId з languageId
     * @param projectID айді проєкта, де потрібно отримати рядки затвердженого перекладу
     * @return {@link StringsTranslationApprovalsListQuery}
     */
    StringsTranslationApprovalsListQuery listTranslationApprovals(long projectID);

    /**
     * Створює запит до Crowdin API - List String Translations<br/>
     * Дає список всіх запропонованих перекладів для певного рядка<br/><br/>
     *
     *
     * Обов'язкові параметри -<br/>
     * {@link StringsTranslationListQuery#stringId(Long)}<br/>
     * {@link StringsTranslationListQuery#languageId(String)}
     * @param projectID айді проєкта, де потрібно отримати рядки перекладу
     * @return {@link StringsTranslationListQuery}
     */
    StringsTranslationListQuery listTranslation(long projectID);
}
