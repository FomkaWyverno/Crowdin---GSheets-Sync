package ua.wyverno.crowdin.api.sourcefiles.files.queries;

import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.FileInfo;
import ua.wyverno.crowdin.api.sourcefiles.ListSourceFilesQuery;

import java.util.List;
import java.util.stream.Collectors;

public class FilesListQuery extends ListSourceFilesQuery<FileInfo, FilesListQuery> {

    public FilesListQuery(SourceFilesApi sourceFilesApi, long projectID) {
        super(sourceFilesApi, projectID);
    }

    @Override
    public List<FileInfo> execute() {
        return this.listWithPagination();
    }

    @Override
    protected List<FileInfo> fetchFromAPI(int limitAPI, int offset) {
        return this.getSourceFilesApi().listFiles(this.getProjectID(), null, this.getDirectoryID(), this.getFilterApi(),
                        this.isRecursive() ? new Object() : null, limitAPI, offset)
                .getData()
                .stream()
                .map(ResponseObject::getData)
                .collect(Collectors.toList());
    }
}
