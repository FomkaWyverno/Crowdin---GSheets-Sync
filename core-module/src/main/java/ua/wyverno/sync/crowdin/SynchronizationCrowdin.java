package ua.wyverno.sync.crowdin;

import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.crowdin.directories.SyncCrowdinDirectoriesService;
import ua.wyverno.sync.crowdin.directories.SyncDirectoriesResult;
import ua.wyverno.sync.crowdin.files.SyncCrowdinFilesService;
import ua.wyverno.sync.crowdin.sourcestrings.SyncCrowdinSourceStringsService;

import java.util.Map;

@Component
public class SynchronizationCrowdin {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationCrowdin.class);

    private final SyncCrowdinDirectoriesService syncCrowdinDirectoriesService;
    private final SyncCrowdinFilesService syncCrowdinFilesService;
    private final SyncCrowdinSourceStringsService syncCrowdinSourceStringsService;

    @Autowired
    public SynchronizationCrowdin(SyncCrowdinDirectoriesService syncCrowdinDirectoriesService, SyncCrowdinFilesService syncCrowdinFilesService, SyncCrowdinSourceStringsService syncCrowdinSourceStringsService) {
        this.syncCrowdinDirectoriesService = syncCrowdinDirectoriesService;
        this.syncCrowdinFilesService = syncCrowdinFilesService;
        this.syncCrowdinSourceStringsService = syncCrowdinSourceStringsService;
    }

    public void synchronizeToCrowdin(GoogleSpreadsheet spreadsheet) {
        logger.info("Crowdin Sync Step 1: Synchronization to Directories.");
        SyncDirectoriesResult syncDirectoriesResult = this.syncCrowdinDirectoriesService.synchronizeToDirectories(spreadsheet);
        logger.info("Crowdin Sync Step 2: Synchronization to Files.");
        Map<FileInfo, GoogleSheet> fileBySheet = this.syncCrowdinFilesService.synchronizeToFiles(syncDirectoriesResult);
        logger.info("Crowdin Sync Step 3: Synchronization to Source Strings.");
        this.syncCrowdinSourceStringsService.synchronizeToSourceStrings(fileBySheet);
    }
}
