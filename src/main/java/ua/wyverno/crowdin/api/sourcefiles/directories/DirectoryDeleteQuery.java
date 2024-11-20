package ua.wyverno.crowdin.api.sourcefiles.directories;

import com.crowdin.client.core.http.exceptions.HttpException;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import ua.wyverno.crowdin.api.Query;

public class DirectoryDeleteQuery implements Query<Boolean> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private final long directoryID;

    public DirectoryDeleteQuery(SourceFilesApi sourceFilesApi, long projectID, long directoryID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
        this.directoryID = directoryID;
    }

    /**
     * Виконує запит до Crowdin API - Delete Directories
     * @return Якщо успішно видалено директорію поверне true, якщо директорію не було знайдено - false
     */
    @Override
    public Boolean execute() {
        try {
            this.sourceFilesApi.deleteDirectory(this.projectID, this.directoryID);
            return true;
        } catch (HttpException e) {
            if (e.getError().getCode().equals("404") && e.getError().getMessage().equals("Directory Not Found")) return false;
            throw e;
        }
    }
}
