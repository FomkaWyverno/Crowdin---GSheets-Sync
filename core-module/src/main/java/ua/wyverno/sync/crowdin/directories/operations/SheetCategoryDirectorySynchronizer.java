package ua.wyverno.sync.crowdin.directories.operations;

import com.crowdin.client.sourcefiles.model.Directory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.google.sheets.model.GoogleSpreadsheet;
import ua.wyverno.sync.crowdin.managers.CrowdinDirectorySyncManager;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SheetCategoryDirectorySynchronizer {
    private static final Logger logger = LoggerFactory.getLogger(SheetCategoryDirectorySynchronizer.class);

    private final CrowdinDirectorySyncManager directoryManager;

    private final Pattern categoryPattern = Pattern.compile("\\((.+)\\)");

    @Autowired
    public SheetCategoryDirectorySynchronizer(CrowdinDirectorySyncManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    /**
     * Синхронізує директорії, щоб у категоріях аркушів була відповідна директорія
     * @param spreadsheet електрона табличка з вмістом
     * @param rootDirectory коренева директорія
     * @param allDirectories всі директорії у Кроудіні
     * @return Мапу де ключ це директорія категорії, а значення це аркуші які входять в цю категорію
     */
    public Map<Directory, List<GoogleSheet>> synchronizeToSheetCategories(GoogleSpreadsheet spreadsheet, Directory rootDirectory, List<Directory> allDirectories) {
        Map<String, List<GoogleSheet>> groupingSheetsByCategory = this.groupingSheetsByCategory(spreadsheet);
        logger.info("Starting synchronize categories sheets.");

        String parentPath = this.getParentPath(rootDirectory);
        Long rootDirectoryId = this.getRootDirectoryId(rootDirectory);

        // Отримуємо всі директорії які існують
        List<Directory> existsDirectories = this.collectExistsDirectories(groupingSheetsByCategory, parentPath, allDirectories);
        // Створюємо директорії, яких не існувало
        List<Directory> createdDirectories = this.createMissingDirectories(groupingSheetsByCategory, existsDirectories, rootDirectoryId);
        // Додаємо до листа директорій які існують, ново-створенні директорії
        existsDirectories.addAll(createdDirectories);
        // Збираємо мапу з директоріями, де ключ це назва директорії, а значення це директорія
        Map<String, Directory> directoryByName = existsDirectories.stream()
                        .collect(Collectors.toMap(Directory::getName, Function.identity()));

        logger.info("Finish synchronize categories sheets.");
        return groupingSheetsByCategory.entrySet().stream()
                .collect(Collectors.toMap( // Перетворюємо мапу у мапі ключ, з назви Категорії, на відповідну інформацію про Директорію.
                        entry -> directoryByName.get(entry.getKey()),
                        Map.Entry::getValue));
    }

    /**
     * Групує аркуша в електронні таблиці за категоріями
     * @param spreadsheet електронна таблиця
     * @return Мапа де ключ це категорія, значення - Лист з аркушами для цієї категорії
     */
    private Map<String, List<GoogleSheet>> groupingSheetsByCategory(GoogleSpreadsheet spreadsheet) {
        return spreadsheet.getSheets().stream()
                .collect(Collectors.groupingBy(sheet -> this.getCategoryFromSheetName(sheet.getSheetName(), "Others")));
    }

    /**
     * Дістає категорію аркуша з його назви
     * @param sheetName назва аркуша
     * @param defaultCategory категорія за замовчуванням, якщо не буде знайдено категорії у назві аркуша
     * @return категорію аркуша
     */
    private String getCategoryFromSheetName(String sheetName, String defaultCategory) {
        Matcher matcher = this.categoryPattern.matcher(sheetName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return defaultCategory;
    }


    /**
     * Збирає всі існуючи директорії в лист
     * @param groupingSheetsByCategory погруповані аркуші за категорією
     * @param parentPath шлях де лежить директорія
     * @param allDirectories всі директорії у Кроудіні
     * @return зібраний список з директоріями
     */
    private List<Directory> collectExistsDirectories(Map<String, List<GoogleSheet>> groupingSheetsByCategory, String parentPath, List<Directory> allDirectories) {
        // Створюємо шляхи до категорій, щоб надалі знайти всі ці директорії у Кроудіні
        List<String> pathsCategories = this.toPathsCategories(groupingSheetsByCategory, parentPath);
        // Збираємо всі директорії які існують для категорій
        return allDirectories.stream()
                .filter(directory -> pathsCategories.contains(directory.getPath()))
                .collect(Collectors.toList());
    }

    private List<String> toPathsCategories(Map<String, List<GoogleSheet>> groupingSheetsByCategory, String parentPath) {
        return groupingSheetsByCategory.keySet().stream()
                .map(category -> parentPath + category)
                .toList();
    }

    /**
     * Беремо айді кореневої директорії якщо rootDirectory null, поверне null
     * @param rootDirectory коренева директорія
     * @return айді директорії, якщо rootDirectory - null, поверне null
     */
    private Long getRootDirectoryId(Directory rootDirectory) {
        return rootDirectory != null ? rootDirectory.getId() : null;
    }

    /**
     * Створює parentPath для пошуку директорії
     * @param rootDirectory коренева директорія
     * @return якщо rootDirectory не null поверне відносно його шлях, якщо ні, поверне як корінь проєкта
     */
    private String getParentPath(Directory rootDirectory) {
        return rootDirectory != null ? rootDirectory.getPath() + "/" : "/";
    }

    /**
     * Створює директорії які не існують для категорій
     * @param groupingSheetsByCategory мапа де ключ це назва категорії
     * @param existsDirectories лист з директоріями які існують
     * @param rootDirectoryId айді кореневої директорії
     * @return Лист зі створеними директоріями
     */
    private List<Directory> createMissingDirectories(Map<String, List<GoogleSheet>> groupingSheetsByCategory, List<Directory> existsDirectories, Long rootDirectoryId) {
        // Формуємо за список категорій у вигляді директорії які існують
        List<String> existsCategoriesNames = existsDirectories.stream().map(Directory::getName).toList();
        // Фільтруємо мапу, залишаючи лише ті категорії які не існують.
        Map<String, List<GoogleSheet>> missingGroupCategories = groupingSheetsByCategory.entrySet().stream()
                .filter(entry -> !existsCategoriesNames.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return missingGroupCategories.keySet().stream()
                .map(category -> this.directoryManager.createDirectory(rootDirectoryId, category, null))
                .toList();
    }

}
