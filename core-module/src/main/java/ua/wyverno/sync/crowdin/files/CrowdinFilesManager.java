package ua.wyverno.sync.crowdin.files;

import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.storage.model.Storage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.edit.EditFilePath;
import ua.wyverno.crowdin.api.sourcefiles.files.queries.edit.PatchFileRequestBuilder;
import ua.wyverno.crowdin.api.util.edit.PatchEditOperation;

import java.util.List;

@Component
public class CrowdinFilesManager {
    private final static Logger logger = LoggerFactory.getLogger(CrowdinFilesManager.class);

    private final CrowdinService crowdinService;
    private final long projectId;

    @Autowired
    public CrowdinFilesManager(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.projectId = configLoader.getCoreConfig().getProjectID();
    }

    public List<FileInfo> getListFiles() {
        logger.debug("Getting files list from Crowdin API");
        return this.crowdinService.files()
                .list(this.projectId)
                .execute();
    }

    /**
     * Створює файл у Кроудіні
     * @param directoryId айді директорії де потрібно створити файл
     * @param fileName імя файлу
     * @param title заголовок файлу
     * @return Створений файл у Кроудіні
     */
    public FileInfo createFile(Long directoryId, String fileName, String title, String content) {
        logger.debug("Creating file: {}, title: {}, in dirId: {}", fileName, title, directoryId);
        Storage storage = this.crowdinService.storages()
                .add()
                .fileName(fileName)
                .content(content)
                .execute();
        return this.crowdinService.files()
                .create(this.projectId)
                .directoryId(directoryId)
                .name(fileName)
                .title(title)
                .storageID(storage.getId())
                .execute();
    }

    /**
     * Змінює заголовок файлу
     * @param file файл
     * @param title новий заголовок
     * @return Оновлений файл
     */
    public FileInfo changeTitle(FileInfo file, String title) {
        return this.crowdinService.files()
                .edit(this.projectId)
                .fileID(file.getId())
                .addPatchRequest(new PatchFileRequestBuilder()
                        .op(PatchEditOperation.REPLACE)
                        .path(EditFilePath.TITLE)
                        .value(title))
                .execute();
    }
}
