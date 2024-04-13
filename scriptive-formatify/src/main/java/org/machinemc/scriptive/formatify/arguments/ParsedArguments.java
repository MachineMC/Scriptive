package org.machinemc.scriptive.formatify.arguments;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ParsedArguments {

    private final Map<String, Object> parsedArguments;
    private final String error;

    public ParsedArguments(Map<String, Object> parsedArguments) {
        this(parsedArguments, null);
    }

    public ParsedArguments(String error) {
        this(null, error);
    }

    private ParsedArguments(Map<String, Object> parsedArguments, String error) {
        this.parsedArguments = parsedArguments;
        this.error = error;
    }

    @SuppressWarnings("unchecked")
    public <T> @Nullable T getArgument(String identifier) {
        T argument = (T) parsedArguments.get(identifier);
        if (argument == null)
            throw new IllegalStateException("Cannot find argument '" + identifier + '\'');
        return argument;
    }

    public boolean successful() {
        return error == null;
    }

    public @Nullable String error() {
        return error;
    }

}
