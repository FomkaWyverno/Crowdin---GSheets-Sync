package ua.wyverno.sync.crowdin.files;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.csv.parsers.GoogleSheetToCSVParser;
import ua.wyverno.google.sheets.model.GoogleSheet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SyncCategoryFiles {
    private final static Logger logger = LoggerFactory.getLogger(SyncCategoryFiles.class);
    private final static String FILE_EXTENSION = ".csv";
    private final static Pattern TITLE_PATTERN = Pattern.compile("(.+)\\(");

    private final CrowdinFilesManager filesManager;
    private final GoogleSheetToCSVParser csvParser;
    private final SyncContentFiles syncContentFiles;

    @Autowired
    public SyncCategoryFiles(CrowdinFilesManager filesManager, GoogleSheetToCSVParser csvParser, SyncContentFiles syncContentFiles) {
        this.filesManager = filesManager;
        this.csvParser = csvParser;
        this.syncContentFiles = syncContentFiles;
    }

    protected Map<FileInfo, GoogleSheet> synchronizeToCategory(Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir , List<FileInfo> allFiles) {
        // Перетворюємо лист з файлами на мапу де ключ це шлях до файлу, а значення Файл кроудіну
        Map<String, FileInfo> fileByPathMap = this.listFilesToFileByPathMap(allFiles);
        logger.debug("Collecting exists files.");
        // Шукаємо файли які існують, та відповідають аркушам
        Map<FileInfo, GoogleSheet> existsFiles = this.collectExistsFilesBySheet(groupingSheetsByCategoryDir, fileByPathMap);
        // Синхронізуємо заголовки файлів, якщо вони не відповідають
        logger.debug("Synchronization exists files to title.");
        existsFiles = this.syncFilesTitle(existsFiles);
        // Синхронізація вмісту файлів
        logger.info("Starting synchronization to Content.");
        this.syncContentFiles.synchronizationToContent(existsFiles);
        // Створюємо файли які не існують
        logger.debug("Creating files if need.");
        Map<FileInfo, GoogleSheet> createdFiles = this.createMissingFiles(groupingSheetsByCategoryDir, existsFiles);
        existsFiles.putAll(createdFiles);
        return existsFiles;
    }

    /**
     * Перетворює лист з файлами, на більш зручну мапу, де ключ це шлях до файлу, а значення це файл Кроудіна
     * @param allFiles лист з файлами
     * @return мапа де ключ це шлях до файлу, а значення це файл Кроудіна
     */
    private Map<String, FileInfo> listFilesToFileByPathMap(List<FileInfo> allFiles) {
        return allFiles.stream()
                .collect(Collectors.toMap(FileInfo::getPath, Function.identity()));
    }

    /**
     * Збирає всі файли у Кроудіні які існують, та створює Мапу де ключ це Файл, а значення це Аркуш
     * @param groupingSheetsByCategoryDir погрупована мапа, де значення це Директорія Категорії, а значення це лист з аркушами
     * @param fileByPathMap мапа де ключ це шлях до файлу, а значення сам файл
     * @return Повертає мапу з файлами які існують у Кроудіні, які відображають аркуш у гугл таблиці.
     */
    private Map<FileInfo, GoogleSheet> collectExistsFilesBySheet(Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir, Map<String, FileInfo> fileByPathMap) {
        Map<FileInfo, GoogleSheet> existsFilesBySheet = new HashMap<>();
        groupingSheetsByCategoryDir.forEach((categoryDir, sheets) ->
                existsFilesBySheet.putAll(this.findFileForCategoryInFiles(categoryDir, sheets, fileByPathMap)));
        return existsFilesBySheet;
    }

    /**
     * Шукає у директорії яка відображає категорію, файли які відображають аркуш
     * @param categoryDir директорія яка відображає категорію
     * @param sheets аркуші у цій категорії
     * @param fileByPathMap мапа з файлами у Кроудіні, де ключ це шлях до файлу, а значення файл у Кроудіні
     * @return Мапу зі знайденими файлами, де ключ це знайдений файл, а значення відповідний аркуш гугл таблиці
     */
    private Map<FileInfo, GoogleSheet> findFileForCategoryInFiles(Directory categoryDir, List<GoogleSheet> sheets, Map<String, FileInfo> fileByPathMap) {
        Map<FileInfo, GoogleSheet> foundFilesBySheets = new HashMap<>();

        String pathToDir = categoryDir.getPath() + "/";
        sheets.forEach(sheet -> {
            String pathToFile = pathToDir + this.getFileNameForSheet(sheet);
            if (fileByPathMap.containsKey(pathToFile)) {
                FileInfo file = fileByPathMap.get(pathToFile);
                logger.debug("Found file for sheet {}, id: {}. File: {}, Path: {}", sheet.getSheetName(), sheet.getSheetId(), file.getName(), file.getPath());
                foundFilesBySheets.put(file, sheet);
            }});

        return foundFilesBySheets;
    }

    /**
     * Синхронізує заголовок файлів з назвою аркуша
     * @param existsFiles мапа з файлами які існують, де ключ це файл, а значення це аркуш
     * @return Повертає нову мапу, з оновленими даними де було потрібно змінити, якщо було все гаразд, дані просто повернуться які були
     */
    private Map<FileInfo, GoogleSheet> syncFilesTitle(Map<FileInfo, GoogleSheet> existsFiles) {
        return existsFiles.entrySet().stream()
                .map(entry -> {
                    FileInfo file = entry.getKey();
                    GoogleSheet sheet = entry.getValue();
                    String title = this.getTitleForSheet(sheet);
                    if (!file.getTitle().equals(title)) { // Якщо заголовок файлу не сходитися, змінюємо його
                        logger.debug("Change title for file: {}, from {} to {}", file.getName(), file.getTitle(), title);
                        return Map.entry(this.filesManager.changeTitle(file, title), sheet); // Зберігаємо його як новий файл
                    }
                    return Map.entry(file, sheet);}) // Якщо заголовок сходиться, нічого не змінюємо.
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Створює файли яких не існує
     * @param groupingSheetsByCategoryDir погрупована мапа, де ключ це Директорія Категорії, а значення це лист з аркушами
     * @param existsFiles мапа з файлами які існують, де ключ це файл, а значення це аркуш
     * @return Мапу зі створеними файлами, та де ключ це файл, а значення це аркуш який відображає цей файл
     */
    private Map<FileInfo, GoogleSheet> createMissingFiles(Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir, Map<FileInfo, GoogleSheet> existsFiles) {
        Map<FileInfo, GoogleSheet> createdMissingFiles = new HashMap<>();

        Map<String, FileInfo> existsFilesByPathMap = this.listFilesToFileByPathMap(existsFiles.keySet().stream().toList());
        groupingSheetsByCategoryDir.forEach((categoryDir, sheets) -> {
            List<GoogleSheet> missingFilesForSheets = this.getMissingFilesForSheets(categoryDir, sheets, existsFilesByPathMap);
            createdMissingFiles.putAll(this.createMissingFilesForCategory(categoryDir, missingFilesForSheets));
        });

        return createdMissingFiles;
    }

    /**
     * Створює файли яких не існує, для певної категорії
     * @param categoryDir директорія де знаходиться ця категорія
     * @param missingFilesForSheets лист з аркушами, у яких немає відповідного файлу
     * @return Мапу зі створеними файлами, у цій категорії. Де ключ це файл, а значення це аркуш який відображає цей файл
     */
    private Map<FileInfo, GoogleSheet> createMissingFilesForCategory(Directory categoryDir, List<GoogleSheet> missingFilesForSheets) {
        Map<FileInfo, GoogleSheet> createdMissingFilesForCategory = new HashMap<>();
        // Шукаємо відсутні файли
        missingFilesForSheets.forEach(sheet -> {
            FileInfo file = this.filesManager.createFile(categoryDir.getId(),
                    this.getFileNameForSheet(sheet),
                    this.getTitleForSheet(sheet),
                    this.csvParser.parseSheet(sheet));
            createdMissingFilesForCategory.put(file, sheet);
        });
        return createdMissingFilesForCategory;
    }

    /**
     * Шукає відсутні файли які відповідають за аркуш у гугл таблиці
     * @param categoryDir директорія у якій потрібно перевірити відсутні файли
     * @param sheets аркуші які мають бути у цій категорії
     * @param existsFilesByPathMap мапа файлів які існують, де ключ це шлях до файлу, а значення сам файл
     * @return Лист з відсутніми аркушами у цій категорії
     */
    private List<GoogleSheet> getMissingFilesForSheets(Directory categoryDir, List<GoogleSheet> sheets, Map<String, FileInfo> existsFilesByPathMap) {
        String pathDir = categoryDir.getPath() + "/";
        return sheets.stream()
                .filter(sheet -> {
                    String pathToFile = pathDir + this.getFileNameForSheet(sheet);
                    return !existsFilesByPathMap.containsKey(pathToFile);
                })
                .toList();
    }

    /**
     * Створює імя файлу для аркуша
     * @param sheet аркуш
     * @return імя файлу для аркуша
     */
    private String getFileNameForSheet(GoogleSheet sheet) {
        return sheet.getSheetId() + FILE_EXTENSION;
    }

    private String getTitleForSheet(GoogleSheet sheet) {
        String sheetName = sheet.getSheetName();
        if (!sheetName.contains("(")) {
            return sheetName;
        }
        Matcher matcher = TITLE_PATTERN.matcher(sheetName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        logger.warn("Sheet Name: {}, can't find title without category.", sheetName);
        return sheetName;
    }
}
