package ua.wyverno.sync;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.storage.model.Storage;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.config.CoreConfig;
import ua.wyverno.config.SyncConfig;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit.EditDirPath;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit.PatchDirRequestBuilder;
import ua.wyverno.crowdin.api.util.edit.PatchEditOperation;
import ua.wyverno.google.sheets.GoogleSheetsService;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SynchronizationService {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationService.class);

    private final long projectId;
    private final String spreadsheetId;

    private final String crowdinDirRoot;
    private SyncConfig syncConfig;

    private final CrowdinService crowdinService;
    private final GoogleSheetsService googleSheetsService;
    private final SynchronizeSheetManager synchronizeSheetManager;

    @Autowired
    public SynchronizationService(CrowdinService crowdinService, GoogleSheetsService googleSheetsService, SynchronizeSheetManager synchronizeSheetManager, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.googleSheetsService = googleSheetsService;
        this.synchronizeSheetManager = synchronizeSheetManager;

        SyncConfig syncConfig = configLoader.getSyncConfig();
        CoreConfig coreConfig = configLoader.getCoreConfig();

        this.projectId = coreConfig.getProjectID();
        this.spreadsheetId = coreConfig.getSpreadsheetID();

        this.crowdinDirRoot = syncConfig.getCrowdinDirectoryRoot();
        this.syncConfig = syncConfig;
    }

    public void synchronizeWithGoogleSheets() {
        try {
            logger.info("Starting synchronization Crowdin translations with Google Sheets.");
            Long crowdinDirRootId = null;
            if (Objects.nonNull(this.crowdinDirRoot) && !this.crowdinDirRoot.isEmpty()) {
                logger.debug("Start synchronization Crowdin Root Directory with Title.");
                Directory crowdinDirRoot = this.getCrowdinDirRoot();
                this.synchronizeRootDirTitle(crowdinDirRoot);
                logger.debug("Finish synchronization Crowdin Root Directory with Title.");
                crowdinDirRootId = crowdinDirRoot.getId();
            }

            Spreadsheet spreadsheetMetadata = this.googleSheetsService.getSpreadsheetMetadata(this.spreadsheetId);

            List<Sheet> sheets = spreadsheetMetadata.getSheets().stream()
                    .filter(sheet -> !this.synchronizeSheetManager.shouldSkipSheetSynchronize(sheet))
                    .toList();

            GoogleSpreadsheet spreadsheet = this.googleSheetsService.getSpreadsheetData(this.spreadsheetId, sheets);
            logger.info("Parse sheet-id ");
            Map<String, String> titleBySheetIdFileName = spreadsheet.getSheets().stream()
                    .collect(Collectors.toMap(
                            sheet -> sheet.getSheetId() + ".json",
                            GoogleSheet::getSheetName,
                            (exists, replacement) -> {
                                logger.warn("Sheet has duplicate: '{}' and '{}'", exists, replacement);
                                return replacement;}));
            List<FileInfo> files =

            logger.info("Finish synchronization Crowdin with Google Sheets.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Синхронізує заголовок директорії, якщо він не відповідає яким має він бути.
     * @param crowdinDirRoot директорія Кроудіна
     */
    private void synchronizeRootDirTitle(Directory crowdinDirRoot) {
        if (!isSyncTitle(crowdinDirRoot)) {
            this.crowdinService.directories()
                    .editDirectory(this.projectId)
                    .directoryID(crowdinDirRoot.getId())
                    .addPatchRequest(new PatchDirRequestBuilder()
                            .op(PatchEditOperation.REPLACE)
                            .path(EditDirPath.TITLE)
                            .value(this.syncConfig.getCrowdinDirectoryRootTitle()))
                    .execute();
        }
    }

    private boolean isSyncTitle(Directory crowdinDirRoot) {
        return Objects.nonNull(this.syncConfig.getCrowdinDirectoryRootTitle()) &&
                !this.syncConfig.getCrowdinDirectoryRootTitle().isEmpty() &&
                crowdinDirRoot.getTitle().equals(this.syncConfig.getCrowdinDirectoryRootTitle());
    }

    /**
     * Шукає кореневу директорію якщо не знаходить створює її.
     * @return {@link Directory} Директорію у Кроудіні
     */
    private Directory getCrowdinDirRoot() {
        Objects.requireNonNull(this.crowdinDirRoot, "Crowdin Directory Root can't be null!");
        List<Directory> directories = this.crowdinService.directories()
                .list(this.projectId)
                .limitAPI(1)
                .maxResults(1)
                .filterApi(this.crowdinDirRoot)
                .execute();

        if (!directories.isEmpty()) return directories.get(0);
        return this.crowdinService.directories() // Створюємо директорію у Кроудіні
                .createDirectory(this.projectId)
                .name(this.crowdinDirRoot)
                .title(Objects.nonNull(this.syncConfig.getCrowdinDirectoryRootTitle()) ? this.syncConfig.getCrowdinDirectoryRootTitle() : null)
                .execute();
    }

    /**
     * Шукає файли якщо не знаходить створює їх.
     * @return {@link FileInfo} Лист з Файлами у Кроудіні
     */
    private List<FileInfo> getCrowdinFiles(Map<String, String> crowdinFileMap, Long crowdinDirRootId) {
        List<FileInfo> files = this.crowdinService.files()
                .list(this.projectId)
                .maxResults(crowdinFileMap.size())
                .filter(file -> crowdinFileMap
                        .keySet()
                        .stream()
                        .anyMatch(fileName -> file.getName().equals(fileName)))
                .directoryID(crowdinDirRootId)
                .execute();

        if (files.size() == crowdinFileMap.size()) return files;
        List<String> missingFileName = crowdinFileMap.keySet().stream()
                .filter(fileName -> files.stream().noneMatch(file -> file.getName().equals(fileName)))
                .toList();

        Map<String, Long> storageIdByFileName = new HashMap<>();

        missingFileName.forEach(fileName -> {
            Storage storage = this.crowdinService.storages()
                    .add()
                    .content("{}")
                    .fileName(fileName)
                    .execute();
            storageIdByFileName.put(fileName, storage.getId());
        });

        // TODO: Додати створення файлу з Title
        storageIdByFileName.forEach((fileName, storageId) -> {
            FileInfo fileInfo = this.crowdinService.files()
                    .create(this.projectId)
                    .storageID(storageId)
                    .name(fileName)
                    .execute();
            files.add(fileInfo);
        });

        return files;
    }
}
