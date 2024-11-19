package ua.wyverno.crowdin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.crowdin.api.sourcefiles.directories.DirectoryAPI;
import ua.wyverno.crowdin.api.sourcefiles.files.FilesAPI;
import ua.wyverno.crowdin.api.storage.StorageAPI;
import ua.wyverno.crowdin.service.CrowdinDirectoriesService;
import ua.wyverno.crowdin.service.CrowdinFilesService;
import ua.wyverno.crowdin.service.CrowdinStorageService;

@Service
public class CrowdinService implements CrowdinDirectoriesService, CrowdinFilesService, CrowdinStorageService {
    private final DirectoryAPI directoriesAPI;
    private final FilesAPI filesAPI;
    private final StorageAPI storageAPI;

    @Autowired
    public CrowdinService(DirectoryAPI directoriesAPI, FilesAPI filesAPI, StorageAPI storageAPI) {
        this.directoriesAPI = directoriesAPI;
        this.filesAPI = filesAPI;
        this.storageAPI = storageAPI;
    }

    @Override
    public DirectoryAPI directories() {
        return this.directoriesAPI;
    }
    @Override
    public FilesAPI files() {return this.filesAPI;}

    @Override
    public StorageAPI storages() {
        return this.storageAPI;
    }
}
