package ua.wyverno.sync.crowdin.files.operations;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.csv.parsers.GoogleSheetToCSVParser;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.crowdin.managers.CrowdinFilesManager;
import ua.wyverno.sync.crowdin.files.utils.SheetFileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MissingFilesCreator {
    private final static Logger logger = LoggerFactory.getLogger(MissingFilesCreator.class);

    private final SheetFileUtils sheetFileUtils;
    private final CrowdinFilesManager filesManager;
    private final GoogleSheetToCSVParser csvParser;

    @Autowired
    public MissingFilesCreator(SheetFileUtils sheetFileUtils, CrowdinFilesManager filesManager, GoogleSheetToCSVParser csvParser) {
        this.sheetFileUtils = sheetFileUtils;
        this.filesManager = filesManager;
        this.csvParser = csvParser;
    }

    /**
     * Створює файли яких не існує
     * @param groupingSheetsByCategoryDir погрупована мапа, де ключ це Директорія Категорії, а значення це лист з аркушами
     * @param existsFilesByPathMap мапа з файлами які існують, де ключ це шлях до файлу, а значення це файл
     * @return Мапу зі створеними файлами, та де ключ це файл, а значення це аркуш який відображає цей файл
     */
    public Map<FileInfo, GoogleSheet> createMissingFiles(Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir, Map<String, FileInfo> existsFilesByPathMap) {
        Map<FileInfo, GoogleSheet> createdMissingFiles = new HashMap<>();

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
                    this.sheetFileUtils.getFileNameForSheet(sheet),
                    this.sheetFileUtils.getTitleForSheet(sheet),
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
                    String pathToFile = pathDir + this.sheetFileUtils.getFileNameForSheet(sheet);
                    return !existsFilesByPathMap.containsKey(pathToFile);
                })
                .toList();
    }
}
