package ua.wyverno.localization.model.key;

import ua.wyverno.google.sheets.util.A1RangeNotation;

public final class GSheetTranslateKey extends TranslateKey {
    private final A1RangeNotation locationA1;
    /**
     * Модель ключа локалізації в гугл таблиці
     *
     * @param identifier   унікальний ключ цього тексту локалізації
     * @param originalText текст локалізації
     * @param translate    переклад в таблиці
     * @param context      контекст цього ключа локалізації
     * @param isApprove    чи затверджений цей переклад
     * @param locationA1 місце розташування в таблиці
     */
    public GSheetTranslateKey(TranslationIdentifier identifier, String originalText, String translate, String context, boolean isApprove, A1RangeNotation locationA1) {
        super(identifier, originalText, translate, context, isApprove);
        this.locationA1 = locationA1;
    }

    public A1RangeNotation locationA1() {
        return locationA1;
    }
}
