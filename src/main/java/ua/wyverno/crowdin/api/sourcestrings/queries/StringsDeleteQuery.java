package ua.wyverno.crowdin.api.sourcestrings.queries;

import com.crowdin.client.core.http.exceptions.HttpException;
import com.crowdin.client.sourcestrings.SourceStringsApi;
import ua.wyverno.crowdin.api.Query;

public class StringsDeleteQuery implements Query<Boolean> {
    private final SourceStringsApi sourceStringsApi;
    private final long projectID;

    private long stringID;
    public StringsDeleteQuery(SourceStringsApi sourceStringsApi, long projectID) {
        this.sourceStringsApi = sourceStringsApi;
        this.projectID = projectID;
    }

    public StringsDeleteQuery stringID(long stringID) {
        this.stringID = stringID;
        return this;
    }

    /**
     * @return Поверне true якщо видалено, false якщо не було знайдено рядок
     */

    @Override
    public Boolean execute() {
        try {
            this.sourceStringsApi.deleteSourceString(this.projectID, this.stringID);
            return true;
        } catch (HttpException e) {
            HttpException.Error error = e.getError();
            if (error.getCode().equals("404") && error.getMessage().equals("String Not Found")) {
                return false;
            }
            throw e;
        }
    }
}
