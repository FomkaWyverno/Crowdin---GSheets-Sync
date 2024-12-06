package ua.wyverno.sync.crowdin.directories;

import com.crowdin.client.sourcefiles.model.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SyncDirectoryCleaner {
    private final static Logger logger = LoggerFactory.getLogger(SyncDirectoryCleaner.class);

    private final CrowdinDirectoryManager directoryManager;

    private final BufferedReader reader;

    @Autowired
    public SyncDirectoryCleaner(CrowdinDirectoryManager directoryManager) {
        this.directoryManager = directoryManager;

        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    protected void cleanDirectories(List<Directory> requiredDirectories, List<Directory> allDirectories) {
        List<String> requiredDirPaths = requiredDirectories.stream().map(Directory::getPath).toList();
        List<Directory> noRequiredDirs = allDirectories.stream()
                .filter(directory -> !requiredDirPaths.contains(directory.getPath()))
                .toList();

        List<Directory> sortedDirectories = noRequiredDirs.stream() // Сортуємо директорії по глибині, від найглибшої, до верхньої
                .sorted((d1, d2) -> Integer.compare(this.getDepthByPath(d2.getPath()), this.getDepthByPath(d1.getPath())))
                .toList();

        sortedDirectories.stream()
                .filter(this::askToDeleteDirectory)
                .forEach(directory -> {
                    boolean isDelete = this.directoryManager.deleteDirectory(directory);
                    if (isDelete) {
                        System.out.printf("Successful delete directory: %s, Path: %s%n", directory.getName(), directory.getPath());
                        logger.debug("Deleted directory: {}, Path: {}", directory.getName(), directory.getPath());
                    } else {
                        System.out.printf("Could not delete directory: %s, Path: %s%n", directory.getName(), directory.getPath());
                        logger.warn("Could not delete directory: {}, Path: {}", directory.getName(), directory.getPath());
                    }
                });
    }

    private int getDepthByPath(String path) {
        return path.split("/").length;
    }

    private boolean askToDeleteDirectory(Directory directory) {
        try {
            System.out.printf("Do you want to delete directory? Directory Name: %s, Path: %s (yes/no): ", directory.getName(), directory.getPath());
            String userInput = reader.readLine();
            return userInput.equalsIgnoreCase("yes");
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
