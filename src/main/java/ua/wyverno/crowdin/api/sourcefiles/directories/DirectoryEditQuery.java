package ua.wyverno.crowdin.api.sourcefiles.directories;

import com.crowdin.client.core.model.PatchOperation;
import com.crowdin.client.core.model.PatchRequest;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.Directory;
import ua.wyverno.crowdin.api.Query;

import java.util.ArrayList;
import java.util.List;

public class DirectoryEditQuery implements Query<Directory> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private final long directoryID;
    private final List<PatchRequest> requestList;
    protected DirectoryEditQuery(SourceFilesApi sourceFilesApi, long projectID, long directoryID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
        this.directoryID = directoryID;
        this.requestList = new ArrayList<>();
    }

    public DirectoryEditQuery addPatchRequest(PatchRequest patchRequest) {
        this.requestList.add(patchRequest);
        return this;
    }

    @Override
    public Directory execute() {
        if (this.requestList.isEmpty()) throw new IllegalArgumentException("Empty patch request list!");
        return this.sourceFilesApi.editDirectory(this.projectID, this.directoryID, this.requestList).getData();
    }
}
