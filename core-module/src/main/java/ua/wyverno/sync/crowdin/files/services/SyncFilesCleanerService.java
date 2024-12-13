package ua.wyverno.sync.crowdin.files.services;

import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.crowdin.managers.CrowdinFilesManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class SyncFilesCleanerService {
    private final static Logger logger = LoggerFactory.getLogger(SyncFilesCleanerService.class);

    private final CrowdinFilesManager filesManager;
    private final BufferedReader reader;

    @Autowired
    public SyncFilesCleanerService(CrowdinFilesManager filesManager) {
        this.filesManager = filesManager;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Очищаємо проєкт від непотрібних файлів
     * @param requiredFiles потрібні файли
     * @param allFiles всі файли у Кроудіні
     */
    public void cleanFiles(List<FileInfo> requiredFiles, List<FileInfo> allFiles) {
        List<String> requiredPathFiles = requiredFiles.stream().map(FileInfo::getPath).toList();
        List<FileInfo> noRequiredFiles = allFiles.stream() // Збираємо файли, які не потрібні
                .filter(file -> !requiredPathFiles.contains(file.getPath()))
                .toList();

        noRequiredFiles.stream()
                .filter(this::askToDeleteFile)
                .forEach(file -> {
                    boolean isDelete = this.filesManager.deleteFile(file);
                    if (isDelete) {
                        System.out.printf("Successful delete file: %s, Title: %s, Path: %s%n", file.getName(), file.getTitle(), file.getPath());
                        logger.debug("Deleted file: {}, Title: {}, Path: {}", file.getName(), file.getTitle(), file.getPath());
                    } else {
                        System.out.printf("Could not delete file: %s, Title: %s, Path: %s%n", file.getName(), file.getTitle(), file.getPath());
                        logger.warn("Could not delete file: {}, Title: {}, Path: {}", file.getName(), file.getTitle(), file.getPath());
                    }
                });
    }

    private boolean askToDeleteFile(FileInfo file) {
        try {
            System.out.printf("Do you want to delete file? File Name: %s, Title: %s, Path: %s (yes/no): ", file.getName(), file.getTitle(), file.getPath());
            String userInput = reader.readLine();
            return "yes".equalsIgnoreCase(userInput);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
