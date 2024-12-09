package ua.wyverno.sync.crowdin.files.services;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.sync.crowdin.directories.results.SyncDirectoriesResult;
import ua.wyverno.sync.crowdin.files.CrowdinFilesManager;

import java.util.List;
import java.util.Map;

@Service
public class SyncCrowdinFilesService {
    private final static Logger logger = LoggerFactory.getLogger(SyncCrowdinFilesService.class);

    private final CrowdinFilesManager filesManager;
    private final SyncCategoryFilesService syncCategoryFilesService;
    private final SyncFilesCleanerService syncFilesCleanerService;

    @Autowired
    public SyncCrowdinFilesService(CrowdinFilesManager filesManager,
                                   SyncCategoryFilesService syncCategoryFilesService,
                                   SyncFilesCleanerService syncFilesCleanerService) {
        this.filesManager = filesManager;
        this.syncCategoryFilesService = syncCategoryFilesService;
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
        Map<FileInfo, GoogleSheet> crowdinFileToSheetMap = this.syncCategoryFilesService.synchronizeToCategory(groupingSheetsByCategoryDir, listFiles);
        logger.info("Cleaning no required files.");
        this.syncFilesCleanerService.cleanFiles(crowdinFileToSheetMap.keySet().stream().toList(), listFiles);
        logger.info("Finish synchronize files in categories.");
    }
}
