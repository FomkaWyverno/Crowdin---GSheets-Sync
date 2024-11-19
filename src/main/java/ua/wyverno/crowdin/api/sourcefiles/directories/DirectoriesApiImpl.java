package ua.wyverno.crowdin.api.sourcefiles.directories;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;

@Component
public class DirectoriesApiImpl implements DirectoryAPI {

    private final SourceFilesApi sourceFilesApi;

    @Autowired
    public DirectoriesApiImpl(CrowdinApiClient crowdinApiClient) {
        this.sourceFilesApi = crowdinApiClient.getCrowdinClient().getSourceFilesApi();
    }

    @Override
    public DirectoryListQuery list(long projectID) {
        return new DirectoryListQuery(this.sourceFilesApi, projectID);
    }

    public DirectoryCreateQuery createDirectory(long projectID, String name) {
        return new DirectoryCreateQuery(this.sourceFilesApi, projectID, name);
    }
}
