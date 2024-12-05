package ua.wyverno.sync.crowdin.directories;

import com.crowdin.client.sourcefiles.model.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;

import java.util.List;
import java.util.Map;

@Service
public class SyncCrowdinDirectoriesService {
    private static final Logger logger = LoggerFactory.getLogger(SyncCrowdinDirectoriesService.class);

    private final SyncRootDirectory syncRootDirectory;
    private final SyncSheetCategories syncSheetCategories;

    private final CrowdinDirectoryManager directoryManager;

    @Autowired
    public SyncCrowdinDirectoriesService(SyncRootDirectory syncRootDirectory,
                                         SyncSheetCategories syncSheetCategories,
                                         CrowdinDirectoryManager directoryManager) {
        this.syncRootDirectory = syncRootDirectory;
        this.syncSheetCategories = syncSheetCategories;
        this.directoryManager = directoryManager;
    }

    public SyncDirectoriesResult synchronizeToDirectories(GoogleSpreadsheet spreadsheet) {
        logger.info("Starting synchronization directories.");

        logger.debug("Getting all directories from Crowdin.");
        List<Directory> allDirectories = this.directoryManager.getAllDirectories();

        logger.info("Starting synchronization Root directory.");
        Directory rootDirectory = this.syncRootDirectory.synchronizeRootDirAndGet(allDirectories).orElse(null);
        logger.info("Starting grouping sheet by Category.");
        Map<Directory, List<GoogleSheet>> syncCategoriesMap = this.syncSheetCategories.synchronizeToSheetCategories(spreadsheet, rootDirectory, allDirectories);
        logger.info("Finish synchronization directories.");
        return new SyncDirectoriesResult(rootDirectory, syncCategoriesMap);
    }
}
