package ua.wyverno.sync.crowdin.files;

import com.crowdin.client.sourcefiles.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.csv.parsers.GoogleSheetToCSVParser;
import ua.wyverno.google.sheets.model.GoogleSheet;

import java.util.Map;

@Component
public class SyncContentFiles {
    private final static Logger logger = LoggerFactory.getLogger(SyncContentFiles.class);

    private final CrowdinFilesManager filesManager;
    private final GoogleSheetToCSVParser csvParser;

    @Autowired
    public SyncContentFiles(CrowdinFilesManager filesManager, GoogleSheetToCSVParser csvParser) {
        this.filesManager = filesManager;
        this.csvParser = csvParser;
    }

    /**
     * Синхронізує файли у Кроудіні за їх вмістом.
     * @param sheetByFile мапа де ключ файл, а значення відповідний аркуш
     */
    public void synchronizationToContent(Map<FileInfo, GoogleSheet> sheetByFile) {
        sheetByFile.forEach((file, sheet) -> {
            String contentCSV = this.filesManager.downloadContent(file);
            String sheetCSV = this.csvParser.parseSheet(sheet);

            if (!contentCSV.equals(sheetCSV)) {
                logger.info("Sheet: {} must be synchronize with Crowdin file content.", sheet.getSheetName());
                this.filesManager.updateContent(file, sheetCSV);
            } else {
                logger.info("Sheet: {} not need synchronize with Crowdin file content.", sheet.getSheetName());
            }
        });
    }
}
