package ua.wyverno.crowdin.api.stingtranslation;

import ua.wyverno.crowdin.api.sourcestrings.queries.StringsListQuery;
import ua.wyverno.crowdin.api.stingtranslation.queries.StringTranslationLanguageListQuery;

public interface StringTranslationAPI {
    /**
     * Створює запит до Crowdin API - List Language Translations<br/><br/>
     * @param projectID айді проєкта, де потрібно отримати рядки перекладу
     * @return {@link StringsListQuery}
     */
    StringTranslationLanguageListQuery listLanguageTranslations(long projectID);
}
