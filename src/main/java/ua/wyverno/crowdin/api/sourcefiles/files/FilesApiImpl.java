package ua.wyverno.crowdin.api.sourcefiles.files;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.CrowdinApiClient;

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
}
