package ua.wyverno.sync.crowdin.directories;

import com.crowdin.client.sourcefiles.model.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SyncCrowdinDirectoriesService {
    private static final Logger logger = LoggerFactory.getLogger(SyncCrowdinDirectoriesService.class);

    private final SyncRootDirectory syncRootDirectory;
    private final SyncSheetCategoryDirectory syncSheetCategoryDirectory;
    private final SyncDirectoryCleaner syncDirectoryCleaner;

    private final CrowdinDirectoryManager directoryManager;

    @Autowired
    public SyncCrowdinDirectoriesService(SyncRootDirectory syncRootDirectory,
                                         SyncSheetCategoryDirectory syncSheetCategoryDirectory,
                                         SyncDirectoryCleaner syncDirectoryCleaner,
                                         CrowdinDirectoryManager directoryManager) {
        this.syncRootDirectory = syncRootDirectory;
        this.syncSheetCategoryDirectory = syncSheetCategoryDirectory;
        this.syncDirectoryCleaner = syncDirectoryCleaner;

        this.directoryManager = directoryManager;
    }

    public SyncDirectoriesResult synchronizeToDirectories(GoogleSpreadsheet spreadsheet) {
        logger.info("Starting synchronization directories.");

        logger.debug("Getting all directories from Crowdin.");
        List<Directory> allDirectories = this.directoryManager.getAllDirectories();

        logger.info("Starting synchronization Root directory.");
        Directory rootDirectory = this.syncRootDirectory.synchronizeRootDirAndGet(allDirectories).orElse(null);
        logger.info("Starting grouping sheet by Category.");
        Map<Directory, List<GoogleSheet>> syncCategoriesMap = this.syncSheetCategoryDirectory.synchronizeToSheetCategories(spreadsheet, rootDirectory, allDirectories);
        logger.info("Starting cleaning directories.");

        List<Directory> requiredDirectories = new ArrayList<>(syncCategoriesMap.keySet()); // Збираємо всі потрібні директорії у один лист
        requiredDirectories.add(rootDirectory);
        this.syncDirectoryCleaner.cleanDirectories(requiredDirectories, allDirectories);

        logger.info("Finish synchronization directories.");
        return new SyncDirectoriesResult(rootDirectory, syncCategoriesMap);
    }
}
