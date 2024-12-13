package ua.wyverno.sync.crowdin.translation.services;

import com.crowdin.client.sourcestrings.model.SourceString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.crowdin.managers.CrowdinStringsManager;
import ua.wyverno.crowdin.managers.CrowdinTranslationManager;
import ua.wyverno.sync.crowdin.translation.GoogleSheetsTranslationManager;
import ua.wyverno.crowdin.util.LanguageTranslationsUtils;
import ua.wyverno.utils.execution.ExecutionTimerFactory;
import ua.wyverno.utils.json.JSONCreator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Однопоточний синхронний імпорт перекладу
 */
@Service
public class ImportTranslationService extends BaseImportTranslationService {

    @Autowired
    public ImportTranslationService(GoogleSheetsTranslationManager sheetsTranslationService,
                                    CrowdinTranslationManager translationManager,
                                    CrowdinStringsManager stringsManager,
                                    LanguageTranslationsUtils translationsUtils,
                                    ExecutionTimerFactory executionTimerFactory,
                                    JSONCreator jsonCreator) {
        super(sheetsTranslationService, translationManager, stringsManager, translationsUtils, executionTimerFactory, jsonCreator);
    }
    /**
     * Однопоточний синхронний імпорт перекладу
     */
    @Override
    protected void processImport(List<SourceString> sourceStrings, Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById, AtomicInteger counter, Set<Long> approveStringIds) {
        this.importTranslations(sourceStrings, mapGSheetKeysById, counter, approveStringIds, sourceStrings.size());
        // Нічого не змінюємо, одразу запускаємо процес імпорту, імпорт буде відбуватись в одному потоці
    }
}
