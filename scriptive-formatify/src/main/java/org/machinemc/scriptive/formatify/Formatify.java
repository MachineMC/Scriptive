package org.machinemc.scriptive.formatify;

import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.exceptions.ParseException;
import org.machinemc.scriptive.formatify.parser.FormatifyParser;
import org.machinemc.scriptive.formatify.tag.TagResolver;
import org.machinemc.scriptive.util.ChatUtils;

import java.util.Collection;
import java.util.function.Consumer;

public class Formatify {

    private final TagResolver tagResolver;
    private final Consumer<ParseException> errorHandler;
    private final boolean strict;

    private Formatify(TagResolver tagResolver, Consumer<ParseException> errorHandler, boolean strict) {
        this.tagResolver = tagResolver;
        this.errorHandler = errorHandler;
        this.strict = strict;
    }

    public TextComponent parse(String string) {
        return new FormatifyParser(this, string).parse();
    }

    public TagResolver tagResolver() {
        return tagResolver;
    }

    public Consumer<ParseException> errorHandler() {
        return errorHandler;
    }

    public boolean isStrict() {
        return strict;
    }

    public boolean isLenient() {
        return !strict;
    }

    public Formatify withTagResolver(TagResolver tagResolver) {
        return new Formatify(tagResolver, errorHandler, strict);
    }

    public Formatify withErrorHandler(Consumer<ParseException> errorHandler) {
        return new Formatify(tagResolver, errorHandler, strict);
    }

    public Formatify lenient() {
        return new Formatify(tagResolver, errorHandler, false);
    }

    public Formatify strict() {
        return new Formatify(tagResolver, errorHandler, true);
    }

    public static Formatify formatify() {
        return Formatify.builder()
            .tagResolver(TagResolver.defaults())
            .errorHandler(error -> System.err.println(error.getMessage()))
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private TagResolver tagResolver = TagResolver.defaults();
        private Consumer<ParseException> errorHandler = error -> {};
        private boolean strict = false;

        public Builder resolvers(Collection<TagResolver> resolvers) {
            return tagResolver(TagResolver.resolvers(resolvers));
        }

        public Builder resolvers(TagResolver... resolvers) {
            return tagResolver(TagResolver.resolvers(resolvers));
        }

        public Builder tagResolver(TagResolver tagResolver) {
            this.tagResolver = tagResolver;
            return this;
        }

        public Builder errorHandler(Consumer<ParseException> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Builder strict() {
            this.strict = true;
            return this;
        }

        public Formatify build() {
            return new Formatify(tagResolver, errorHandler, strict);
        }

    }

}
