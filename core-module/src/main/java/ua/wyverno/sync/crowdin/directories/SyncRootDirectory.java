package ua.wyverno.sync.crowdin.directories;

import com.crowdin.client.sourcefiles.model.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.config.SyncConfig;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit.EditDirPath;
import ua.wyverno.crowdin.api.sourcefiles.directories.queries.edit.PatchDirRequestBuilder;
import ua.wyverno.crowdin.api.util.edit.PatchEditOperation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class SyncRootDirectory {
    private static final Logger logger = LoggerFactory.getLogger(SyncRootDirectory.class);

    private final CrowdinDirectoryManager directoryManager;

    private final SyncConfig syncConfig;

    @Autowired
    public SyncRootDirectory(CrowdinDirectoryManager directoryManager, ConfigLoader configLoader) {
        this.directoryManager = directoryManager;
        this.syncConfig = configLoader.getSyncConfig();
    }

    /**
     * Синхронізує кореневу директорію
     * @return повертає Optional з кореневою директорію, якщо у конфігурації не налаштована коренева директорія, то поверне порожній Optional
     */
    protected Optional<Directory> synchronizeRootDirAndGet(List<Directory> allDirectories) {
        String crowdinDirRootName = this.syncConfig.getCrowdinDirectoryRoot();
        if (Objects.nonNull(crowdinDirRootName) && !crowdinDirRootName.isEmpty()) {
            logger.debug("Start synchronization Crowdin Root Directory with Title.");
            Directory crowdinDirRoot = this.getOrCreateCrowdinDirRoot(crowdinDirRootName, allDirectories);
            crowdinDirRoot = this.synchronizeRootDirTitleAndGet(crowdinDirRoot);
            logger.debug("Finish synchronization Crowdin Root Directory with Title.");
            return Optional.of(crowdinDirRoot);
        }
        return Optional.empty();
    }

    /**
     * Шукає кореневу директорію якщо не знаходить створює її.
     * @param crowdinDirRootName назва кореневої директорії
     * @param allDirectories список з усіма поточними директоріями у Кроудіні
     * @return {@link Directory} Директорію у Кроудіні
     */
    private Directory getOrCreateCrowdinDirRoot(String crowdinDirRootName, List<Directory> allDirectories) {
        Objects.requireNonNull(crowdinDirRootName, "Crowdin Directory Root can't be null!");
        String pathRootDirectory = "/"+crowdinDirRootName;
        Directory rootDirectory = allDirectories.stream() // Шукаємо кореневу директорію за її шляхом
                .filter(directory -> directory.getPath().equals(pathRootDirectory))
                .findFirst().orElse(null);

        if (rootDirectory != null) return rootDirectory;
        String dirTitle = Objects.nonNull(this.syncConfig.getCrowdinDirectoryRootTitle()) ? this.syncConfig.getCrowdinDirectoryRootTitle() : null;
        return this.directoryManager.createDirectory(null, crowdinDirRootName, dirTitle);
    }

    /**
     * Синхронізує заголовок директорії, якщо він не відповідає яким має він бути.
     * @param crowdinDirRoot директорія Кроудіна
     * @return Повертає директорію, якщо було змінено, повертає з новими змінами, якщо нічого не змінювалось, повертає Директорію яка була передана як параметр методу.
     */
    private Directory synchronizeRootDirTitleAndGet(Directory crowdinDirRoot) {
        if (!isSyncTitle(crowdinDirRoot)) {
            PatchDirRequestBuilder requestReplaceTitle = new PatchDirRequestBuilder()
                    .op(PatchEditOperation.REPLACE)
                    .path(EditDirPath.TITLE)
                    .value(this.syncConfig.getCrowdinDirectoryRootTitle());
            List<PatchDirRequestBuilder> request = Collections.singletonList(requestReplaceTitle);
            return this.directoryManager.editDirectory(crowdinDirRoot.getDirectoryId(), request);
        }

        return crowdinDirRoot;
    }

    /**
     * Чи синхронізований заголовок кореневої директорії з налаштуваннями програми
     * @param crowdinDirRoot коренева директорія Кроудіна
     * @return true - якщо директорія має заголовок як у налаштуваннях програми, інакше false
     */
    private boolean isSyncTitle(Directory crowdinDirRoot) {
        return Objects.nonNull(this.syncConfig.getCrowdinDirectoryRootTitle()) &&
                !this.syncConfig.getCrowdinDirectoryRootTitle().isEmpty() &&
                crowdinDirRoot.getTitle().equals(this.syncConfig.getCrowdinDirectoryRootTitle());
    }
}
