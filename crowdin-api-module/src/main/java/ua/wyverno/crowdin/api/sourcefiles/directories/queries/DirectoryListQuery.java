package ua.wyverno.crowdin.api.sourcefiles.directories.queries;

import com.crowdin.client.core.model.ResponseList;
import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.Directory;
import ua.wyverno.crowdin.api.sourcefiles.ListSourceFilesQuery;

import java.util.List;

public class DirectoryListQuery extends ListSourceFilesQuery<Directory, DirectoryListQuery> {
    public DirectoryListQuery(SourceFilesApi sourceFilesApi, long projectID) {
        super(sourceFilesApi, projectID);
    }
    /**
     * Виконує запит до АПІ<br/>
     * @return {@link List}<{@link Directory}>
     */
    @Override
    public List<Directory> execute() {
        return this.listWithPagination();
    }
    @Override
    protected List<Directory> fetchFromAPI(int limitAPI, int offset) {
        ResponseList<Directory> response = this.getSourceFilesApi()
                .listDirectories(this.getProjectID(), null, this.getDirectoryID(), this.getFilterApi(), this.isRecursive() ? new Object() : null, limitAPI, offset);
        // Конвертуємо відповідь у лист з даними про директорії
        return response.getData()
                .stream()
                .map(ResponseObject::getData)
                .toList();
    }
}
