package ua.wyverno.localization.builder.key;

import ua.wyverno.localization.model.key.GSheetTranslateRegistryKey;
import ua.wyverno.localization.model.key.TranslationIdentifier;

public class GSheetTranslateRegistryKeyBuilder {
    private String containerId;
    private String key = "";
    private final StringBuilder originalText = new StringBuilder();
    private final StringBuilder translateText = new StringBuilder();
    private String context = "";
    private boolean isApprove = false;
    private String sheetLocationA1;

    public GSheetTranslateRegistryKeyBuilder containerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    public GSheetTranslateRegistryKeyBuilder key(String key) {
        this.key = key;
        return this;
    }

    public GSheetTranslateRegistryKeyBuilder appendOriginalText(String originalText) {
        originalText = originalText.replaceAll("\n", "");
        this.originalText.append(originalText).append("\\n\n");
        return this;
    }

    public GSheetTranslateRegistryKeyBuilder appendTranslateText(String translateText) {
        translateText = translateText.replaceAll("\\n", "");
        this.translateText.append(translateText).append("\\n\n");
        return this;
    }

    public GSheetTranslateRegistryKeyBuilder context(String context) {
        this.context = context;
        return this;
    }

    public GSheetTranslateRegistryKeyBuilder setIsApprove(boolean isApprove) {
        this.isApprove = isApprove;
        return this;
    }

    public GSheetTranslateRegistryKeyBuilder sheetLocationA1(String sheetLocationA1) {
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

    public String getSheetLocationA1() {
        return sheetLocationA1;
    }

    public GSheetTranslateRegistryKey build() {
        TranslationIdentifier identifier = new TranslationIdentifier(Integer.parseInt(this.containerId), this.key);
        return new GSheetTranslateRegistryKey(identifier,
                this.originalText.toString().replaceAll("\\\\n\\n$", ""),
                this.translateText.toString().replaceAll("\\\\n\\n$", ""),
                this.context.replaceAll("\\n$", ""),
                this.isApprove,
                this.sheetLocationA1);
    }
}
