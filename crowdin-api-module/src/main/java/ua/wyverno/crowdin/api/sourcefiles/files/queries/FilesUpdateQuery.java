package ua.wyverno.crowdin.api.sourcefiles.files.queries;

import com.crowdin.client.sourcefiles.SourceFilesApi;
import com.crowdin.client.sourcefiles.model.*;
import ua.wyverno.crowdin.api.Query;

import java.util.List;

public class FilesUpdateQuery implements Query<FileInfo> {
    private final SourceFilesApi sourceFilesApi;
    private final long projectId;

    private Long fileId;
    private Long storageId;
    private UpdateOption updateOption;
    private ImportOptions importOptions;
    private ExportOptions exportOptions;
    private List<Long> attachLabelIds;
    private List<Long> detachLabelIds;

    public FilesUpdateQuery(SourceFilesApi sourceFilesApi, long projectId) {
        this.sourceFilesApi = sourceFilesApi;
        this.projectId = projectId;
    }

    /**
     * @param fileId File Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.files.getMany">List Files</a>
     * @return {@link FilesUpdateQuery}
     */
    public FilesUpdateQuery fileId(Long fileId) {
        this.fileId = fileId;
        return this;
    }

    /**
     * @param storageId Storage Identifier. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.storages.getMany">List Storages<a/>
     * @return {@link FilesUpdateQuery}
     */
    public FilesUpdateQuery storageId(Long storageId) {
        this.storageId = storageId;
        return this;
    }

    /**
     * @param updateOption Default: "clear_translations_and_approvals"<br/>
     * Enum: "clear_translations_and_approvals" "keep_translations" "keep_translations_and_approvals"<br/>
     * Defines whether to keep existing translations and approvals for updated strings
     * @return {@link FilesUpdateQuery}
     */
    public FilesUpdateQuery updateOption(UpdateOption updateOption) {
        this.updateOption = updateOption;
        return this;
    }

    /**
     * @param importOptions Spreadsheet File Import Options (object) or Xml File Import Options (object) or WebXml File Import Options (object) or Docx File Import Options (object) or Html File Import Options (object) or Html with Front Matter File Import Options (object) or Md File Import Options (object) or Mdx v1 File Import Options (object) or Mdx v2 File Import Options (object) or Other Files Import Options (object) (File Import Options)
     * @return {@link FilesUpdateQuery}
     */
    public FilesUpdateQuery importOptions(ImportOptions importOptions) {
        this.importOptions = importOptions;
        return this;
    }

    /**
     * @param exportOptions General File Export Options (object) or Property File Export Options (object) or JavaScript File Export Options (object) or Md File Export Options (object) or Mdx v1 File Export Options (object) or Mdx v2 File Export Options (object) (File Export Options)
     * @return {@link FilesUpdateQuery}
     */
    public FilesUpdateQuery exportOptions(ExportOptions exportOptions) {
        this.exportOptions = exportOptions;
        return this;
    }

    /**
     * @param attachLabelIds Attach labels to updated strings. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.labels.getMany">List Labels<a/>
     * @return {@link FilesUpdateQuery}
     */
    public FilesUpdateQuery attachLabelIds(List<Long> attachLabelIds) {
        this.attachLabelIds = attachLabelIds;
        return this;
    }

    /**
     * @param detachLabelIds Detach labels from updated strings. Get via <a href="https://support.crowdin.com/developer/api/v2/#operation/api.projects.labels.getMany">List Labels</a>
     * @return {@link FilesUpdateQuery}
     */
    public FilesUpdateQuery detachLabelIds(List<Long> detachLabelIds) {
        this.detachLabelIds = detachLabelIds;
        return this;
    }

    @Override
    public FileInfo execute() {
        UpdateFileRequest updateFileRequest = new UpdateFileRequest();
        updateFileRequest.setStorageId(this.storageId);
        updateFileRequest.setUpdateOption(this.updateOption);
        updateFileRequest.setImportOptions(this.importOptions);
        updateFileRequest.setExportOptions(this.exportOptions);
        updateFileRequest.setAttachLabelIds(this.attachLabelIds);
        updateFileRequest.setDetachLabelIds(this.detachLabelIds);

        return this.sourceFilesApi.updateOrRestoreFile(this.projectId, this.fileId, updateFileRequest)
                .getData();
    }
}
