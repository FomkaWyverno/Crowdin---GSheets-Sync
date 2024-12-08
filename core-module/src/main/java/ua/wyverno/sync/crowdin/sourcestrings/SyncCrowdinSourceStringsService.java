package ua.wyverno.sync.crowdin.sourcestrings;

import com.crowdin.client.sourcefiles.model.FileInfo;
import com.crowdin.client.sourcestrings.model.SourceString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.AddStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.RemoveBatchStringRequestBuilder;
import ua.wyverno.crowdin.api.sourcestrings.queries.builders.ReplaceBatchStringRequestBuilder;
import ua.wyverno.google.sheets.model.GoogleSheet;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
public class SyncCrowdinSourceStringsService {
    private final static Logger logger = LoggerFactory.getLogger(SyncCrowdinSourceStringsService.class);

    private final CrowdinSourceStringsManager sourceStringsManager;
    private final SyncSheetSourceStrings syncSheetSourceStrings;
    private final SyncSourceStringsCleaner syncSourceStringsCleaner;

    @Autowired
    public SyncCrowdinSourceStringsService(CrowdinSourceStringsManager sourceStringsManager, SyncSheetSourceStrings syncSheetSourceStrings, SyncSourceStringsCleaner syncSourceStringsCleaner) {
        this.sourceStringsManager = sourceStringsManager;
        this.syncSheetSourceStrings = syncSheetSourceStrings;
        this.syncSourceStringsCleaner = syncSourceStringsCleaner;
    }

    /**
     * Синхронізує всі вихідні рядки з аркушами з гугл таблички
     * @param fileBySheet мапа де ключ файл, а значення аркуш, який відображає файл у Кроудіні
     */
    public void synchronizeToSourceStrings(Map<FileInfo, GoogleSheet> fileBySheet) {
        logger.info("Getting all strings in project Crowdin.");
        List<SourceString> allStrings = this.sourceStringsManager.listSourceStrings();

        Map<Long, List<SourceString>> groupingStringByFileId = allStrings.stream()
                        .collect(Collectors.groupingBy(SourceString::getFileId)); // Групуємо вихідні рядки за айді

        List<SourceString> existsStringsInCrowdin = new CopyOnWriteArrayList<>(); // Створюємо лист для рядків які існують і правильно синхронізовані у Кроудіні
                                                                                // до внесення будь-яких змін у Кроудіні.
        List<AddStringRequestBuilder> requestsAddStrings = new CopyOnWriteArrayList <>(); // Створюємо лист для створених запитів на додавання нових рядків
        List<ReplaceBatchStringRequestBuilder> requestReplaceStrings = new CopyOnWriteArrayList<>(); // Створюємо лист для створених запитів на зміну вихідного рядка

        logger.info("Starting synchronization to source strings...");
//        fileBySheet.entrySet().parallelStream() // Створюємо паралельні стріми
//                .forEach(entry -> { // Відправляємо кожен аркуш
//                    SyncSheetSourceStringsResult result = this.syncSheetSourceStrings.synchronizeToSheet(entry.getKey(), entry.getValue(),
//                            groupingStringByFileId.getOrDefault(entry.getKey().getId(), Collections.emptyList())); // Синхронізуємо файл з аркушем
//                    existsStringsInCrowdin.addAll(result.existsStrings()); // Додаємо рядки які існували вже
//                    requestsAddStrings.addAll(result.preparedAddStringRequests()); // Додаємо рядки які потрібно додати
//                    requestReplaceStrings.addAll(result.preparedReplaceStringRequest()); // Додаємо рядки які потрібно змінити якось
//                });
        logger.info("Finish synchronization to source strings.");
        logger.info("Starting collect strings for cleaning no required if need.");
        List<RemoveBatchStringRequestBuilder> requestsRemove = this.syncSourceStringsCleaner.cleanSourceStrings(existsStringsInCrowdin, allStrings);
        // Виконуємо створення, видалення, та редагування рядків у Кроудіні одним Патч запитом
        logger.info("Starting a batch request to the Crowdin API make changes with Source strings.");
        List<SourceString> stringsBatch = this.sourceStringsManager.batchSourceStrings(requestsAddStrings, requestsRemove, requestReplaceStrings);
        logger.info("Finish cleaning no required strings.");
    }
}
