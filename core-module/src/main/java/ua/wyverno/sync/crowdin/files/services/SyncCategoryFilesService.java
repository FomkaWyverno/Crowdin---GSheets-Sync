package ua.wyverno.sync.crowdin.files.services;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.sync.crowdin.files.operations.ExistsFilesCollector;
import ua.wyverno.sync.crowdin.files.operations.FileTitleSynchronizer;
import ua.wyverno.sync.crowdin.files.operations.FilesContentSynchronizer;
import ua.wyverno.sync.crowdin.files.operations.MissingFilesCreator;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SyncCategoryFilesService {
    private final static Logger logger = LoggerFactory.getLogger(SyncCategoryFilesService.class);

    private final ExistsFilesCollector existsFilesCollector;
    private final FileTitleSynchronizer fileTitleSynchronizer;
    private final FilesContentSynchronizer filesContentSynchronizer;
    private final MissingFilesCreator missingFilesCreator;

    @Autowired
    public SyncCategoryFilesService(FilesContentSynchronizer filesContentSynchronizer,
                                    ExistsFilesCollector existsFilesCollector,
                                    FileTitleSynchronizer fileTitleSynchronizer,
                                    MissingFilesCreator missingFilesCreator) {
        this.filesContentSynchronizer = filesContentSynchronizer;
        this.existsFilesCollector = existsFilesCollector;
        this.fileTitleSynchronizer = fileTitleSynchronizer;
        this.missingFilesCreator = missingFilesCreator;
    }

    protected Map<FileInfo, GoogleSheet> synchronizeToCategory(Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir , List<FileInfo> allFiles) {
        // Перетворюємо лист з файлами на мапу де ключ це шлях до файлу, а значення Файл кроудіну
        Map<String, FileInfo> fileByPathMap = this.mapFileByPath(allFiles);
        logger.debug("Collecting exists files.");
        // Шукаємо файли які існують, та відповідають аркушам
        Map<FileInfo, GoogleSheet> existsFiles = this.existsFilesCollector.collectExistsFilesBySheet(groupingSheetsByCategoryDir, fileByPathMap);
        // Синхронізуємо заголовки файлів, якщо вони не відповідають
        logger.debug("Synchronization exists files to title.");
        existsFiles = this.fileTitleSynchronizer.syncFilesTitle(existsFiles);
        // Синхронізація вмісту файлів
        logger.info("Starting synchronization to Content.");
        this.filesContentSynchronizer.synchronizationToContent(existsFiles);
        // Створюємо файли які не існують
        logger.debug("Creating files if need.");
        Map<FileInfo, GoogleSheet> createdFiles = this.missingFilesCreator.createMissingFiles(groupingSheetsByCategoryDir, this.mapFileByPath(existsFiles.keySet().stream().toList()));
        existsFiles.putAll(createdFiles);
        return existsFiles;
    }

    /**
     * Перетворює лист з файлами, на більш зручну мапу, де ключ це шлях до файлу, а значення це файл Кроудіна
     * @param allFiles лист з файлами
     * @return мапа де ключ це шлях до файлу, а значення це файл Кроудіна
     */
    private Map<String, FileInfo> mapFileByPath(List<FileInfo> allFiles) {
        return allFiles.stream()
                .collect(Collectors.toMap(FileInfo::getPath, Function.identity()));
    }
}
