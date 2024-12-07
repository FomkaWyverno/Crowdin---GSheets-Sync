package ua.wyverno.sync.crowdin.files;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.sync.crowdin.directories.SyncDirectoriesResult;

import java.util.List;
import java.util.Map;

@Service
public class SyncCrowdinFilesService {
    private final static Logger logger = LoggerFactory.getLogger(SyncCrowdinFilesService.class);

    private final CrowdinFilesManager filesManager;
    private final SyncCategoryFiles syncCategoryFiles;

    @Autowired
    public SyncCrowdinFilesService(CrowdinFilesManager filesManager, SyncCategoryFiles syncCategoryFiles) {
        this.filesManager = filesManager;
        this.syncCategoryFiles = syncCategoryFiles;
    }

    /**
     * Синхронізує файли у Кроудіні
     * @param syncDirectoriesResult результат синхронізації директорій.
     */
    public void synchronizeToFiles(SyncDirectoriesResult syncDirectoriesResult) {
        List<FileInfo> listFiles = this.filesManager.getListFiles();
        Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir = syncDirectoriesResult.groupingSheetByCategoryDir();
        Map<FileInfo, GoogleSheet> crowdinFileToSheetMap = this.syncCategoryFiles.synchronizeToCategory(groupingSheetsByCategoryDir, listFiles);
        logger.info("Finish synchronize files in categories.");
    }
}
