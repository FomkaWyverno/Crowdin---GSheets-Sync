package ua.wyverno.google.sheets.util.execptions;

public class BadA1RangeNotationException extends RuntimeException {
    public BadA1RangeNotationException() {
    }

    public BadA1RangeNotationException(String message) {
        super(message);
    }

    public BadA1RangeNotationException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadA1RangeNotationException(Throwable cause) {
        super(cause);
    }

    public BadA1RangeNotationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
