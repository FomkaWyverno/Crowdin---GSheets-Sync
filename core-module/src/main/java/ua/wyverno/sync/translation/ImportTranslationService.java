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

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ImportTranslationService {
    private final static Logger logger = LoggerFactory.getLogger(ImportTranslationService.class);

    private final GoogleSheetsTranslationService sheetsTranslationService;
    private final CrowdinTranslationService translationService;
    private final LanguageTranslationsUtils translationsUtils;
    private final JSONCreator jsonCreator;


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
        AtomicInteger counter = new AtomicInteger(0);
        long startImportMs = System.currentTimeMillis();

        sourceStrings.forEach(string -> {
            if (mapGSheetKeysById.containsKey(string.getIdentifier())) {
                this.importTranslation(string, mapGSheetKeysById.get(string.getIdentifier()));
                counter.getAndIncrement();
                logger.info("Imported: {} Remaining: {}/{}", string.getIdentifier(), counter, sourceStrings.size());
            } else {
                logger.error("Missing source string in Sheet: {}. Source String JSON: {}",
                        string.getIdentifier(), this.jsonCreator.toJSON(string));
            }
        });

        long endImportMs = System.currentTimeMillis();
        logger.info("Total time for import: {}ms", endImportMs - startImportMs);
    }

    private void importTranslation(SourceString sourceString, TranslateRegistryKey key) {
        if (key.translate().isEmpty()) {
            logger.trace("Translation not found in Sheet for SourceString: {}.", sourceString.getIdentifier());
            return;
        }
        List<LanguageTranslations> translations = this.translationService.getTranslations(sourceString);
        LanguageTranslations crowdinTranslation = this.translationsUtils.findCrowdinTranslation(translations, key.translate());
        if (crowdinTranslation != null && key.isApprove()) {
            if (this.translationService.noApprovalString(sourceString)) { // Якщо переклад не має затвердження будь-якого, тоді додаємо затвердження
                this.translationService.addApproveTranslation(this.translationsUtils.getTranslationId(crowdinTranslation));
                logger.trace("Add approve for: {} Approved: {}",
                        sourceString.getIdentifier(),
                        this.translationsUtils.getTranslation(crowdinTranslation));
            } else {
                logger.trace("{} - Already has approval.", sourceString.getIdentifier());
            }
            return;
        }
        StringTranslation stringTranslation = this.translationService.addTranslation(sourceString, key.translate());
        logger.trace("Created translation for: {}, Translation: {}", sourceString.getIdentifier(), key.translate());
        if (key.isApprove()) {
            if (this.translationService.noApprovalString(sourceString)) { // Якщо переклад не має затвердження будь-якого, тоді додаємо затвердження
                this.translationService.addApproveTranslation(stringTranslation.getId());
                logger.trace("Approved translation: {}, Translation: {}", sourceString.getIdentifier(), key.translate());
            }
        }
    }
}
