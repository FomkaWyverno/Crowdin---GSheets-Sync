package ua.wyverno.sync.crowdin.files.services;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.sync.crowdin.directories.results.SyncDirectoriesResult;
import ua.wyverno.sync.crowdin.managers.CrowdinFilesSyncManager;

import java.util.List;
import java.util.Map;

@Service
public class SyncCrowdinFilesService {
    private final static Logger logger = LoggerFactory.getLogger(SyncCrowdinFilesService.class);

    private final CrowdinFilesSyncManager filesManager;
    private final SyncFilesInCategoryService syncFilesInCategoryService;
    private final SyncFilesCleanerService syncFilesCleanerService;

    @Autowired
    public SyncCrowdinFilesService(CrowdinFilesSyncManager filesManager,
                                   SyncFilesInCategoryService syncFilesInCategoryService,
                                   SyncFilesCleanerService syncFilesCleanerService) {
        this.filesManager = filesManager;
        this.syncFilesInCategoryService = syncFilesInCategoryService;
        this.syncFilesCleanerService = syncFilesCleanerService;
    }

    /**
     * Синхронізує файли у Кроудіні
     * @param syncDirectoriesResult результат синхронізації директорій.
     */
    public void synchronizeToFiles(SyncDirectoriesResult syncDirectoriesResult) {
        logger.info("Staring synchronize to files in categories.");
        List<FileInfo> listFiles = this.filesManager.getListFiles();
        Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir = syncDirectoriesResult.groupingSheetByCategoryDir();
        Map<FileInfo, GoogleSheet> crowdinFileToSheetMap = this.syncFilesInCategoryService.synchronizeToCategoryInFiles(groupingSheetsByCategoryDir, listFiles);
        logger.info("Cleaning no required files.");
        this.syncFilesCleanerService.cleanFiles(crowdinFileToSheetMap.keySet().stream().toList(), listFiles);
        logger.info("Finish synchronize files in categories.");
    }
}
