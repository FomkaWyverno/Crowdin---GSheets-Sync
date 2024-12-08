package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.core.http.exceptions.HttpBatchBadRequestException;
import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcestrings.model.SourceString;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import ua.wyverno.google.sheets.model.GoogleSheet;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class SyncCrowdinSourceStringsService {
    private final static Logger logger = LoggerFactory.getLogger(SyncCrowdinSourceStringsService.class);

    private final CrowdinSourceStringsManager sourceStringsManager;
    private final SyncSheetSourceStrings syncSheetSourceStrings;

    @Autowired
    public SyncCrowdinSourceStringsService(CrowdinSourceStringsManager sourceStringsManager, SyncSheetSourceStrings syncSheetSourceStrings) {
        this.sourceStringsManager = sourceStringsManager;
        this.syncSheetSourceStrings = syncSheetSourceStrings;
    }

    /**
     * Синхронізує всі вихідні рядки з аркушами з гугл таблички
     * @param fileBySheet мапа де ключ файл, а значення аркуш, який відображає файл у Кроудіні
     */
    public void synchronizeToSourceStrings(Map<FileInfo, GoogleSheet> fileBySheet) {
        logger.info("Staring synchronization to source strings.");
        List<SourceString> allStrings = this.sourceStringsManager.listSourceStrings();
        Map<Long, List<SourceString>> groupingStringByFileId = allStrings.stream()
                        .collect(Collectors.groupingBy(SourceString::getFileId)); // Групуємо вихідні рядки за айді

        List<SourceString> existsStrings = new CopyOnWriteArrayList<>(); // Створюємо лист для рядків які існують
        List<AddStringRequestBuilder> requestsAdd = new CopyOnWriteArrayList <>(); // Створюємо лист для запитів на додавання нових рядків

        fileBySheet.entrySet().parallelStream() // Створюємо паралельні стріми
                .forEach(entry -> { // Відправляємо кожен аркуш
                    SyncSheetSourceStringsResult result = this.syncSheetSourceStrings.synchronizeToSheet(entry.getKey(), entry.getValue(),
                            groupingStringByFileId.getOrDefault(entry.getKey().getId(), Collections.emptyList())); // Синхронізуємо файл з аркушем
                    existsStrings.addAll(result.existsStrings()); // Додаємо рядки які існували вже
                    requestsAdd.addAll(result.preparedAddStringRequests()); // Додаємо рядки які потрібно додати
                });
        // Виконуємо створення рядків у Кроудіні
        List<SourceString> createdStrings = this.sourceStringsManager.createBatchSourceStrings(requestsAdd);

        logger.info("Finish synchronization to source strings.");
    }
}
