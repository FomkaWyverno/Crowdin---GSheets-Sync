package ua.wyverno.crowdin.api.sourcestrings;

import ua.wyverno.crowdin.api.sourcestrings.queries.*;
import ua.wyverno.crowdin.api.sourcestrings.queries.batch.StringsBatchQuery;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.EditStringRequestBuilder;

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
     * {@link StringsGetQuery#stringID(long stringID)}
     * @param projectID айді проєкту де знаходиться вихідний рядок
     * @return {@link StringsGetQuery}
     */
    StringsGetQuery get(long projectID);

    /**
     * Створює запит до Crowdin API - List Strings<br/><br/>
     * @param projectID айді проєкта, з якого потрібно взяти весь список вихідних рядків
     * @return {@link StringsListQuery}
     */
    StringsListQuery list(long projectID);

    /**
     * Створює запит до Crowdin API - Edit String<br/><br/>
     *
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link StringsEditQuery#putEditStringRequest(EditStringRequestBuilder)} - мінімум хоча б один запит на зміну має бути для виконання цього запиту<br/>
     * {@link StringsEditQuery#stringID(Long stringID)} айді рядка котрий потрібно якось змінити
     * @param projectID айді проєкта, у якому знаходиться рядок, якрий потрібно змінити.
     * @return {@link StringsEditQuery}
     */
    StringsEditQuery edit(long projectID);

    /**
     * Створює запит до Crowdin API - Delete String<br/><br/>
     *
     * Обов'язкові параметри при створенні запиту -<br/>
     * {@link StringsDeleteQuery#stringID(long)} - айді рядка який потрібно видалити
     * @param projectID айді проєкта де знаходиться цей рядок.
     * @return {@link StringsDeleteQuery}
     */
    StringsDeleteQuery delete(long projectID);
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
