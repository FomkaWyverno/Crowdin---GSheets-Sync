package ua.wyverno.sync.translation;

import com.crowdin.client.sourcestrings.model.SourceString;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.localization.model.TranslateRegistryKey;
import ua.wyverno.sync.translation.utils.LanguageTranslationsUtils;
import ua.wyverno.utils.json.JSONCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ImportTranslationService {
    private final static Logger logger = LoggerFactory.getLogger(ImportTranslationService.class);

    private final GoogleSheetsTranslationService sheetsTranslationService;
    private final CrowdinTranslationService translationService;
    private final LanguageTranslationsUtils translationsUtils;
    private final JSONCreator jsonCreator;

    // Встановлюємо 20 максимальних одночасних запитів, щоб вписатися у ліміт до Crowdin API, для прискорення запису перекладів
    private final static int NUMBER_SIMULTANEOUS_REQUESTS = 20;

    @Autowired
    public ImportTranslationService(GoogleSheetsTranslationService sheetsTranslationService,
                                    CrowdinTranslationService translationService,
                                    LanguageTranslationsUtils translationsUtils,
                                    JSONCreator jsonCreator) {
        this.sheetsTranslationService = sheetsTranslationService;
        this.translationService = translationService;
        this.translationsUtils = translationsUtils;
        this.jsonCreator = jsonCreator;
    }

    public void importTranslationsToCrowdin() {
        logger.info("Downloading and parsing sheet keys...");
        Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById = this.sheetsTranslationService.getTranslationsKeys();
        logger.info("Getting all source strings.");
        List<SourceString> sourceStrings = this.translationService.getListSourceString();
        logger.info("Got all the Source Strings, the number of: {}.", sourceStrings.size());
        logger.info("Importing translation.");

        long startImportMs = System.currentTimeMillis();

        this.asyncImportTranslationToCrowdin(sourceStrings, mapGSheetKeysById);

        long endImportMs = System.currentTimeMillis();
        logger.info("Total time for import: {}ms", endImportMs - startImportMs);
    }

    /**
     * Імпорт певного перекладу
     * @param sourceString вихідний рядок
     * @param key ключ перекладу з Аркуша
     */
    private void importTranslation(SourceString sourceString, TranslateRegistryKey key) {
        if (key.translate().isEmpty()) {
            logger.trace("Translation not found in Sheet for SourceString: {}.", sourceString.getIdentifier());
            return;
        }
        List<LanguageTranslations> translations = this.translationService.getTranslations(sourceString);
        LanguageTranslations crowdinTranslation = this.translationsUtils.findCrowdinTranslation(translations, key.translate());
        if (crowdinTranslation != null) {
            if (!key.isApprove()) {
                logger.trace("{} - already has translation, but not have approve.", sourceString.getIdentifier());
                return;
            }
            if (this.translationService.noApprovalString(sourceString)) { // Якщо переклад не має затвердження будь-якого, тоді додаємо затвердження
                logger.trace("Adding approve for: {} Approved: {}",
                        sourceString.getIdentifier(),
                        this.translationsUtils.getTranslation(crowdinTranslation));
                this.translationService.addApproveTranslation(this.translationsUtils.getTranslationId(crowdinTranslation));
            } else {
                logger.trace("{} - Already has approval.", sourceString.getIdentifier());
            }
            return;
        }
        logger.trace("Creating translation for: {}, Translation: {}", sourceString.getIdentifier(), key.translate());
        StringTranslation stringTranslation = this.translationService.addTranslation(sourceString, key.translate());
        if (key.isApprove()) {
            if (this.translationService.noApprovalString(sourceString)) { // Якщо переклад не має затвердження будь-якого, тоді додаємо затвердження
                logger.trace("Approving translation: {}, Translation: {}", sourceString.getIdentifier(), key.translate());
                this.translationService.addApproveTranslation(stringTranslation.getId());
            }
        }
    }

    /**
     * Асинхроний імпорт перекладу у Кроудін
     * @param sourceStrings лист вихідних рядків з Кроудіна
     * @param mapGSheetKeysById мапа де ключ це Айді, а значення ключ перекладу з Аркуша
     */
    private void asyncImportTranslationToCrowdin(List<SourceString> sourceStrings,
                                                 Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById) {
        // Створюємо Тред-Пул на максимальну кількість одночасних запитів до Crowdin API
        try (ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_SIMULTANEOUS_REQUESTS)) {
            // Спільний лічильник прогресу
            AtomicInteger counter = new AtomicInteger(0);
            // Розбиваємо список на максимальну кількість запитів до Crowdin API
            int batchSize = (int) Math.ceil((double) sourceStrings.size() / NUMBER_SIMULTANEOUS_REQUESTS);
            // Ділимо один запит на кількість максимально одночасних запитів
            List<List<SourceString>> batches = this.splitList(sourceStrings, batchSize);
            // Запускаємо завдання на імпорт перекладу
            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch -> // Створюємо завдання для виконання всього імпорту
                            CompletableFuture.runAsync(() ->
                                    this.processBatch(batch,
                                            mapGSheetKeysById,
                                            counter,
                                            sourceStrings.size()), executorService))
                    .toList();

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    /**
     * Виконання однієї партії імпорту
     * @param batch партія для імпорту
     * @param mapGSheetKeysById мапа де ключ це Айді, а значення ключ перекладу з Аркуша
     * @param counter лічильник
     * @param sizeSourceStrings загальна кількість вихідних рядків
     */
    private void processBatch(List<SourceString> batch,
                              Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById,
                              AtomicInteger counter,
                              final int sizeSourceStrings) {
        batch.forEach(string -> {
            try {
                if (mapGSheetKeysById.containsKey(string.getIdentifier())) {
                    this.importTranslation(string, mapGSheetKeysById.get(string.getIdentifier()));
                    logger.info("Imported: {} Remaining: {}/{}",
                            string.getIdentifier(),
                            counter.incrementAndGet(),
                            sizeSourceStrings);
                } else {
                    logger.error("Missing source string in Sheet: {}. Source String JSON: {}",
                            string.getIdentifier(), this.jsonCreator.toJSON(string));
                }
            } catch (Exception e) {
                logger.error("", e);
                throw new RuntimeException(e);
            }
        });
    }

    private List<List<SourceString>> splitList(List<SourceString> list, int batchSize) {
        int totalSize = list.size();
        List<List<SourceString>> batches = new ArrayList<>();
        for (int i = 0; i < totalSize; i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, totalSize)));
        }
        return batches;
    }
}
