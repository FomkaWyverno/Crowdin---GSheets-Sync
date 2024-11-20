package ua.wyverno.crowdin.api.sourcefiles.files.queries;

import com.crowdin.client.core.http.exceptions.HttpException;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import ua.wyverno.crowdin.api.Query;

public class FilesDeleteQuery implements Query<Boolean> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private final long fileID;

    public FilesDeleteQuery(SourceFilesApi sourceFilesApi, long projectID, long fileID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
        this.fileID = fileID;
    }

    /**
     * Виконує запит до Crowdin API - Delete File
     * @return Якщо успішно видалено файл поверне true, якщо файл не було знайдено - false
     */
    @Override
    public Boolean execute() {
        try {
            this.sourceFilesApi.deleteFile(this.projectID, this.fileID);
            return true;
        } catch (HttpException e) {
            if (e.getError().getCode().equals("404") && e.getError().getMessage().equals("File Not Found")) return false;
            throw e;
        }
    }
}
