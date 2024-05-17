package org.machinemc.scriptive.formatify.parameter;

import org.machinemc.scriptive.formatify.Formatify;

import java.util.Map;
import java.util.Optional;

public class Arguments {

    private final Formatify formatify;
    private final Map<String, Object> arguments;

    public Arguments(Formatify formatify, Map<String, Object> arguments) {
        this.formatify = formatify;
        this.arguments = arguments;
    }

    public <T> T get(String name) {
        return get0(name);
    }

    public <T> Optional<T> getOptional(String name) {
        return Optional.ofNullable(get0(name));
    }

    private <T> T get0(String name) {
        //noinspection unchecked
        return (T) arguments.get(name);
    }

    public Formatify formatify() {
        return formatify;
    }

}
