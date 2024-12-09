package ua.wyverno.sync.crowdin.files.operations;

import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.sync.crowdin.files.CrowdinFilesManager;
import ua.wyverno.sync.crowdin.files.utils.SheetFileUtils;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileTitleSynchronizer {
    private final static Logger logger = LoggerFactory.getLogger(FileTitleSynchronizer.class);

    private final SheetFileUtils sheetFileUtils;
    private final CrowdinFilesManager filesManager;

    @Autowired
    public FileTitleSynchronizer(SheetFileUtils sheetFileUtils, CrowdinFilesManager filesManager) {
        this.sheetFileUtils = sheetFileUtils;
        this.filesManager = filesManager;
    }

    /**
     * Синхронізує заголовок файлів з назвою аркуша
     * @param existsFiles мапа з файлами які існують, де ключ це файл, а значення це аркуш
     * @return Повертає нову мапу, з оновленими даними де було потрібно змінити, якщо було все гаразд, дані просто повернуться які були
     */
    public Map<FileInfo, GoogleSheet> syncFilesTitle(Map<FileInfo, GoogleSheet> existsFiles) {
        return existsFiles.entrySet().stream()
                .map(entry -> {
                    FileInfo file = entry.getKey();
                    GoogleSheet sheet = entry.getValue();
                    String title = this.sheetFileUtils.getTitleForSheet(sheet);
                    if (!file.getTitle().equals(title)) { // Якщо заголовок файлу не сходитися, змінюємо його
                        logger.debug("Change title for file: {}, from {} to {}", file.getName(), file.getTitle(), title);
                        return Map.entry(this.filesManager.changeTitle(file, title), sheet); // Зберігаємо його як новий файл
                    }
                    return Map.entry(file, sheet);}) // Якщо заголовок сходиться, нічого не змінюємо.
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
