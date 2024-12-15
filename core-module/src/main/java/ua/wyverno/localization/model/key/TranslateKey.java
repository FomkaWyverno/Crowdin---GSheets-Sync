package ua.wyverno.localization.model.key;

import java.util.Objects;

public class TranslateKey {
    private final TranslationIdentifier identifier;
    private final String originalText;
    private final String translate;
    private final String context;
    private final boolean isApprove;

    /**
     * Модель ключа локалізації
     *
     * @param identifier   унікальний ключ цього тексту локалізації
     * @param originalText текст локалізації
     * @param translate    переклад в таблиці
     * @param context      контекст цього ключа локалізації
     * @param isApprove    чи затверджений цей переклад
     */
    public TranslateKey(
            TranslationIdentifier identifier,
            String originalText,
            String translate,
            String context,
            boolean isApprove) {
        this.identifier = identifier;
        this.originalText = originalText;
        this.translate = translate;
        this.context = context;
        this.isApprove = isApprove;
    }

    public TranslationIdentifier identifier() {
        return identifier;
    }

    public String originalText() {
        return originalText;
    }

    public String translate() {
        return translate;
    }

    public String context() {
        return context;
    }

    public boolean isApprove() {
        return isApprove;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (TranslateKey) obj;
        return Objects.equals(this.identifier, that.identifier) &&
                Objects.equals(this.originalText, that.originalText) &&
                Objects.equals(this.translate, that.translate) &&
                Objects.equals(this.context, that.context) &&
                this.isApprove == that.isApprove;
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier, originalText, translate, context, isApprove);
    }

    @Override
    public String toString() {
        return "TranslateKey[" +
                "identifier=" + identifier + ", " +
                "originalText=" + originalText + ", " +
                "translate=" + translate + ", " +
                "context=" + context + ", " +
                "isApprove=" + isApprove + ']';
    }
}
