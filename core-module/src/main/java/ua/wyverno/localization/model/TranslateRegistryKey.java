package ua.wyverno.localization.model;

/**
 * Модель ключа локалізації
 * @param identifier унікальний ключ цього тексту локалізації
 * @param originalText текст локалізації
 * @param translate переклад в таблиці
 * @param context контекст цього ключа локалізації
 * @param isTranslate чи перекладений цей текст
 * @param isApprove чи затверджений цей переклад
 * @param locationA1 місце знаходження ключа в таблиці записана в A1 форматі
 */
public record TranslateRegistryKey(
        TranslationIdentifier identifier,
        String originalText,
        String translate,
        String context,
        boolean isTranslate,
        boolean isApprove,
        String locationA1
) { }
