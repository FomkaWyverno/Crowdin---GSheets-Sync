package ua.wyverno.crowdin.api.stingtranslation;

import ua.wyverno.crowdin.api.sourcestrings.queries.StringsListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringTranslationApprovalsListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringTranslationLanguageListQuery;

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
     * Note: Either translationId OR fileId OR labelIds OR excludeLabelIds with languageId OR stringId with languageId are required
     * @param projectID айді проєкта, де потрібно отримати рядки затвердженого перекладу
     * @return {@link StringTranslationApprovalsListQuery}
     */
    StringTranslationApprovalsListQuery listTranslationApprovals(long projectID);
}
