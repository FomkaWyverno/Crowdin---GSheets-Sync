package ua.wyverno.crowdin.api.sourcefiles.files.queries;

import com.crowdin.client.core.model.PatchRequest;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.FileInfo;
import ua.wyverno.crowdin.api.Query;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.edit.PatchFileRequestBuilder;

import java.util.ArrayList;
import java.util.List;

public class FilesEditQuery implements Query<FileInfo> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectID;
    private long fileID;
    private final List<PatchRequest> requestList;

    public FilesEditQuery(SourceFilesApi sourceFilesApi, long projectID) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
        this.requestList = new ArrayList<>();
    }

    /**
     * @param fileID файл айді потрібно редагувати
     * @return {@link FilesEditQuery}
     */
    public FilesEditQuery fileID(long fileID) {
        this.fileID = fileID;
        return this;
    }

    public FilesEditQuery addPatchRequest(PatchFileRequestBuilder patchFileRequest) {
        this.requestList.add(patchFileRequest.build());
        return this;
    }

    @Override
    public FileInfo execute() {
        if (this.requestList.isEmpty()) throw new IllegalArgumentException("Empty patch request list!");
        return this.sourceFilesApi.editFile(this.projectID, this.fileID, this.requestList).getData();
    }
}
