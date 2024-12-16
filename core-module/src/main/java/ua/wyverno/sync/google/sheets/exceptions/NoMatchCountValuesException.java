package ua.wyverno.sync.google.sheets.exceptions;

public class NoMatchCountValuesException extends IllegalStateException {
    public NoMatchCountValuesException() {
    }

    public NoMatchCountValuesException(String s) {
        super(s);
    }

    public NoMatchCountValuesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchCountValuesException(Throwable cause) {
        super(cause);
    }
}
