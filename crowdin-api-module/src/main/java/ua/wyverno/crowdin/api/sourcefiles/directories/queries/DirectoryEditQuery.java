package ua.wyverno.crowdin.api.sourcefiles.directories.queries;

import com.crowdin.client.core.model.PatchRequest;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.Directory;
import ua.wyverno.crowdin.api.Query;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit.PatchDirRequestBuilder;

import java.util.ArrayList;
import java.util.List;

public class DirectoryEditQuery implements Query<Directory> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private long directoryID;
    private final List<PatchRequest> requestList;
    public DirectoryEditQuery(SourceFilesApi sourceFilesApi, long projectID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
        this.requestList = new ArrayList<>();
    }

    /**
     * @param directoryID айді директорії яку потрібно змінити
     * @return {@link DirectoryEditQuery}
     */
    public DirectoryEditQuery directoryID(long directoryID) {
        this.directoryID = directoryID;
        return this;
    }

    /**
     * @param patchRequest інструкція що саме потрібно змінити в директорії
     * @return {@link DirectoryEditQuery}
     */
    public DirectoryEditQuery addPatchRequest(PatchDirRequestBuilder patchRequest) {
        this.requestList.add(patchRequest.build());
        return this;
    }

    @Override
    public Directory execute() {
        if (this.requestList.isEmpty()) throw new IllegalArgumentException("Empty patch request list!");
        return this.sourceFilesApi.editDirectory(this.projectID, this.directoryID, this.requestList).getData();
    }
}
