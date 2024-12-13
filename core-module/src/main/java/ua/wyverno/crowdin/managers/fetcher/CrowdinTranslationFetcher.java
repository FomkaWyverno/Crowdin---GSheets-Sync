package ua.wyverno.crowdin.managers.fetcher;

import com.crowdin.client.sourcestrings.model.SourceString;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.google.common.base.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.crowdin.managers.CrowdinStringsManager;
import ua.wyverno.crowdin.managers.CrowdinTranslationManager;
import ua.wyverno.crowdin.model.CrowdinTranslation;
import ua.wyverno.crowdin.util.LanguageTranslationsUtils;
import ua.wyverno.utils.json.JSONCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CrowdinTranslationFetcher {

    private final CrowdinTranslationManager translationManager;
    private final CrowdinStringsManager stringsManager;

    private final LanguageTranslationsUtils translationsUtils;
    private final JSONCreator jsonCreator;

    @Autowired
    public CrowdinTranslationFetcher(CrowdinTranslationManager translationManager,
                                     CrowdinStringsManager stringsManager,
                                     LanguageTranslationsUtils translationsUtils,
                                     JSONCreator jsonCreator) {
        this.translationManager = translationManager;
        this.stringsManager = stringsManager;
        this.translationsUtils = translationsUtils;
        this.jsonCreator = jsonCreator;
    }

    /**
     * Отримує всі переклади з Кроудіна
     * @return Повертає лист з перекладами на Кроудін {@link CrowdinTranslation}
     */
    public List<CrowdinTranslation> fetchTranslations() {
        Map<Long, SourceString> keyIdentifierByStringId = this.stringsManager.getListSourceString().stream()
                .collect(Collectors.toMap(SourceString::getId, Functions.identity()));
        List<LanguageTranslations> translationsWithoutApprove = this.translationManager.getTranslationsWithoutApproval();
        List<LanguageTranslations> translationsWithApprove = this.translationManager.getApprovalTranslations();

        List<CrowdinTranslation> translations =
                new ArrayList<>(this.toCrowdinTranslations(translationsWithoutApprove, keyIdentifierByStringId, false));
        translations.addAll(this.toCrowdinTranslations(translationsWithApprove, keyIdentifierByStringId, true));

        return translations;
    }

    /**
     * Перетворює лист з перекладами які виглядають як {@link LanguageTranslations} на власну реалізацію відображення перекладу {@link CrowdinTranslation}
     * @param translations переклади безпосередньо з Кроудін
     * @param sourceStringByStringId мапа де ключ це айді вихідного рядка, а значення сам вихідний рядок
     * @param isApprove лист чи є затвердженим перекладом
     * @return Лист з перетвореним об'єктом на {@link CrowdinTranslation}
     */
    private List<CrowdinTranslation> toCrowdinTranslations(List<LanguageTranslations> translations, Map<Long, SourceString> sourceStringByStringId, boolean isApprove) {
        return translations.stream()
                .map(translationObj -> {
                    long stringId = this.translationsUtils.getStringId(translationObj);
                    if (!sourceStringByStringId.containsKey(stringId))
                        throw new IllegalStateException("Translation for not exists Source String. JSON: " + this.jsonCreator.toJSON(translationObj));
                    SourceString sourceString = sourceStringByStringId.get(stringId);
                    String translation = this.translationsUtils.getTranslation(translationObj);
                    return new CrowdinTranslation(sourceString, translation, isApprove);
                }).toList();
    }
}
