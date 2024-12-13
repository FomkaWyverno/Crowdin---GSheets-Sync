package ua.wyverno.sync.crowdin.directories.services;

import com.crowdin.client.sourcefiles.model.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.crowdin.directories.operations.RootDirectorySynchronizer;
import ua.wyverno.sync.crowdin.directories.operations.SheetCategoryDirectorySynchronizer;
import ua.wyverno.sync.crowdin.directories.results.SyncDirectoriesResult;
import ua.wyverno.crowdin.managers.CrowdinDirectoryManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SyncCrowdinDirectoriesService {
    private static final Logger logger = LoggerFactory.getLogger(SyncCrowdinDirectoriesService.class);

    private final RootDirectorySynchronizer rootDirectorySynchronizer;
    private final SheetCategoryDirectorySynchronizer sheetCategoryDirectorySynchronizer;
    private final SyncDirectoryCleanerService syncDirectoryCleanerService;

    private final CrowdinDirectoryManager directoryManager;

    @Autowired
    public SyncCrowdinDirectoriesService(RootDirectorySynchronizer rootDirectorySynchronizer,
                                         SheetCategoryDirectorySynchronizer sheetCategoryDirectorySynchronizer,
                                         SyncDirectoryCleanerService syncDirectoryCleanerService,
                                         CrowdinDirectoryManager directoryManager) {
        this.rootDirectorySynchronizer = rootDirectorySynchronizer;
        this.sheetCategoryDirectorySynchronizer = sheetCategoryDirectorySynchronizer;
        this.syncDirectoryCleanerService = syncDirectoryCleanerService;

        this.directoryManager = directoryManager;
    }

    public SyncDirectoriesResult synchronizeToDirectories(GoogleSpreadsheet spreadsheet) {
        logger.info("Starting synchronization directories.");

        logger.debug("Getting all directories from Crowdin."); // Отримуємо всі директорії у Кроудіні
        List<Directory> allDirectories = this.directoryManager.getAllDirectories();

        logger.info("Starting synchronization Root directory."); // Синхронізуємо кореневу директорію, та отримуємо цю директорію
        Directory rootDirectory = this.rootDirectorySynchronizer.synchronizeRootDirAndGet(allDirectories).orElse(null);
        logger.info("Starting grouping sheet by Category."); // Синхронізуємо директорії за категоріями
        Map<Directory, List<GoogleSheet>> syncCategoriesMap = this.sheetCategoryDirectorySynchronizer.synchronizeToSheetCategories(spreadsheet, rootDirectory, allDirectories);
        logger.info("Starting cleaning directories.");

        List<Directory> requiredDirectories = new ArrayList<>(syncCategoriesMap.keySet()); // Збираємо всі потрібні директорії у один лист
        requiredDirectories.add(rootDirectory); // Очищаємо директорії
        this.syncDirectoryCleanerService.cleanDirectories(requiredDirectories, allDirectories);

        logger.info("Finish synchronization directories.");
        return new SyncDirectoriesResult(rootDirectory, syncCategoriesMap);
    }
}
