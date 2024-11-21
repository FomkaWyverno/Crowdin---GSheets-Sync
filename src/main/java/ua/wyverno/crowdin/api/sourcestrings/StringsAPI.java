package ua.wyverno.crowdin.api.sourcestrings;

import ua.wyverno.crowdin.api.sourcestrings.queries.StringsGetQuery;
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsBatchQuery;
import ua.wyverno.crowdin.api.sourcestrings.queries.StringsAddQuery;

public interface StringsAPI {

    /**
     * Створює запит до Crowdin API - Add String<br/><br/>
     * Обовязкові параметри при створенні запиту -<br/>
     * {@link ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder<StringsAddQuery>#text(String text)}<br/>
     * {@link ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder<StringsAddQuery>#fileID(long fileID)}<br/><br/>
     * @param projectID айді проєкту де потрібно додати вихідний рядок
     * @return {@link StringsAddQuery}
     */
    StringsAddQuery add(long projectID);

    /**
     * Створює запит до Crowdin API - Get String<br/><br/>
     * Обовязкові параметри при створенні запиту -<br/>
     * @param projectID айді проєкту де знаходиться вихідний рядок
     * @return {@link StringsGetQuery}
     */
    StringsGetQuery get(long projectID);

    /**
     * Створює запит до Crowdin API - String Batch Operations<br/><br/>
     *
     * Обовязково потрібно створити, хоча-б один Patch<br/>
     *
     * @param projectID
     * @return {@link StringsBatchQuery}
     */
    StringsBatchQuery batch(long projectID);
}
