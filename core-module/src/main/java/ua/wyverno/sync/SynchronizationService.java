package ua.wyverno.sync;

import com.crowdin.client.sourcefiles.model.Directory;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

            Spreadsheet spreadsheet = this.googleSheetsService.getSpreadsheetMetadata(this.spreadsheetId);

            List<Sheet> sheets = spreadsheet.getSheets().stream()
                    .filter(sheet -> !this.synchronizeSheetManager.shouldSkipSheetSynchronize(sheet))
                    .toList();

            logger.info(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(sheets));
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
     * Шукає якщо не знаходить створює директорію.
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
}
