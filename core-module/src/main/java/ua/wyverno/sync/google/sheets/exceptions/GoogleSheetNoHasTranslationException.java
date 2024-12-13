package ua.wyverno.sync.google.sheets.exceptions;

public class GoogleSheetNoHasTranslationException extends IllegalStateException {
    public GoogleSheetNoHasTranslationException() {
    }

    public GoogleSheetNoHasTranslationException(String message) {
        super(message);
    }

    public GoogleSheetNoHasTranslationException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleSheetNoHasTranslationException(Throwable cause) {
        super(cause);
    }
}
