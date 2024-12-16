package ua.wyverno.sync.crowdin.translation.services;

import com.crowdin.client.sourcestrings.model.SourceString;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.localization.model.key.TranslateKey;
import ua.wyverno.crowdin.managers.CrowdinStringsManager;
import ua.wyverno.crowdin.managers.CrowdinTranslationManager;
import ua.wyverno.sync.crowdin.translation.GoogleSheetsTranslationManager;
import ua.wyverno.crowdin.util.LanguageTranslationsUtils;
import ua.wyverno.utils.execution.ExecutionTimer;
import ua.wyverno.utils.execution.ExecutionTimerFactory;
import ua.wyverno.utils.json.JSONCreator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseImportTranslationService {
    private final static Logger logger = LoggerFactory.getLogger(BaseImportTranslationService.class);

    private final GoogleSheetsTranslationManager sheetsTranslationManager;
    private final CrowdinTranslationManager translationManager;
    private final CrowdinStringsManager stringsManager;
    private final LanguageTranslationsUtils translationsUtils;
    private final ExecutionTimerFactory executionTimerFactory;
    private final JSONCreator jsonCreator;

    public BaseImportTranslationService(GoogleSheetsTranslationManager sheetsTranslationManager,
                                        CrowdinTranslationManager translationManager,
                                        CrowdinStringsManager stringsManager,
                                        LanguageTranslationsUtils translationsUtils,
                                        ExecutionTimerFactory executionTimerFactory,
                                        JSONCreator jsonCreator) {
        this.sheetsTranslationManager = sheetsTranslationManager;
        this.translationManager = translationManager;
        this.stringsManager = stringsManager;
        this.translationsUtils = translationsUtils;
        this.executionTimerFactory = executionTimerFactory;
        this.jsonCreator = jsonCreator;
    }

    /**
     * Імпортує переклад з таблиці до Кроудіна.<br/>
     * Залежно від реалізації {@link #processImport(List, Map, AtomicInteger, Set)} processImport(List, Map, AtomicInteger)} успадкованого класса
     * буде певним чином відбуватись імпорт перекладу.
     */
    public void importTranslationsToCrowdin() {
        logger.info("Downloading and parsing sheet keys...");
        ExecutionTimer timerDownload = this.executionTimerFactory.createTimer();
        timerDownload.start();
        Map<String, GSheetTranslateKey> mapGSheetKeysById = this.sheetsTranslationManager.getTranslationsKeys();
        timerDownload.end();
        logger.info("Downloaded and parsed sheet keys. Total time: {}.", timerDownload.getDetailFormattedDuration());

        logger.info("Getting all source strings.");
        ExecutionTimer stringsGetTimer = this.executionTimerFactory.createTimer();
        stringsGetTimer.start();
        List<SourceString> sourceStrings = this.stringsManager.getListSourceString();
        stringsGetTimer.end();
        logger.info("Got all the Source Strings, the number of: {}. Total time: {}.", sourceStrings.size(), stringsGetTimer.getDetailFormattedDuration());


        logger.info("Getting all approve translations in Crowdin project.");
        ExecutionTimer approveTimer = this.executionTimerFactory.createTimer();
        approveTimer.start();
        List<LanguageTranslations> approveTranslations = this.translationManager.getApprovalTranslations();
        approveTimer.end();
        logger.info("Converting list with an approved translation to a Set with the StringId of these translations.\n" +
                "Total time: {}", approveTimer.getDetailFormattedDuration());


        Set<Long> approveStringIds = this.translationsUtils.listTranslationsToStringId(approveTranslations);
        logger.info("Converted approve translations to Set with StringId. Size: {}", approveStringIds.size());
        logger.info("Importing translation.");

        ExecutionTimer importTimer = this.executionTimerFactory.createTimer();
        importTimer.start();
        AtomicInteger counter = new AtomicInteger(0);
        this.processImport(sourceStrings, mapGSheetKeysById, counter, approveStringIds);
        importTimer.end();
        logger.info("Total time for import: {}.", importTimer.getDetailFormattedDuration());
    }

    /**
     * Імпорт певного перекладу
     * @param sourceString вихідний рядок
     * @param key ключ перекладу з Аркуша
     * @param approveStringIds айді вихідних рядків, які мають затверджений переклад
     */
    private void importTranslation(SourceString sourceString, TranslateKey key, Set<Long> approveStringIds) {
        if (key.translation().isEmpty()) {
            logger.trace("CrowdinTranslation not found in Sheet for SourceString: {}.", sourceString.getIdentifier());
            return;
        }
        List<StringTranslation> translations = this.translationManager.getTranslationsForString(sourceString);
        StringTranslation crowdinTranslation = this.translationsUtils.findCrowdinTranslation(translations, key.translation());
        if (crowdinTranslation != null) {
            if (!key.isApprove()) {
                logger.trace("{} - already has translation, but not have approve.", sourceString.getIdentifier());
                return;
            }
            if (!approveStringIds.contains(sourceString.getId())) { // Якщо переклад не має затвердження будь-якого, тоді додаємо затвердження
                logger.trace("Adding approve for: {} Approved: {}",
                        sourceString.getIdentifier(),
                        crowdinTranslation.getText());
                this.translationManager.addApproveTranslation(crowdinTranslation.getId());
            } else {
                logger.trace("{} - Already has approval.", sourceString.getIdentifier());
            }
            return;
        }
        logger.trace("Creating translation for: {}, CrowdinTranslation: {}", sourceString.getIdentifier(), key.translation());
        StringTranslation stringTranslation = this.translationManager.addTranslation(sourceString, key.translation());
        if (key.isApprove()) {
            if (!approveStringIds.contains(sourceString.getId())) { // Якщо переклад не має затвердження будь-якого, тоді додаємо затвердження
                logger.trace("Approving translation: {}, CrowdinTranslation: {}", sourceString.getIdentifier(), key.translation());
                this.translationManager.addApproveTranslation(stringTranslation.getId());
            }
        }
    }

    /**
     * Процес імпорту який має бути реалізований в успадкованому класі.<br/>
     * Після модифікацій з параметрами - потрібно викликати {@link BaseImportTranslationService#importTranslations(List, Map, AtomicInteger, Set, int)}, щоб запустити імпорт перекладу
     * @param sourceStrings лист з вихідними рядками які потрібно імпортувати переклад
     * @param mapGSheetKeysById мапа де ключ це Айді, а значення ключ перекладу з Аркуша
     * @param counter Атомарний лічильник, для асинхронного лічильника
     * @param approveStringIds айді вихідних рядків, які мають затверджений переклад
     */
    protected abstract void processImport(List<SourceString> sourceStrings,
                                          Map<String, GSheetTranslateKey> mapGSheetKeysById,
                                          AtomicInteger counter,
                                          Set<Long> approveStringIds);

    /**
     * Імпорт певних вихідних рядків.<br/>
     * Потрібно цей метод викликати з {@link BaseImportTranslationService#processImport(List, Map, AtomicInteger, Set)}, щоб запустити процес імпорту
     * @param sourceStrings лист з рядками для яких потрібно імпортувати переклад
     * @param mapGSheetKeysById мапа де ключ це Айді, а значення ключ перекладу з Аркуша
     * @param counter Атомарний лічильник, для асинхронного лічильника
     * @param sizeSourceStrings загальна кількість вихідних рядків, потрібно вказати, через те, що цей метод може виконуватись асинхронно, одразу великий пакет
     * @param approveStringIds айді вихідних рядків, які мають затверджений переклад
     */
    protected void importTranslations(List<SourceString> sourceStrings,
                                      Map<String, GSheetTranslateKey> mapGSheetKeysById,
                                      AtomicInteger counter,
                                      Set<Long> approveStringIds,
                                      final int sizeSourceStrings) {
        sourceStrings.forEach(string -> {
            try {
                if (mapGSheetKeysById.containsKey(string.getIdentifier())) {
                    this.importTranslation(string, mapGSheetKeysById.get(string.getIdentifier()), approveStringIds);
                    logger.info("Processed: {} Remaining: {}/{}",
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
