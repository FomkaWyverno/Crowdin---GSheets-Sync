package ua.wyverno.crowdin.api.util.stringtranslation;

import com.crowdin.client.stringtranslations.model.LanguageTranslations;

import java.util.List;

public class LanguageTranslationUtils {

    /**
     * Фільтрує всі об'єкти в колекції за конкретним типом.
     *
     * @param translations Список об'єктів {@link LanguageTranslations}.
     * @param type         Клас, який треба фільтрувати.
     * @param <T>          Тип об'єкта.
     * @return             Список об'єктів конкретного типу.
     */
    public static <T extends LanguageTranslations> List<T> getAllTypes(List<LanguageTranslations> translations, Class<T> type) {
        return translations.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    /**
     * Повертає перший об'єкт певного типу.
     *
     * @param translations Список об'єктів {@link LanguageTranslations}.
     * @param type         Клас, який треба знайти.
     * @param <T>          Тип об'єкта.
     * @return Перший об'єкт конкретного типу або null, якщо його немає.
     */
    public static <T extends LanguageTranslations> T getFirstOfType(List<LanguageTranslations> translations, Class<T> type) {
        return translations.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }

    /**
     * Перевіряє, чи є хоча б один об'єкт певного типу в колекції.
     *
     * @param translations Список об'єктів {@link LanguageTranslations}.
     * @param type         Клас, наявність якого треба перевірити.
     * @param <T>          Тип об'єкта.
     * @return true, якщо хоча б один об'єкт типу T знайдено.
     */
    public static <T extends LanguageTranslations> boolean containsType(List<LanguageTranslations> translations, Class<T> type) {
        return translations.stream().anyMatch(type::isInstance);
    }
}
