package ua.wyverno.localization.model.builders;

import ua.wyverno.localization.model.TranslateRegistryKey;
import ua.wyverno.localization.model.TranslationIdentifier;

public class TranslateRegistryKeyBuilder {
    private String containerId;
    private String key = "";
    private final StringBuilder originalText = new StringBuilder();
    private final StringBuilder translateText = new StringBuilder();
    private String context = "";
    private boolean isTranslate = false;
    private boolean isApprove = false;
    private String locationA1;

    public TranslateRegistryKeyBuilder containerId(String containerId) {
        this.containerId = containerId;
        return this;
    }

    public TranslateRegistryKeyBuilder key(String key) {
        this.key = key;
        return this;
    }

    public TranslateRegistryKeyBuilder appendOriginalText(String originalText) {
        this.originalText.append(originalText).append("\n");
        return this;
    }

    public TranslateRegistryKeyBuilder appendTranslateText(String translateText) {
        this.translateText.append(translateText).append("\n");
        return this;
    }

    public TranslateRegistryKeyBuilder context(String context) {
        this.context = context;
        return this;
    }

    public TranslateRegistryKeyBuilder setIsTranslate(boolean isTranslate) {
        this.isTranslate = isTranslate;
        return this;
    }

    public TranslateRegistryKeyBuilder setIsApprove(boolean isApprove) {
        this.isApprove = isApprove;
        return this;
    }

    public TranslateRegistryKeyBuilder locationA1(String locationA1) {
        this.locationA1 = locationA1;
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

    public boolean isTranslate() {
        return isTranslate;
    }

    public boolean isApprove() {
        return isApprove;
    }

    public String getLocationA1() {
        return locationA1;
    }

    public TranslateRegistryKey build() {
        TranslationIdentifier identifier = new TranslationIdentifier(Integer.parseInt(this.containerId), this.key);
        return new TranslateRegistryKey(identifier,
                this.originalText.toString().replaceAll("\\n$", ""),
                this.translateText.toString().replaceAll("\\n$",""),
                this.context.replaceAll("\\n$",""),
                this.isTranslate,
                this.isApprove,
                this.locationA1);
    }
}
