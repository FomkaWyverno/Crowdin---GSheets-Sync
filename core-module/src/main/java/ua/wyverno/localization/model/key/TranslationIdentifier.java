package ua.wyverno.localization.model.key;

public record TranslationIdentifier(int containerId, String key) {

    @Override
    public String toString() {
        return this.containerId + "." + this.key;
    }
}
