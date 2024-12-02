package ua.wyverno.localization.model;

public record TranslationIdentifier(int containerId, String key) {

    @Override
    public String toString() {
        return this.containerId + "." + this.key;
    }
}
