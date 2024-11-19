package ua.wyverno.crowdin.api.sourcefiles.directories;

import com.crowdin.client.core.model.ResponseList;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.Directory;
import ua.wyverno.crowdin.api.sourcefiles.ListQuery;

import java.util.List;

public class DirectoryListQuery extends ListQuery<Directory> {
    protected DirectoryListQuery(SourceFilesApi sourceFilesApi, long projectID) {
        super(sourceFilesApi, projectID);
    }
    /**
     * Виконує запит до АПІ<br/>
     * @return {@link List}<{@link Directory}>
     */
    @Override
    public List<Directory> execute() {
        return this.listWithPagination(this.getProjectID());
    }
    @Override
    protected List<Directory> fetchFromAPI(long projectID, Long branchID, Long directoryID,
                                           String filter, boolean isRecursion, int limit, int offset) {
        ResponseList<Directory> response = this.getSourceFilesApi()
                .listDirectories(projectID, branchID, directoryID, filter, isRecursion ? new Object() : null, limit, offset);
        // Конвертуємо відповідь у лист з даними про директорії
        return response.getData()
                .stream()
                .map(ResponseObject::getData)
                .toList();
    }
}
