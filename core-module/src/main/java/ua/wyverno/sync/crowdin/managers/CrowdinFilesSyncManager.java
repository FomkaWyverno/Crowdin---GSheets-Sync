package ua.wyverno.sync.crowdin.managers;

import com.crowdin.client.core.model.DownloadLink;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;

@Component
public class CrowdinFilesSyncManager {
    private final static Logger logger = LoggerFactory.getLogger(CrowdinFilesSyncManager.class);

    private final CrowdinService crowdinService;
    private final long projectId;

    @Autowired
    public CrowdinFilesSyncManager(CrowdinService crowdinService, ConfigLoader configLoader) {
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

    public boolean deleteFile(FileInfo file) {
        return this.crowdinService.files()
                .delete(this.projectId)
                .fileID(file.getId())
                .execute();
    }

    public String downloadContent(FileInfo file) {
        StringBuilder content = new StringBuilder();
        DownloadLink link = this.crowdinService.files()
                .download(this.projectId)
                .fileId(file.getId())
                .execute();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(URI.create(link.getUrl()).toURL().openStream()))) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return content.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateContent(FileInfo file, String content) {
        Storage storage = this.crowdinService.storages()
                .add()
                .fileName(file.getName())
                .content(content)
                .execute();
        this.crowdinService.files()
                .update(this.projectId)
                .fileId(file.getId())
                .storageId(storage.getId())
                .execute();
    }
}
