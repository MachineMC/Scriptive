package org.machinemc.scriptive.formatify.exceptions;

public class ParseException extends NoStacktraceException {

    public ParseException() {
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException withMessage(String message, Object... arguments) {
        return withMessage(String.format(message, arguments));
    }

    public ParseException withMessage(String message) {
        return new ParseException(message, getCause());
    }

}
