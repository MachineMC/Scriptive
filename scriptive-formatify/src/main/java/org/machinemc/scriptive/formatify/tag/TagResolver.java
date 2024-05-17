package org.machinemc.scriptive.formatify.tag;

import org.machinemc.scriptive.components.Component;
import org.machinemc.scriptive.formatify.Formatify;
import org.machinemc.scriptive.formatify.exceptions.ParseException;
import org.machinemc.scriptive.formatify.parameter.Parameter;
import org.machinemc.scriptive.formatify.parameter.ArgumentQueue;
import org.machinemc.scriptive.formatify.parameter.Arguments;
import org.machinemc.scriptive.formatify.tag.Resolver.ParameterInfo;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

public interface TagResolver {

    Optional<Tag> resolve(String tagName, ArgumentQueue arguments) throws ParseException;

    static TagResolver defaults() {
        return resolvers(
            DefaultTags.BOLD,
            DefaultTags.OBFUSCATED,
            DefaultTags.ITALIC,
            DefaultTags.UNDERLINED,
            DefaultTags.STRIKETHROUGH,
            DefaultTags.FONT,
            DefaultTags.INSERTION,
            DefaultTags.CLICK_EVENT,
            DefaultTags.HOVER_EVENT,
            DefaultTags.KEYBIND,
            DefaultTags.TRANSLATION,
            DefaultTags.COLOR
        );
    }

    static TagResolver resolver(String name, Tag tag, String... aliases) {
        String[] names = new String[aliases.length + 1];
        names[0] = name;
        System.arraycopy(aliases, 0, names, 1, aliases.length);
        return resolver(Set.of(names), tag);
    }

    static TagResolver resolver(Set<String> names, Tag tag) {
        return resolver(names::contains, tag);
    }

    static TagResolver resolver(Predicate<String> predicate, Tag tag) {
        return (tagName, arguments) -> Optional.of(tagName).filter(predicate).map(s -> tag);
    }

    static TagResolver resolvers(TagResolver... resolvers) {
        return resolvers(Arrays.asList(resolvers));
    }

    static TagResolver resolvers(Collection<TagResolver> resolvers) {
        return new Resolvers(resolvers);
    }

    static Builder builder() {
        return new Builder();
    }

    class Builder {

        private final Set<String> names = new HashSet<>();
        private final Queue<ParameterInfo<?>> parameters = new LinkedList<>();
        private BiConsumer<Component, Arguments> componentUpdater;

        public Builder name(String name) {
            return names(name);
        }

        public Builder names(String... names) {
            return names(Set.of(names));
        }

        public Builder names(Set<String> names) {
            this.names.addAll(names);
            return this;
        }

        public Builder parameter(String name, Parameter<?> parameter) {
            parameters.offer(new ParameterInfo<>(name, parameter, false));
            return this;
        }

        public Builder optionalParameter(String name, Parameter<?> parameter) {
            parameters.offer(new ParameterInfo<>(name, parameter, true));
            return this;
        }

        public Builder componentUpdater(BiConsumer<Component, Arguments> componentUpdater) {
            this.componentUpdater = componentUpdater;
            return this;
        }

        public TagResolver build() {
            return new Resolver(names, parameters, componentUpdater);
        }

    }

}

class Resolver implements TagResolver {

    private final Set<String> names;
    private final Queue<ParameterInfo<?>> parameters;
    private final BiConsumer<Component, Arguments> componentUpdater;

    Resolver(Set<String> names, Queue<ParameterInfo<?>> parameters, BiConsumer<Component, Arguments> componentUpdater) {
        this.names = names;
        this.parameters = parameters;
        this.componentUpdater = componentUpdater;
    }

    @Override
    public Optional<Tag> resolve(String tagName, ArgumentQueue arguments) throws ParseException {
        if (!names.contains(tagName)) return Optional.empty();
        Map<String, Object> parsedArguments = HashMap.newHashMap(arguments.size());
        while (!arguments.isEmpty()) {
            String argument = arguments.poll();
            ParameterInfo<?> parameterInfo = parameters.poll();
            if (parameterInfo == null) throw new ParseException("Too many arguments");
            try {
                Object parsed = parameterInfo.parameter().parse(argument);
                parsedArguments.put(parameterInfo.name(), parsed);
            } catch (ParseException e) {
                if (!parameterInfo.optional()) throw e;
                // retry the argument with the next parameter
                arguments.offer(argument);
            }
        }
        for (ParameterInfo<?> parameter : parameters) {
            if (parameter.optional()) continue;
            throw new ParseException("Not enough arguments");
        }
        Formatify formatify = arguments.formatify();
        return Optional.of(component -> componentUpdater.accept(component, new Arguments(formatify, parsedArguments)));
    }

    record ParameterInfo<T>(String name, Parameter<T> parameter, boolean optional) {}

}

class Resolvers implements TagResolver {

    Collection<TagResolver> resolvers;

    public Resolvers(Collection<TagResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public Optional<Tag> resolve(String tagName, ArgumentQueue arguments) throws ParseException {
        ParseException exception = null;
        for (TagResolver resolver : resolvers) {
            ArgumentQueue cloneArguments = new ArgumentQueue(arguments);
            Optional<Tag> tag;
            try {
                tag = resolver.resolve(tagName, cloneArguments);
            } catch (ParseException e) {
                if (exception == null) exception = e;
                continue;
            }
            if (tag.isEmpty()) continue;
            for (int i = arguments.size() - cloneArguments.size(); i > 0; i--)
                arguments.poll();
            return tag;
        }
        if (exception != null) throw exception;
        return Optional.empty();
    }

}
