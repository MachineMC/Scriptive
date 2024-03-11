package org.machinemc.scriptive.components;

import org.jetbrains.annotations.Nullable;
import org.machinemc.scriptive.Contents;
import org.machinemc.scriptive.locale.LocaleLanguage;
import org.machinemc.scriptive.style.TextFormat;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationComponent extends BaseComponent {

    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    private final @Nullable LocaleLanguage localeLanguage;
    private String translation;
    private @Nullable String fallback;
    private Component[] arguments;

    private List<Component> decomposedParts;

    protected TranslationComponent(@Nullable LocaleLanguage localeLanguage, String translation, @Nullable String fallback, Component... arguments) {
        super();
        this.localeLanguage = localeLanguage;
        this.translation = translation;
        this.fallback = fallback;
        this.arguments = arguments;
    }

    public @Nullable LocaleLanguage getLocaleLanguage() {
        return localeLanguage;
    }

    public TranslationComponent withLocaleLanguage(LocaleLanguage localeLanguage) {
        TranslationComponent component = new TranslationComponent(localeLanguage, translation, fallback, arguments);
        component.merge(this);
        return component;
    }

    public String getTranslation() {
        return translation;
    }

    public void setTranslation(String translation) {
        this.translation = translation;
    }

    public @Nullable String getFallback() {
        return fallback;
    }

    public void setFallback(@Nullable String fallback) {
        this.fallback = fallback;
    }

    public Component[] getArguments() {
        return arguments;
    }

    public void setArguments(Component... arguments) {
        this.arguments = arguments;
    }

    @Override
    public List<Component> toFlatList() {
        decompose();
        List<Component> separated = super.toFlatList();
        separated.remove(0);
        separated.addAll(0, decomposedParts);
        return separated;
    }

    @Override
    public String getString() {
        decompose();
        StringBuilder builder = new StringBuilder();
        decomposedParts.forEach(component -> builder.append(component.getString()));
        return builder.toString();
    }

    private void decompose() {
        if (decomposedParts != null) return;

        if (localeLanguage == null) {
            decomposedParts = List.of(TextComponent.of(translation));
            return;
        }

        String translation = Optional.ofNullable(fallback)
                .map(fallback -> localeLanguage.getOrDefault(this.translation, fallback))
                .orElse(localeLanguage.getOrDefault(this.translation));

        List<Component> parts = new ArrayList<>();
        if (decomposeTemplate(translation, parts::add)) {
            decomposedParts = parts;
        } else {
            decomposedParts = List.of(componentPart(translation));
        }
    }

    private boolean decomposeTemplate(String translation, Consumer<Component> consumer) {
        Matcher matcher = FORMAT_PATTERN.matcher(translation);

        int currentIndex = 0;

        int offset;
        int next;
        for (offset = 0; matcher.find(offset); offset = next) {
            int start = matcher.start();
            next = matcher.end();
            String string;
            if (start > offset) {
                string = translation.substring(offset, start);
                if (string.indexOf(31) != -1)
                    return false;
                consumer.accept(componentPart(string));
            }

            string = matcher.group(2);
            String otherString = translation.substring(start, next);
            if (string.equals("%") && otherString.equals("%%")) {
                consumer.accept(componentPart("%"));
                continue;
            }

            if (!string.equals("s"))
                return false;

            String stringIndex = matcher.group(1);
            int index = stringIndex != null ? Integer.parseInt(stringIndex) : currentIndex++;
            Component argument = getArgument(index);
            if (argument == null)
                return false;
            consumer.accept(argument);
        }

        if (offset >= translation.length())
            return true;

        String rest = translation.substring(offset);
        if (rest.indexOf(37) != -1)
            return false;
        consumer.accept(componentPart(rest));
        return true;
    }

    private Component getArgument(int index) {
        if (index < 0 || index >= arguments.length)
            return null;
        Component argument = arguments[index];
        if (argument == null)
            return TextComponent.of("null");
        return argument;
    }

    private Component componentPart(String part) {
        Component component = TextComponent.of(part);
        component.merge(this);
        return component;
    }

    @Override
    public TranslationComponent append(String literal) {
        return (TranslationComponent) super.append(literal);
    }

    @Override
    public TranslationComponent append(String literal, TextFormat textFormat) {
        return (TranslationComponent) super.append(literal, textFormat);
    }

    @Override
    public TranslationComponent append(Component component) {
        return (TranslationComponent) super.append(component);
    }

    @Override
    public void merge(Component other) {
        super.merge(other);
        if (getClass().isInstance(other)) {
            TranslationComponent translation = (TranslationComponent) other;
            setTranslation(translation.getTranslation());
            setFallback(translation.getFallback());
            Component[] arguments = translation.getArguments();
            for (int i = 0; i < arguments.length; i++)
                arguments[i] = arguments[i].clone();
            setArguments(arguments);
        }
    }

    @Override
    public ComponentModifier modify() {
        return new ComponentModifier(this);
    }

    @Override
    public Map<String, Object> asMap() {
        Map<String, Object> map = super.asMap();
        map.put("translate", translation);
        map.put("with", Arrays.stream(arguments).map(Contents::asMap).toArray(Map[]::new));
        return map;
    }

    @Override
    public TranslationComponent clone() {
        TranslationComponent clone = new TranslationComponent(localeLanguage, translation, fallback, arguments);
        clone.merge(this);
        return clone;
    }

    public static TranslationComponent of(String translation, Component... arguments) {
        return of(null, translation, null, arguments);
    }

    public static TranslationComponent of(@Nullable LocaleLanguage localeLanguage, String translation, Component... arguments) {
        return of(localeLanguage, translation, null, arguments);
    }

    public static TranslationComponent of(
            @Nullable LocaleLanguage localeLanguage,
            String translation,
            @Nullable String fallback,
            Component... arguments
    ) {
        return new TranslationComponent(localeLanguage, translation, fallback, arguments);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslationComponent that)) return false;
        return Objects.equals(localeLanguage, that.localeLanguage)
                && translation.equals(that.translation)
                && Objects.equals(fallback, that.fallback)
                && Arrays.equals(arguments, that.arguments)
                && Objects.equals(decomposedParts, that.decomposedParts)

                // base component
                && getSiblings().equals(that.getSiblings())
                && getTextFormat().equals(that.getTextFormat())
                && Objects.equals(getInsertion(), that.getInsertion())
                && Objects.equals(getClickEvent(), that.getClickEvent())
                && Objects.equals(getHoverEvent(), that.getHoverEvent());
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(localeLanguage);
        result = 31 * result + translation.hashCode();
        result = 31 * result + Objects.hashCode(fallback);
        result = 31 * result + Arrays.hashCode(arguments);
        result = 31 * result + Objects.hashCode(decomposedParts);

        // base component
        result = 31 * result + getSiblings().hashCode();
        result = 31 * result + getTextFormat().hashCode();
        result = 31 * result + Objects.hashCode(getInsertion());
        result = 31 * result + Objects.hashCode(getClickEvent());
        result = 31 * result + Objects.hashCode(getHoverEvent());

        return result;
    }

    public static class ComponentModifier extends Component.ComponentModifier<ComponentModifier, TranslationComponent> {

        protected ComponentModifier(TranslationComponent component) {
            super(component);
        }

        public ComponentModifier translation(String translation) {
            component.setTranslation(translation);
            return getThis();
        }

        public ComponentModifier fallback(String fallback) {
            component.setFallback(fallback);
            return getThis();
        }

        public ComponentModifier arguments(Component... arguments) {
            component.setArguments(arguments);
            return getThis();
        }

        @Override
        protected ComponentModifier getThis() {
            return this;
        }

    }

}
