package ua.wyverno.crowdin.api.sourcefiles.files.queries;

import com.crowdin.client.core.model.ResponseObject;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.FileInfo;
import ua.wyverno.crowdin.api.sourcefiles.ListQuery;

import java.util.List;
import java.util.stream.Collectors;

public class FilesListQuery extends ListQuery<FileInfo> {

    public FilesListQuery(SourceFilesApi sourceFilesApi, long projectID) {
        super(sourceFilesApi, projectID);
    }

    @Override
    public List<FileInfo> execute() {
        return this.listWithPagination(this.getProjectID());
    }

    @Override
    protected List<FileInfo> fetchFromAPI(long projectID, Long branchID, Long directoryID, String filter, boolean isRecursion, int limit, int offset) {
        return this.getSourceFilesApi().listFiles(projectID, branchID, directoryID, filter, isRecursion ? new Object() : null, limit, offset)
                .getData()
                .stream()
                .map(ResponseObject::getData)
                .collect(Collectors.toList());
    }
}
