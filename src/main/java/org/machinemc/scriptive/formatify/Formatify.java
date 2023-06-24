package org.machinemc.scriptive.formatify;

import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.lexer.LexicalAnalyzer;
import org.machinemc.scriptive.formatify.tags.Tag;
import org.machinemc.scriptive.formatify.tags.TagRegistry;
import org.machinemc.scriptive.formatify.tree.RootNode;

import java.util.function.Consumer;

public class Formatify {

    private static final TagRegistry DEFAULT_REGISTRY = new TagRegistry();
    private static final Consumer<String> DEFAULT_ERROR_HANDLER = error -> {};
    private static final Formatify DEFAULT_FORMATIFY = new Formatify(DEFAULT_REGISTRY, DEFAULT_ERROR_HANDLER);

    private final TagRegistry registry;
    private final Consumer<String> errorHandler;

    private Formatify(TagRegistry registry, Consumer<String> errorHandler) {
        this.registry = registry;
        this.errorHandler = errorHandler;
    }

    public TextComponent parse(String string) {
        return parse(string, registry);
    }

    public TextComponent parse(String string, TagRegistry registry) {
        LexicalAnalyzer lexer = new LexicalAnalyzer(string);
        RootNode tree = FormatifyParser.buildTree(string, lexer.lex(), registry, errorHandler);
        return FormatifyParser.componentFromTree(tree);
    }

    public static Formatify formatify() {
        return DEFAULT_FORMATIFY;
    }

    public static Builder builder() {
        return builder(true);
    }

    public static Builder builder(boolean defaultRegistry) {
        return new Builder(defaultRegistry ? DEFAULT_REGISTRY : new TagRegistry());
    }

    public static class Builder {

        private final TagRegistry registry;
        private Consumer<String> errorHandler;

        public Builder(TagRegistry registry) {
            this.registry = registry;
        }

        public Builder tag(Tag tag) {
            registry.registerTag(tag);
            return this;
        }

        public Builder onError(Consumer<String> errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        public Formatify build() {
            return new Formatify(registry, errorHandler);
        }

    }

}
