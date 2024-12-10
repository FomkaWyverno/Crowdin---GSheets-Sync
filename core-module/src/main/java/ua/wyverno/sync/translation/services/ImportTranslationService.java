package ua.wyverno.sync.translation.services;

import com.crowdin.client.sourcestrings.model.SourceString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.sync.translation.managers.CrowdinTranslationManager;
import ua.wyverno.sync.translation.managers.GoogleSheetsTranslationManager;
import ua.wyverno.sync.translation.utils.LanguageTranslationsUtils;
import ua.wyverno.utils.json.JSONCreator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Однопоточний синхронний імпорт перекладу
 */
@Service
public class ImportTranslationService extends BaseImportTranslationService {

    @Autowired
    public ImportTranslationService(GoogleSheetsTranslationManager sheetsTranslationService, CrowdinTranslationManager translationService, LanguageTranslationsUtils translationsUtils, JSONCreator jsonCreator) {
        super(sheetsTranslationService, translationService, translationsUtils, jsonCreator);
    }
    /**
     * Однопоточний синхронний імпорт перекладу
     */
    @Override
    protected void processImport(List<SourceString> sourceStrings, Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById, AtomicInteger counter) {
        this.importTranslations(sourceStrings, mapGSheetKeysById, counter, sourceStrings.size());
        // Нічого не змінюємо, одразу запускаємо процес імпорту, імпорт буде відбуватись в одному потоці
    }
}
