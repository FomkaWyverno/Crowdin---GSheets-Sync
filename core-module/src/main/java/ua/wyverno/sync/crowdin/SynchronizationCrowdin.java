package ua.wyverno.sync.crowdin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.crowdin.directories.SyncCrowdinDirectoriesService;
import ua.wyverno.sync.crowdin.directories.SyncDirectoriesResult;
import ua.wyverno.sync.crowdin.files.SyncCrowdinFilesService;

@Component
public class SynchronizationCrowdin {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationCrowdin.class);

    private final SyncCrowdinDirectoriesService syncCrowdinDirectoriesService;
    private final SyncCrowdinFilesService syncCrowdinFilesService;
    private final SyncCrowdinSourceStrings syncCrowdinSourceStrings;

    @Autowired
    public SynchronizationCrowdin(SyncCrowdinDirectoriesService syncCrowdinDirectoriesService, SyncCrowdinFilesService syncCrowdinFilesService, SyncCrowdinSourceStrings syncCrowdinSourceStrings) {
        this.syncCrowdinDirectoriesService = syncCrowdinDirectoriesService;
        this.syncCrowdinFilesService = syncCrowdinFilesService;
        this.syncCrowdinSourceStrings = syncCrowdinSourceStrings;
    }

    public void synchronizeToCrowdin(GoogleSpreadsheet spreadsheet) {
        logger.info("Crowdin Sync Step 1: Synchronization to Directories.");
        SyncDirectoriesResult syncDirectoriesResult = this.syncCrowdinDirectoriesService.synchronizeToDirectories(spreadsheet);
        logger.info("Crowdin Sync Step 2: Synchronization to Files.");
        this.syncCrowdinFilesService.synchronizeToFiles(syncDirectoriesResult);
    }
}
