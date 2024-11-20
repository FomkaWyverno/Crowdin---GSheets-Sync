package ua.wyverno.crowdin.api.sourcefiles.files;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesCreateQuery;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesDeleteQuery;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesEditQuery;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.FilesListQuery;

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
    public FilesCreateQuery create(long projectID, long storageID, String name) {
        return new FilesCreateQuery(this.sourceFilesApi, projectID, storageID, name);
    }

    @Override
    public FilesEditQuery edit(long projectID, long fileID) {
        return new FilesEditQuery(this.sourceFilesApi, projectID, fileID);
    }

    @Override
    public FilesDeleteQuery delete(long projectID, long fileID) {
        return new FilesDeleteQuery(this.sourceFilesApi, projectID, fileID);
    }
}
