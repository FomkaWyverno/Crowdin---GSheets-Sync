package ua.wyverno.sync.translation.services;

import com.crowdin.client.sourcestrings.model.SourceString;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.localization.model.GSheetTranslateRegistryKey;
import ua.wyverno.localization.model.TranslateRegistryKey;
import ua.wyverno.sync.translation.managers.CrowdinTranslationManager;
import ua.wyverno.sync.translation.managers.GoogleSheetsTranslationManager;
import ua.wyverno.sync.translation.utils.LanguageTranslationsUtils;
import ua.wyverno.utils.json.JSONCreator;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseImportTranslationService {
    private final static Logger logger = LoggerFactory.getLogger(BaseImportTranslationService.class);

    private final GoogleSheetsTranslationManager sheetsTranslationService;
    private final CrowdinTranslationManager translationService;
    private final LanguageTranslationsUtils translationsUtils;
    private final JSONCreator jsonCreator;

    public BaseImportTranslationService(GoogleSheetsTranslationManager sheetsTranslationService,
                                        CrowdinTranslationManager translationService,
                                        LanguageTranslationsUtils translationsUtils,
                                        JSONCreator jsonCreator) {
        this.sheetsTranslationService = sheetsTranslationService;
        this.translationService = translationService;
        this.translationsUtils = translationsUtils;
        this.jsonCreator = jsonCreator;
    }

    /**
     * Імпортує переклад з таблиці до Кроудіна.<br/>
     * Залежно від реалізації {@link #processImport(List, Map, AtomicInteger)} processImport(List, Map, AtomicInteger)} успадкованого класса
     * буде певним чином відбуватись імпорт перекладу.
     */
    public void importTranslationsToCrowdin() {
        logger.info("Downloading and parsing sheet keys...");
        Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById = this.sheetsTranslationService.getTranslationsKeys();
        logger.info("Getting all source strings.");
        List<SourceString> sourceStrings = this.translationService.getListSourceString();
        logger.info("Got all the Source Strings, the number of: {}.", sourceStrings.size());
        logger.info("Importing translation.");

        long startImportMs = System.currentTimeMillis();

        AtomicInteger counter = new AtomicInteger(0);
        this.processImport(sourceStrings, mapGSheetKeysById, counter);

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
     * Процес імпорту який має бути реалізований в успадкованому класі.<br/>
     * Після модифікацій з параметрами - потрібно викликати {@link BaseImportTranslationService#importTranslations(List, Map, AtomicInteger, int)}, щоб запустити імпорт перекладу
     * @param sourceStrings лист з вихідними рядками які потрібно імпортувати переклад
     * @param mapGSheetKeysById мапа де ключ це Айді, а значення ключ перекладу з Аркуша
     * @param counter Атомарний лічильник, для асинхронного лічильника
     * @param sizeSourceStrings загальна кількість вихідних рядків, потрібно вказати, через те, що цей метод може виконуватись асинхронно, одразу великий пакет
     */
    protected abstract void processImport(List<SourceString> sourceStrings,
                                       Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById,
                                       AtomicInteger counter);

    /**
     * Імпорт певних вихідних рядків.<br/>
     * Потрібно цей метод викликати з {@link BaseImportTranslationService#processImport(List, Map, AtomicInteger)}, щоб запустити процес імпорту
     * @param sourceStrings лист з рядками для яких потрібно імпортувати переклад
     * @param mapGSheetKeysById мапа де ключ це Айді, а значення ключ перекладу з Аркуша
     * @param counter Атомарний лічильник, для асинхронного лічильника
     * @param sizeSourceStrings загальна кількість вихідних рядків, потрібно вказати, через те, що цей метод може виконуватись асинхронно, одразу великий пакет
     */
    protected void importTranslations(List<SourceString> sourceStrings,
                                      Map<String, GSheetTranslateRegistryKey> mapGSheetKeysById,
                                      AtomicInteger counter,
                                      final int sizeSourceStrings) {
        sourceStrings.forEach(string -> {
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
}
