package ua.wyverno.sync.crowdin.directories;

public class AmbiguousDirectoryException extends RuntimeException {
    public AmbiguousDirectoryException(String message) {
        super(message);
    }

  public AmbiguousDirectoryException(String message, Throwable cause) {
    super(message, cause);
  }

  public AmbiguousDirectoryException(Throwable cause) {
    super(cause);
  }

  public AmbiguousDirectoryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public AmbiguousDirectoryException() {
  }
}
