package ua.wyverno.sync.translation.managers;

import com.crowdin.client.sourcestrings.model.SourceString;
import com.crowdin.client.stringtranslations.model.Approval;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.crowdin.client.stringtranslations.model.StringTranslation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.config.ConfigLoader;
import ua.wyverno.crowdin.CrowdinService;

import java.util.List;

@Component
public class CrowdinTranslationManager {
    private final static Logger logger = LoggerFactory.getLogger(CrowdinTranslationManager.class);

    private final CrowdinService crowdinService;
    private final long projectId;
    private final String languageId;

    @Autowired
    public CrowdinTranslationManager(CrowdinService crowdinService, ConfigLoader configLoader) {
        this.crowdinService = crowdinService;
        this.projectId = configLoader.getCoreConfig().getProjectID();
        this.languageId = configLoader.getCoreConfig().getLanguageId();
    }

    /**
     * Отримання всіх вихідних рядків
     *
     * @return Лист з усіма вихідними рядками
     */
    public List<SourceString> getListSourceString() {
        logger.trace("Getting list source strings from Crowdin Project.");
        return this.crowdinService.sourceStrings()
                .list(this.projectId)
                .execute();
    }

    /**
     * Отримує список перекладів для вихідного рядка
     *
     * @param sourceString вихідний рядок
     * @return лист з перекладами
     */
    public List<LanguageTranslations> getTranslations(SourceString sourceString) {
        logger.trace("Getting Translations for: {}", sourceString.getIdentifier());
        return this.crowdinService.string_translations()
                .listLanguageTranslations(this.projectId)
                .stringIds(sourceString.getId().toString())
                .languageId(this.languageId)
                .execute();
    }

    /**
     * Перевіряє чи у Вихідного рядка є затверджений переклад
     *
     * @param sourceString вихідний рядок
     * @return якщо є затверджений переклад поверне true, якщо немає затвердженого перекладу поверне false
     */
    public boolean noApprovalString(SourceString sourceString) {
        logger.trace("Checking for no approve source string: {}", sourceString.getIdentifier());
        return this.crowdinService.string_translations()
                .listLanguageTranslations(this.projectId)
                .languageId(this.languageId)
                .maxResults(1)
                .limitAPI(1)
                .stringIds(sourceString.getId().toString())
                .approvedOnly(true)
                .execute().isEmpty();
    }

    /**
     * Додає переклад для певного вихідного рядка
     *
     * @param sourceString вихідний рядок якому потрібно додати переклад
     * @param translation  переклад який потрібно встановити цьому вихідному рядку
     * @return повертає об'єкт перекладу який був створений у Кроудіні
     */
    public StringTranslation addTranslation(SourceString sourceString, String translation) {
        logger.trace("Add translation: {}\nTranslation: {}", sourceString.getIdentifier(), translation);
        return this.crowdinService.string_translations()
                .addTranslation(this.projectId)
                .stringId(sourceString.getId())
                .languageId(this.languageId)
                .text(translation)
                .execute();
    }

    /**
     * Затверджує переклад
     *
     * @param translationId айді перекладу який потрібно затвердити
     */
    public Approval addApproveTranslation(long translationId) {
        logger.trace("Add approve for translationId: {}", translationId);
        return this.crowdinService.string_translations()
                .addApproval(this.projectId)
                .translationId(translationId)
                .execute();
    }
}
