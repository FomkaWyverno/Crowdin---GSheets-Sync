package ua.wyverno.crowdin.api.sourcefiles.files;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.*;

@Component
public class FilesApiImpl implements FilesAPI {
    private final SourceFilesApi sourceFilesApi;

    @Autowired
    public FilesApiImpl(CrowdinApiClient crowdinApiClient) {
        this.sourceFilesApi = crowdinApiClient.getCrowdinClient().getSourceFilesApi();
    }

    @Override
    public FilesListQuery list(long projectID) {
        return new FilesListQuery(this.sourceFilesApi, projectID);
    }

    @Override
    public FilesCreateQuery create(long projectID) {
        return new FilesCreateQuery(this.sourceFilesApi, projectID);
    }

    @Override
    public FilesEditQuery edit(long projectID) {
        return new FilesEditQuery(this.sourceFilesApi, projectID);
    }

    @Override
    public FilesDeleteQuery delete(long projectID) {
        return new FilesDeleteQuery(this.sourceFilesApi, projectID);
    }

    @Override
    public FilesUpdateQuery update(long projectId) {
        return new FilesUpdateQuery(this.sourceFilesApi, projectId);
    }

    @Override
    public FilesDownloadPreviewQuery downloadPreview(long projectId) {
        return new FilesDownloadPreviewQuery(this.sourceFilesApi, projectId);
    }

    @Override
    public FilesDownloadQuery download(long projectId) {
        return new FilesDownloadQuery(this.sourceFilesApi, projectId);
    }
}
