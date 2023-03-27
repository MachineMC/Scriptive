package me.tud.componentslibrary.exceptions;

public class MalformedHexCodeException extends IllegalArgumentException {

    public MalformedHexCodeException() {
    }

    public MalformedHexCodeException(String hex) {
        super("'" + hex + "' is not a valid hex code");
    }

    public MalformedHexCodeException(String hex, Throwable cause) {
        super("'" + hex + "' is not a valid hex code", cause);
    }

    public MalformedHexCodeException(Throwable cause) {
        super(cause);
    }

}
