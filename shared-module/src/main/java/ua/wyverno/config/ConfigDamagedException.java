package ua.wyverno.config;

public class ConfigDamagedException extends Exception {
    public ConfigDamagedException() {
        super();
    }

    public ConfigDamagedException(String message) {
        super(message);
    }

    public ConfigDamagedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigDamagedException(Throwable cause) {
        super(cause);
    }

    protected ConfigDamagedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
