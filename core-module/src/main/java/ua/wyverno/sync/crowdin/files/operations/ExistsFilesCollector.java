package ua.wyverno.sync.crowdin.files.operations;

import com.crowdin.client.sourcefiles.model.Directory;
import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.google.sheets.model.GoogleSheet;
import ua.wyverno.sync.crowdin.files.utils.SheetFileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ExistsFilesCollector {
    private final static Logger logger = LoggerFactory.getLogger(ExistsFilesCollector.class);

    private final SheetFileUtils sheetFileUtils;

    @Autowired
    public ExistsFilesCollector(SheetFileUtils sheetFileUtils) {
        this.sheetFileUtils = sheetFileUtils;
    }

    /**
     * Збирає всі файли у Кроудіні які існують, та створює Мапу де ключ це Файл, а значення це Аркуш
     * @param groupingSheetsByCategoryDir погрупована мапа, де значення це Директорія Категорії, а значення це лист з аркушами
     * @param fileByPathMap мапа де ключ це шлях до файлу, а значення сам файл
     * @return Повертає мапу з файлами які існують у Кроудіні, які відображають аркуш у гугл таблиці.
     */
    public Map<FileInfo, GoogleSheet> collectExistsFilesBySheet(Map<Directory, List<GoogleSheet>> groupingSheetsByCategoryDir, Map<String, FileInfo> fileByPathMap) {
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
            String pathToFile = pathToDir + this.sheetFileUtils.getFileNameForSheet(sheet);
            if (fileByPathMap.containsKey(pathToFile)) {
                FileInfo file = fileByPathMap.get(pathToFile);
                logger.debug("Found file for sheet {}, id: {}. File: {}, Path: {}", sheet.getSheetName(), sheet.getSheetId(), file.getName(), file.getPath());
                foundFilesBySheets.put(file, sheet);
            }});

        return foundFilesBySheets;
    }
}
