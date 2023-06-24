package org.machinemc.scriptive.formatify.tags;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.arguments.Argument;
import org.machinemc.scriptive.formatify.arguments.ParsedArguments;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Tag {

    private final String identifier;
    private final Set<String> aliases;
    protected final LinkedHashMap<String, Argument<?>> arguments;
    private final ComponentUpdater updater;
    private final @Nullable Parser tagParser;

    protected Tag(
            String identifier, Set<String> aliases,
            LinkedHashMap<String, Argument<?>> arguments,
            ComponentUpdater updater,
            @Nullable Parser tagParser
    ) {
        this.identifier = identifier;
        this.aliases = aliases;
        this.arguments = arguments;
        this.updater = updater;
        this.tagParser = tagParser;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public @UnmodifiableView Map<String, Argument<?>> getArguments() {
        return Collections.unmodifiableMap(arguments);
    }

    public ComponentUpdater getUpdater() {
        return updater;
    }

    public @Nullable Parser getTagParser() {
        return tagParser;
    }

    public static Builder builder(String identifier, String... aliases) {
        return new Builder(identifier, aliases);
    }

    public static Tag noArg(ComponentUpdater updater, String identifier, String... aliases) {
        return new Builder(identifier, aliases)
                .update(updater)
                .build();
    }

    public static class Builder {

        private final String identifier;
        private final Set<String> aliases;
        private final LinkedHashMap<String, Argument<?>> arguments = new LinkedHashMap<>();
        private ComponentUpdater updater = (component, parsedArguments) -> {};
        private @Nullable Parser tagParser;

        private Builder(String identifier, String[] aliases) {
            this.identifier = identifier;
            this.aliases = Set.of(aliases);
        }

        public Builder argument(String name, Argument<?> argument) {
            arguments.put(name, argument);
            return this;
        }

        public Builder tagParser(Parser tagParser) {
            this.tagParser = tagParser;
            return this;
        }

        public Builder update(ComponentUpdater updater) {
            this.updater = updater;
            return this;
        }

        public Tag build() {
            return new Tag(identifier, aliases, arguments, updater, tagParser);
        }

    }

    @FunctionalInterface
    public interface ComponentUpdater {

        void updateComponent(TextComponent component, ParsedArguments arguments);

    }

    @FunctionalInterface
    public interface Parser {

        ParsedArguments parse(String label, String[] args);

    }

}
