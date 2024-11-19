package ua.wyverno.crowdin;

import com.crowdin.client.sourcefiles.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ua.wyverno.crowdin.api.sourcefiles.directories.DirectoryAPI;
import ua.wyverno.crowdin.api.sourcefiles.files.FilesAPI;
import ua.wyverno.crowdin.api.sourcefiles.files.FilesApiImpl;

import java.util.List;
import java.util.function.Predicate;

@Service
public class CrowdinService implements CrowdinDirectoriesService, CrowdinFilesService {
    private final DirectoryAPI directoriesAPI;
    private final FilesAPI filesAPI;

    @Autowired
    public CrowdinService(DirectoryAPI directoriesAPI, FilesAPI filesAPI) {
        this.directoriesAPI = directoriesAPI;
        this.filesAPI = filesAPI;
    }

    @Override
    public DirectoryAPI directories() {
        return this.directoriesAPI;
    }
    @Override
    public FilesAPI files() {return this.filesAPI;}
}
