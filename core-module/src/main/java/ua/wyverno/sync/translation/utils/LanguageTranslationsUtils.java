package ua.wyverno.sync.translation.utils;

import com.crowdin.client.stringtranslations.model.ICULanguageTranslations;
import com.crowdin.client.stringtranslations.model.LanguageTranslations;
import com.crowdin.client.stringtranslations.model.PlainLanguageTranslations;
import com.crowdin.client.stringtranslations.model.PluralLanguageTranslations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ua.wyverno.utils.json.JSONCreator;

import java.util.List;

@Component
public class LanguageTranslationsUtils {

    private final JSONCreator jsonCreator;

    @Autowired
    public LanguageTranslationsUtils(JSONCreator jsonCreator) {
        this.jsonCreator = jsonCreator;
    }

    /**
     * Повертає айді перекладу з LanguageTranslations
     *
     * @param translation переклад
     * @return айді перекладу
     */
    public long getTranslationId(LanguageTranslations translation) {
        if (translation instanceof PlainLanguageTranslations plainTranslation)
            return plainTranslation.getTranslationId();
        if (translation instanceof ICULanguageTranslations icuTranslation)
            return icuTranslation.getTranslationId();
        if (translation instanceof PluralLanguageTranslations pluralTranslation) {
            throw new UnsupportedOperationException(
                    "PluralLanguageTranslations unsupported class for matches translation!\nJSON: "
                            + this.jsonCreator.toJSON(pluralTranslation));
        }

        String messageError = String.format("Unsupported translations: %s%nJSON: %s",
                translation.getClass().getName(),
                this.jsonCreator.toJSON(translation));
        throw new IllegalStateException(messageError);

    }

    public String getTranslation(LanguageTranslations translation) {
        if (translation instanceof PlainLanguageTranslations plainTranslation)
            return plainTranslation.getText();
        if (translation instanceof ICULanguageTranslations icuTranslation)
            return icuTranslation.getText();
        if (translation instanceof PluralLanguageTranslations pluralTranslation) {
            throw new UnsupportedOperationException(
                    "PluralLanguageTranslations unsupported class for matches translation!\nJSON: "
                            + this.jsonCreator.toJSON(pluralTranslation));
        }
        throw new UnsupportedOperationException(translation.getClass().getName() +
                " unsupported class for matches translation!");
    }

    /**
     * Шукає переклад у Кроудіні, якщо існує повертає його, якщо ні поверне null
     *
     * @param crowdinTranslations переклади для одного вихідного рядка
     * @param sheetTranslation    переклад у аркуші
     * @return {@link LanguageTranslations} якщо був знайдений поверне переклад, Кроудіна, якщо не існує поверне null
     */
    public LanguageTranslations findCrowdinTranslation(List<LanguageTranslations> crowdinTranslations, String sheetTranslation) {
        if (crowdinTranslations.isEmpty()) return null; // Якщо перекладів для цього рядка не існує, тоді повертає false
        return crowdinTranslations.stream()
                .filter(translation -> this.matchesTranslation(sheetTranslation, translation))
                .findFirst().orElse(null);
    }

    /**
     * Перевіряє чи однаковий переклад
     *
     * @param sheetTranslation   переклад з Аркуша
     * @param crowdinTranslation переклад з Кроудіна
     * @return true якщо переклад однаковий, інакше false
     */
    private boolean matchesTranslation(String sheetTranslation, LanguageTranslations crowdinTranslation) {
        if (crowdinTranslation instanceof PlainLanguageTranslations plainTranslation) {
            return plainTranslation.getText().equals(sheetTranslation);
        }
        if (crowdinTranslation instanceof ICULanguageTranslations icuTranslation) {
            return icuTranslation.getText().equals(sheetTranslation);
        }
        if (crowdinTranslation instanceof PluralLanguageTranslations pluralTranslations) {
            throw new UnsupportedOperationException(
                    "PluralLanguageTranslations unsupported class for matches translation!\nJSON: "
                            + this.jsonCreator.toJSON(pluralTranslations));
        }
        return false;
    }
}
