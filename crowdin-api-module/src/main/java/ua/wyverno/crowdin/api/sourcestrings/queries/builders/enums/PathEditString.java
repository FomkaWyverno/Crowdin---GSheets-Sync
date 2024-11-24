package ua.wyverno.crowdin.api.sourcestrings.queries.builders.enums;

public enum PathEditString {
    IDENTIFIER("/identifier"), TEXT("/text"), CONTEXT("/context"),
    IS_HIDDEN("/isHidden"), MAX_LENGTH("/maxLength"), LABEL_IDS("/labelIds");
    private final String value;

    PathEditString(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
