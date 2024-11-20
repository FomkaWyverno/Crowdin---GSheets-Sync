package ua.wyverno.crowdin.api.sourcestrings;

import ua.wyverno.crowdin.api.sourcestrings.queries.StringsAddQuery;

public interface StringsAPI {

    /**
     * Створює запит до Crowdin API - Add String<br/><br/>
     * Обовязкові параметри при створенні запиту -<br/>
     * {@link StringsAddQuery#text(String text)}<br/>
     * {@link StringsAddQuery#fileID(long fileID)}<br/><br/>
     * @param projectID айді проєкту де потрібно додати вихідний рядок
     * @return {@link StringsAddQuery}
     */
    StringsAddQuery add(long projectID);
}
