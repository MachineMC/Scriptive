package org.machinemc.scriptive.formatify.exceptions;

public class FormatifyException extends RuntimeException {

    public FormatifyException() {
    }

    public FormatifyException(String message) {
        super(message);
    }

    public FormatifyException(String message, Throwable cause) {
        super(message, cause);
    }

    public FormatifyException(Throwable cause) {
        super(cause);
    }

    public FormatifyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
