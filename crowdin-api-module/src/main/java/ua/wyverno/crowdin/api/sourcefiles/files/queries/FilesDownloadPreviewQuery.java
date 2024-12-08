package ua.wyverno.crowdin.api.sourcefiles.files.queries;

import com.crowdin.client.core.model.DownloadLink;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import ua.wyverno.crowdin.api.Query;

public class FilesDownloadPreviewQuery implements Query<DownloadLink> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectId;

    private Long fileId;

    public FilesDownloadPreviewQuery(SourceFilesApi sourceFilesApi, long projectId) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectId = projectId;
    }

    /**
     * @param fileId File Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.files.getMany">List Files</a>
     * @return {@link FilesDownloadPreviewQuery}
     */
    public FilesDownloadPreviewQuery fileId(Long fileId) {
        this.fileId = fileId;
        return this;
    }

    @Override
    public DownloadLink execute() {
        return this.sourceFilesApi.downloadFilePreview(this.projectId, this.fileId).getData();
    }
}
