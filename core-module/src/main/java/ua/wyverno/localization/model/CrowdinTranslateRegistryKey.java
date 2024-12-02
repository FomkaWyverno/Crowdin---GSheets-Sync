package ua.wyverno.localization.model;

public class CrowdinTranslateRegistryKey extends TranslateRegistryKey {
    private final String crowdinSourceStringId;
    /**
     * Модель ключа локалізації
     *
     * @param identifier   унікальний ключ цього тексту локалізації
     * @param originalText текст локалізації
     * @param translate    переклад в таблиці
     * @param context      контекст цього ключа локалізації
     * @param isApprove    чи затверджений цей переклад
     * @param crowdinSourceStringId айді рядка у Кроудіні
     */
    public CrowdinTranslateRegistryKey(TranslationIdentifier identifier, String originalText, String translate, String context, boolean isApprove, String crowdinSourceStringId) {
        super(identifier, originalText, translate, context, isApprove);
        this.crowdinSourceStringId = crowdinSourceStringId;
    }

    public String crowdinSourceStringId() {
        return crowdinSourceStringId;
    }
}
