package ua.wyverno.crowdin.api.sourcefiles.directories;

import com.crowdin.client.core.model.Priority;
import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.AddDirectoryRequest;
import com.crowdin.client.sourcefiles.model.Directory;
import ua.wyverno.crowdin.api.Query;

public class DirectoryCreateQuery implements Query<Directory> {
    private final SourceFilesApi sourceFilesApi;
    private final Long projectID;
    private final String name;
    private Long directoryID = null;
    private String title = null;
    private String exportPattern = null;
    private Priority priority;

    protected DirectoryCreateQuery(SourceFilesApi sourceFilesApi, Long projectID, String name) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectID = projectID;
        this.name = name;
    }

    /**
     * Parent Directory Identifier. Get via List Directories
     *<br/><br/>
     * Note: Can't be used with branchId in same request
     * @param directoryID
     * @return {@link Directory}
     */
    public DirectoryCreateQuery directoryID(Long directoryID) {
        this.directoryID = directoryID;
        return this;
    }

    /**
     * Use to provide more details for translators. Title is available in UI only
     * @param title title
     * @return {@link Directory}
     */
    public DirectoryCreateQuery title(String title) {
        this.title = title;
        return this;
    }

    /**
     * Directory export pattern. Defines directory name and path in resulting translations bundle<br/>
     * Note: Can't contain : * ? " < > | symbols
     * @param exportPattern pattern
     * @return {@link Directory}
     */
    public DirectoryCreateQuery exportPattern(String exportPattern) {
        this.exportPattern = exportPattern;
        return this;
    }

    /**
     * Default: "normal" <br/>
     * Defines priority level for each branch
     * @param priority Enum: "low" "normal" "high"
     * @return {@link Directory}
     */

    public DirectoryCreateQuery priority(Priority priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Виконує API запит на створення теки в проєкті
     * @return {@link Directory} - інформація про створену теку
     */
    @Override
    public Directory execute() {
        AddDirectoryRequest addDirectoryRequest = new AddDirectoryRequest();
        addDirectoryRequest.setName(this.name);
        addDirectoryRequest.setDirectoryId(this.directoryID);
        addDirectoryRequest.setTitle(this.title);
        addDirectoryRequest.setExportPattern(this.exportPattern);
        addDirectoryRequest.setPriority(this.priority);
        return this.sourceFilesApi
                .addDirectory(this.projectID, addDirectoryRequest)
                .getData();
    }
}
