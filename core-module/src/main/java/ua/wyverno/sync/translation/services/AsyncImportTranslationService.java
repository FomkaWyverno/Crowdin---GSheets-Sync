package ua.wyverno.sync.translation.services;

import com.crowdin.client.sourcestrings.model.SourceString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.sync.translation.managers.CrowdinTranslationManager;
import ua.wyverno.sync.translation.managers.GoogleSheetsTranslationManager;
import ua.wyverno.sync.translation.utils.LanguageTranslationsUtils;
import ua.wyverno.utils.json.JSONCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Асинхронний імпорт перекладу. Розбиває один великий лист з вихідними рядками
 * на максимальну кількість одночасних запитів до Crowdin API,
 * щоб одночасно дзвонити до Crowdin API.
 */
@Service
public class AsyncImportTranslationService extends BaseImportTranslationService {

    // Максимальна кількість одночасних запитів до Crowdin API при асинхронному імпорту перекладу,
    // якщо не встановлено, то значення за замовченням буде 20
    @Value("${async.translation.import.max.parallel.calls.api:20}")
    private int MAX_PARALLEL_CALLS_API;

    @Autowired
    public AsyncImportTranslationService(GoogleSheetsTranslationManager sheetsTranslationService,
                                         CrowdinTranslationManager translationService,
                                         LanguageTranslationsUtils translationsUtils,
                                         JSONCreator jsonCreator) {
        super(sheetsTranslationService, translationService, translationsUtils, jsonCreator);
    }

    /**
     * Асинхронний імпорт перекладу. Розбиває один великий лист з вихідними рядками
     * на максимальну кількість одночасних запитів до Crowdin API,
     * щоб одночасно дзвонити до Crowdin API.
     * @param sourceStrings лист з вихідними рядками які потрібно імпортувати переклад
     * @param mapGSheetKeysById мапа де ключ це Айді, а значення ключ перекладу з Аркуша
     * @param counter Атомарний лічильник, для асинхронного лічильника
     */
    @Override
    protected void processImport(List<SourceString> sourceStrings, Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById, AtomicInteger counter) {

        // Рахуємо розмір однієї партії
        int batchSize = (int) Math.ceil((double) sourceStrings.size() / MAX_PARALLEL_CALLS_API);
        // Ділимо один запит на кількість максимально одночасних запитів
        List<List<SourceString>> batches = this.splitList(sourceStrings, batchSize);
        // Створюємо Тред-Пул на максимальну кількість одночасних запитів до Crowdin API
        try (ExecutorService executor = Executors.newFixedThreadPool(MAX_PARALLEL_CALLS_API)) {
            // Запускаємо завдання на імпорт перекладу
            List<CompletableFuture<Void>> futures = batches.stream()
                    .map(batch ->
                            CompletableFuture.runAsync(() ->
                                    this.importTranslations(
                                            batch,
                                            mapGSheetKeysById,
                                            counter,
                                            sourceStrings.size()), executor))
                    .toList();
            // Очікуємо виконання всіх асинхронного виконання
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    /**
     * Розділення одного листа на багато листів з розміром однієї партії
     * @param list один великий лист з вихідними рядками
     * @param batchSize розмір партії
     * @return Лист Листів які розбиті за розміром партії
     */
    private List<List<SourceString>> splitList(List<SourceString> list, int batchSize) {
        int totalSize = list.size();
        List<List<SourceString>> batches = new ArrayList<>();
        for (int i = 0; i < totalSize; i += batchSize) {
            batches.add(list.subList(i, Math.min(i + batchSize, totalSize)));
        }
        return batches;
    }
}
