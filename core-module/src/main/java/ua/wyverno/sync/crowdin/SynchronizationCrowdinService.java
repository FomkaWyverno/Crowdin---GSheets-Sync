package ua.wyverno.sync.crowdin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.crowdin.directories.services.SyncCrowdinDirectoriesService;
import ua.wyverno.sync.crowdin.directories.results.SyncDirectoriesResult;
import ua.wyverno.sync.crowdin.files.services.SyncCrowdinFilesService;

@Component
public class SynchronizationCrowdinService {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationCrowdinService.class);

    private final SyncCrowdinDirectoriesService syncCrowdinDirectoriesService;
    private final SyncCrowdinFilesService syncCrowdinFilesService;

    @Autowired
    public SynchronizationCrowdinService(SyncCrowdinDirectoriesService syncCrowdinDirectoriesService,
                                         SyncCrowdinFilesService syncCrowdinFilesService) {
        this.syncCrowdinDirectoriesService = syncCrowdinDirectoriesService;
        this.syncCrowdinFilesService = syncCrowdinFilesService;
    }

    /**
     * Синхронізує Кроудін проєкт з гугл таблицею
     * @param spreadsheet електронна таблиця з вмістом, для синхронізації
     */
    public void synchronizeToCrowdin(GoogleSpreadsheet spreadsheet) {
        logger.info("Start synchronize to Crowdin.");
        logger.info("Crowdin Sync Step 1: Synchronization to Directories.");
        SyncDirectoriesResult syncDirectoriesResult = this.syncCrowdinDirectoriesService.synchronizeToDirectories(spreadsheet);
        logger.info("Crowdin Sync Step 2: Synchronization to Files.");
        this.syncCrowdinFilesService.synchronizeToFiles(syncDirectoriesResult);
        logger.info("Finish synchronization Crowdin with Google Sheets.");
    }
}
