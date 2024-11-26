package ua.wyverno.localization.model;

public record SourceRegistryKey(TranslationIdentifier identifier, String originalText, String timing, String voice, String dub) {
}
