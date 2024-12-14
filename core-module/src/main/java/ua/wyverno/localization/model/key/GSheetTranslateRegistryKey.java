package ua.wyverno.localization.model.key;

public final class GSheetTranslateRegistryKey extends TranslateRegistryKey {
    private final String sheetLocationA1;
    /**
     * Модель ключа локалізації в гугл таблиці
     *
     * @param identifier   унікальний ключ цього тексту локалізації
     * @param originalText текст локалізації
     * @param translate    переклад в таблиці
     * @param context      контекст цього ключа локалізації
     * @param isApprove    чи затверджений цей переклад
     * @param sheetLocationA1 місце розташування в таблиці
     */
    public GSheetTranslateRegistryKey(TranslationIdentifier identifier, String originalText, String translate, String context, boolean isApprove, String sheetLocationA1) {
        super(identifier, originalText, translate, context, isApprove);
        this.sheetLocationA1 = sheetLocationA1;
    }

    public String sheetLocationA1() {
        return sheetLocationA1;
    }
}
