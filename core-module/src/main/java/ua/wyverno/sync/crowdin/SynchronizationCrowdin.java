package ua.wyverno.sync.crowdin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.crowdin.directories.SyncCrowdinDirectoriesService;

@Component
public class SynchronizationCrowdin {
    private static final Logger logger = LoggerFactory.getLogger(SynchronizationCrowdin.class);

    private final SyncCrowdinDirectoriesService syncCrowdinDirectoriesService;
    private final SyncCrowdinFiles syncCrowdinFiles;
    private final SyncCrowdinSourceStrings syncCrowdinSourceStrings;

    @Autowired
    public SynchronizationCrowdin(SyncCrowdinDirectoriesService syncCrowdinDirectoriesService, SyncCrowdinFiles syncCrowdinFiles, SyncCrowdinSourceStrings syncCrowdinSourceStrings) {
        this.syncCrowdinDirectoriesService = syncCrowdinDirectoriesService;
        this.syncCrowdinFiles = syncCrowdinFiles;
        this.syncCrowdinSourceStrings = syncCrowdinSourceStrings;
    }

    public void synchronizeToCrowdin(GoogleSpreadsheet spreadsheet) {
        this.syncCrowdinDirectoriesService.synchronizeToDirectories(spreadsheet);
    }
}
