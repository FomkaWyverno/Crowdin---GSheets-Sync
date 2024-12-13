package ua.wyverno.sync.google.sheets.exceptions;

public class GoogleSheetHeaderNoExistsException extends RuntimeException {
    public GoogleSheetHeaderNoExistsException() {
    }

    public GoogleSheetHeaderNoExistsException(String message) {
        super(message);
    }

    public GoogleSheetHeaderNoExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleSheetHeaderNoExistsException(Throwable cause) {
        super(cause);
    }

    public GoogleSheetHeaderNoExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
