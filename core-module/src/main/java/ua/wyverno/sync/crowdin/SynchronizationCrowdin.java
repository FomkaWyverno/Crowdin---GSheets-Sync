package ua.wyverno.sync.crowdin;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
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
import ua.wyverno.sync.SynchronizeSheetManager;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SynchronizationCrowdin {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationCrowdin.class);

    private final long projectId;
    private final String spreadsheetId;

    private final String crowdinDirRoot;
    private final SyncConfig syncConfig;

    private final CrowdinService crowdinService;
    private final GoogleSheetsService googleSheetsService;
    private final SynchronizeSheetManager synchronizeSheetManager;

    @Autowired
    public SynchronizationCrowdin(CrowdinService crowdinService, GoogleSheetsService googleSheetsService, SynchronizeSheetManager synchronizeSheetManager, ConfigLoader configLoader) {
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

    public void synchronizeToCrowdin() {
        try {
            logger.info("Starting synchronization Crowdin translations with Google Sheets.");
            Long crowdinDirRootId = this.synchronizeRootDirAndGetId();

            Spreadsheet spreadsheetMetadata = this.googleSheetsService.getSpreadsheetMetadata(this.spreadsheetId);

            List<Sheet> sheets = spreadsheetMetadata.getSheets().stream()
                    .filter(sheet -> !this.synchronizeSheetManager.shouldSkipSheetSynchronize(sheet))
                    .toList();

            GoogleSpreadsheet spreadsheet = this.googleSheetsService.getSpreadsheetData(this.spreadsheetId, sheets);
            Map<String, List<GoogleSheet>> sheetsGroupingByCategory = this.groupingSheetsByCategory(spreadsheet);

            logger.info("Finish synchronization Crowdin with Google Sheets.");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Синхронізує кореневу директорію
     * @return повертає айді кореневої директорії
     */
    private Long synchronizeRootDirAndGetId() {
        Long crowdinDirRootId = null;
        if (Objects.nonNull(this.crowdinDirRoot) && !this.crowdinDirRoot.isEmpty()) {
            logger.debug("Start synchronization Crowdin Root Directory with Title.");
            Directory crowdinDirRoot = this.getOrCreateCrowdinDirRoot();
            this.synchronizeRootDirTitle(crowdinDirRoot);
            logger.debug("Finish synchronization Crowdin Root Directory with Title.");
            crowdinDirRootId = crowdinDirRoot.getId();
        }
        return crowdinDirRootId;
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

    /**
     * Чи синхронізований заголовок кореневої директорії з налаштуваннями програми
     * @param crowdinDirRoot коренева директорія Кроудіна
     * @return true - якщо директорія має заголовок як у налаштуваннях програми, інакше false
     */
    private boolean isSyncTitle(Directory crowdinDirRoot) {
        return Objects.nonNull(this.syncConfig.getCrowdinDirectoryRootTitle()) &&
                !this.syncConfig.getCrowdinDirectoryRootTitle().isEmpty() &&
                crowdinDirRoot.getTitle().equals(this.syncConfig.getCrowdinDirectoryRootTitle());
    }

    /**
     * Шукає кореневу директорію якщо не знаходить створює її.
     * @return {@link Directory} Директорію у Кроудіні
     */
    private Directory getOrCreateCrowdinDirRoot() {
        Objects.requireNonNull(this.crowdinDirRoot, "Crowdin Directory Root can't be null!");
        Directory rootDirectory = this.getDirectory(this.crowdinDirRoot);

        if (rootDirectory != null) return rootDirectory;
        String dirTitle = Objects.nonNull(this.syncConfig.getCrowdinDirectoryRootTitle()) ? this.syncConfig.getCrowdinDirectoryRootTitle() : null;
        return this.createDirectory(null, this.crowdinDirRoot, dirTitle);
    }

    private List<Directory> getDirectoriesByNames(List<String> directoriesNames) {
        Objects.requireNonNull(directoriesNames, "filesNames can't be null!");
        if (directoriesNames.isEmpty()) throw new IllegalArgumentException("directoriesNames can't be empty!");
        if (directoriesNames.size() == 1) {
            return Collections.singletonList(this.getDirectory(directoriesNames.get(0)));
        }

        int limitApi = Math.min(directoriesNames.size(), 100);


        return this.crowdinService.directories()
                .list(this.projectId)
                .maxResults(directoriesNames.size())
                .limitAPI(limitApi)
                .filter(directory -> directoriesNames.contains(directory.getName()))
                .execute();
    }

    private Directory getDirectory(String directoryName) {
        Objects.requireNonNull(directoryName, "DirectoryName can't be null!");
        if (directoryName.isEmpty()) throw new IllegalArgumentException("DirectoryName can't be empty!");
        List<Directory> directories = this.crowdinService.directories()
                .list(this.projectId)
                .maxResults(1)
                .limitAPI(1)
                .filterApi(directoryName)
                .execute();
        if (directories.isEmpty()) return null;
        return directories.get(0);
    }

    /**
     * Створює директорію у Кроудіні
     * @param directoryId айді директорії де має бути розташована директорія, якщо це коренева директорія має бути null
     * @param directoryName ім'я директорії
     * @param directoryTitle заголовок директорії може бути null, щоб не встановлювати загаловок
     * @return {@link Directory} створена директорія на Кроудіні
     */
    private Directory createDirectory(@Nullable Long directoryId, String directoryName, @Nullable String directoryTitle) {
        Objects.requireNonNull(directoryName, "Directory name can't be null!");
        return this.crowdinService.directories() // Створюємо директорію у Кроудіні
                .createDirectory(this.projectId)
                .directoryID(directoryId)
                .name(directoryName)
                .title(directoryTitle)
                .execute();
    }

    private String getCategoryFromSheetName(String sheetName, String defaultCategory) {
        Pattern pattern = Pattern.compile("\\((.+)\\)");
        Matcher matcher = pattern.matcher(sheetName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return defaultCategory;
    }

    private Map<String, List<GoogleSheet>> groupingSheetsByCategory(GoogleSpreadsheet spreadsheet) {
        return spreadsheet.getSheets().stream()
                .collect(Collectors.groupingBy(sheet -> this.getCategoryFromSheetName(sheet.getSheetName(), "Others")));
    }
}
