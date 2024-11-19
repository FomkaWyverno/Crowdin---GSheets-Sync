package ua.wyverno.crowdin;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import ua.wyverno.crowdin.api.source.files.DirectoriesAPI;
import ua.wyverno.crowdin.api.source.files.FilesAPI;

import java.util.List;
import java.util.function.Predicate;

@Service
public class CrowdinService implements CrowdinDirectoriesService, CrowdinFilesService {
    private final DirectoriesAPI directoriesAPI;
    private final FilesAPI filesAPI;

    @Autowired
    public CrowdinService(DirectoriesAPI directoriesAPI, FilesAPI filesAPI) {
        this.directoriesAPI = directoriesAPI;
        this.filesAPI = filesAPI;
    }

    @Override
    public List<Directory> listAllDirectories(long projectID) {
        return this.directoriesAPI.listDirectoriesWithPagination(projectID, 100, null, false);
    }

    @Override
    public List<Directory> listAllDirectories(long projectID, Long directoryID) {
        return this.directoriesAPI.listDirectoriesWithPagination(projectID, 100, directoryID, false);
    }

    @Override
    public List<Directory> listAllDirectories(long projectID, Long directoryID, boolean isRecursive) {
        return this.directoriesAPI.listDirectoriesWithPagination(projectID, 100, directoryID,  isRecursive);
    }

    @Override
    public List<Directory> findDirectories(long projectID, @NonNull Predicate<Directory> filter) {
        return this.directoriesAPI.findDirectories(projectID, 100, null, false, filter, null);
    }

    @Override
    public List<Directory> findDirectories(long projectID, Long directoryID, @NonNull Predicate<Directory> filter) {
        return this.directoriesAPI.findDirectories(projectID, 100,directoryID, false, filter, null);
    }

    @Override
    public List<Directory> findDirectories(long projectID, Long directoryID, boolean isRecursive, @NonNull Predicate<Directory> filter) {
        return this.directoriesAPI.findDirectories(projectID, 100, directoryID, isRecursive, filter, null);
    }

    @Override
    public List<Directory> findDirectories(long projectID, Predicate<Directory> filter, Integer maxResult) {
        return this.directoriesAPI.findDirectories(projectID, 100, null, false, filter, maxResult);
    }

    @Override
    public List<Directory> findDirectories(long projectID, Long directoryID, @NonNull Predicate<Directory> filter, Integer maxResult) {
        return this.directoriesAPI.findDirectories(projectID, 100, directoryID, false, filter, maxResult);
    }

    @Override
    public List<Directory> findDirectories(long projectID, Long directoryID, boolean isRecursive, @NonNull Predicate<Directory> filter, Integer maxResult) {
        return this.directoriesAPI.findDirectories(projectID, 100, directoryID, isRecursive, filter, maxResult);
    }

    @Override
    public Directory createDirectory(long projectID, String directoryName) {
        return this.directoriesAPI.createDirectory(projectID, directoryName, null);
    }

    @Override
    public Directory createDirectory(long projectID, String directoryName, Long directoryID) {
        return this.directoriesAPI.createDirectory(projectID, directoryName, directoryID);
    }

    @Override
    public List<FileInfo> listAllFiles(long projectID) {
        return this.filesAPI.listFilesWithPagination(projectID, 100, null);
    }

    @Override
    public List<FileInfo> listAllFiles(long projectID, Long directoryID) {
        return this.filesAPI.listFilesWithPagination(projectID, 100, directoryID);
    }

    @Override
    public List<FileInfo> findFiles(long projectID, @NonNull Predicate<FileInfo> filter) {
        return this.filesAPI.findFiles(projectID, 100, null, filter, null);
    }

    @Override
    public List<FileInfo> findFiles(long projectID, Predicate<FileInfo> filter, Integer maxResults) {
        return this.filesAPI.findFiles(projectID, 100, null, filter, maxResults);
    }

    @Override
    public List<FileInfo> findFiles(long projectID, Long directoryID, @NonNull Predicate<FileInfo> filter) {
        return this.filesAPI.findFiles(projectID, 100, directoryID, filter, null);
    }

    @Override
    public List<FileInfo> findFiles(long projectID, Long directoryID, @NonNull Predicate<FileInfo> filter, Integer maxResults) {
        return this.filesAPI.findFiles(projectID, 100, directoryID, filter, maxResults);
    }
}
