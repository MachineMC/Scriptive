package org.machinemc.scriptive.formatify.exceptions;

public class NoStacktraceException extends RuntimeException {

    public NoStacktraceException() {
        this(null, null);
        System.out.println("A");
    }

    public NoStacktraceException(String message) {
        this(message, null);
    }

    public NoStacktraceException(Throwable cause) {
        this(cause != null ? cause.getMessage() : null, cause);
    }

    public NoStacktraceException(String message, Throwable cause) {
        super(message, cause, false, false);
    }

}
