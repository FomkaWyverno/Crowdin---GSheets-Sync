package ua.wyverno.crowdin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.crowdin.api.projects.ProjectsAPI;
import ua.wyverno.crowdin.api.sourcefiles.directories.DirectoryAPI;
import ua.wyverno.crowdin.api.sourcefiles.files.FilesAPI;
import ua.wyverno.crowdin.api.sourcestrings.StringsAPI;
import ua.wyverno.crowdin.api.stingtranslation.StringTranslationAPI;
import ua.wyverno.crowdin.api.storage.StorageAPI;
import ua.wyverno.crowdin.service.*;

@Service
public class CrowdinService
        implements CrowdinDirectoriesService, CrowdinFilesService,
        CrowdinStorageService, CrowdinSourceStringsService, CrowdinStringTranslationService,
        CrowdinProjectsService {
    private final DirectoryAPI directoriesAPI;
    private final FilesAPI filesAPI;
    private final StorageAPI storageAPI;
    private final StringsAPI stringsAPI;
    private final StringTranslationAPI stringTranslationAPI;
    private final ProjectsAPI projectsAPI;

    @Autowired
    public CrowdinService(DirectoryAPI directoriesAPI, FilesAPI filesAPI, StorageAPI storageAPI,
                          StringsAPI stringsAPI, StringTranslationAPI stringTranslationAPI,
                          ProjectsAPI projectsAPI) {
        this.directoriesAPI = directoriesAPI;
        this.filesAPI = filesAPI;
        this.storageAPI = storageAPI;
        this.stringsAPI = stringsAPI;
        this.stringTranslationAPI = stringTranslationAPI;
        this.projectsAPI = projectsAPI;
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

    @Override
    public StringsAPI sourceStrings() {
        return this.stringsAPI;
    }

    @Override
    public StringTranslationAPI string_translations() {
        return this.stringTranslationAPI;
    }

    @Override
    public ProjectsAPI projects() {
        return this.projectsAPI;
    }
}
