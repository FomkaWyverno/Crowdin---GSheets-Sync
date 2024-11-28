package ua.wyverno.google.sheets.util.execptions;

public class GoogleSheetHeaderIncorrectException extends RuntimeException {
    public GoogleSheetHeaderIncorrectException() {
        super();
    }

    public GoogleSheetHeaderIncorrectException(String message) {
        super(message);
    }

    public GoogleSheetHeaderIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }

    public GoogleSheetHeaderIncorrectException(Throwable cause) {
        super(cause);
    }

    protected GoogleSheetHeaderIncorrectException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
