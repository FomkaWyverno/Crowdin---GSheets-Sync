package ua.wyverno.localization.model;

/**
 * Модель ключа локалізації
 * @param identifier унікальний ключ цього тексту локалізації
 * @param originalText текст локалізації
 * @param context контекст цього ключа локалізації
 * @param locationA1 місце знаходження ключа в таблиці записана в A1 форматі
 */
public record SourceRegistryKey(TranslationIdentifier identifier, String originalText, String context, String locationA1) { }
