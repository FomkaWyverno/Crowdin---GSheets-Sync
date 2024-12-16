package ua.wyverno.localization.builder.key;

import ua.wyverno.google.sheets.util.A1RangeNotation;
import ua.wyverno.localization.model.key.GSheetTranslateKey;
import ua.wyverno.localization.model.key.TranslationIdentifier;

public class GSheetTranslateKeyBuilder {
    private String containerId;
    private String key = "";
    private final StringBuilder originalText = new StringBuilder();
    private final StringBuilder translateText = new StringBuilder();
    private String context = "";
    private boolean isApprove = false;
    private A1RangeNotation sheetLocationA1;

    public GSheetTranslateKeyBuilder containerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    public GSheetTranslateKeyBuilder key(String key) {
        this.key = key;
        return this;
    }

    public GSheetTranslateKeyBuilder appendOriginalText(String originalText) {
        originalText = originalText.replaceAll("\n", "");
        this.originalText.append(originalText).append("\\n\n");
        return this;
    }

    public GSheetTranslateKeyBuilder appendTranslateText(String translateText) {
        translateText = translateText.replaceAll("\\n", "");
        this.translateText.append(translateText).append("\\n\n");
        return this;
    }

    public GSheetTranslateKeyBuilder context(String context) {
        this.context = context;
        return this;
    }

    public GSheetTranslateKeyBuilder setIsApprove(boolean isApprove) {
        this.isApprove = isApprove;
        return this;
    }

    public GSheetTranslateKeyBuilder sheetLocationA1(A1RangeNotation sheetLocationA1) {
        this.sheetLocationA1 = sheetLocationA1;
        return this;
    }

    public String getContainerId() {
        return containerId;
    }

    public String getKey() {
        return key;
    }

    public StringBuilder getOriginalText() {
        return originalText;
    }

    public StringBuilder getTranslateText() {
        return translateText;
    }

    public String getContext() {
        return context;
    }

    public boolean isApprove() {
        return isApprove;
    }

    public A1RangeNotation getSheetLocationA1() {
        return sheetLocationA1;
    }

    public GSheetTranslateKey build() {
        TranslationIdentifier identifier = new TranslationIdentifier(Integer.parseInt(this.containerId), this.key);
        return new GSheetTranslateKey(identifier,
                this.originalText.toString().replaceAll("\\\\n\\n$", ""),
                this.translateText.toString().replaceAll("\\\\n\\n$", ""),
                this.context.replaceAll("\\n$", ""),
                this.isApprove,
                this.sheetLocationA1);
    }
}
