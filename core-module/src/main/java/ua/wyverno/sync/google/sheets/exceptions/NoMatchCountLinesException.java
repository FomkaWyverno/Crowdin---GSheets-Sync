package ua.wyverno.sync.google.sheets.exceptions;

public class NoMatchCountLinesException extends IllegalStateException {
    public NoMatchCountLinesException() {
    }

    public NoMatchCountLinesException(String s) {
        super(s);
    }

    public NoMatchCountLinesException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMatchCountLinesException(Throwable cause) {
        super(cause);
    }
}
