package org.machinemc.scriptive.components;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.scriptive.serialization.ComponentProperty;
import org.machinemc.scriptive.serialization.ComponentSerializer;
import org.machinemc.scriptive.locale.LocaleLanguage;
import org.machinemc.scriptive.style.TextFormat;

import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A component that can display translated text.
 *
 * @see LocaleLanguage
 */
public final class TranslationComponent extends BaseComponent implements ClientComponent {

    /**
     * Creates new translation component.
     *
     * @param translation translation key
     * @param arguments arguments
     * @return translation component
     */
    public static TranslationComponent of(String translation, Component... arguments) {
        return of(null, translation, null, arguments);
    }

    /**
     * Creates new translation component.
     *
     * @param translation translation key
     * @param fallback fallback
     * @param arguments arguments
     * @return translation component
     */
    public static TranslationComponent of(String translation, String fallback, Component... arguments) {
        return of(null, translation, fallback, arguments);
    }

    /**
     * Creates new translation component.
     *
     * @param localeLanguage locale language
     * @param translation translation key
     * @param arguments arguments
     * @return translation component
     */
    public static TranslationComponent of(@Nullable LocaleLanguage localeLanguage, String translation, Component... arguments) {
        return of(localeLanguage, translation, null, arguments);
    }

    /**
     * Creates new translation component.
     *
     * @param localeLanguage locale language
     * @param translation translation key
     * @param fallback fallback
     * @param arguments arguments
     * @return translation component
     */
    public static TranslationComponent of(
            @Nullable LocaleLanguage localeLanguage,
            String translation,
            @Nullable String fallback,
            Component... arguments
    ) {
        return new TranslationComponent(localeLanguage, translation, fallback, arguments);
    }

    private static final Pattern FORMAT_PATTERN = Pattern.compile("%(?:(\\d+)\\$)?([A-Za-z%]|$)");

    private final @Nullable LocaleLanguage localeLanguage;
    private String translation;
    private @Nullable String fallback;
    private Component[] arguments;

    private transient List<Component> decomposedParts;

    private TranslationComponent(@Nullable LocaleLanguage localeLanguage, String translation, @Nullable String fallback, Component... arguments) {
        super();
        this.localeLanguage = localeLanguage;
        this.translation = translation;
        this.fallback = fallback;
        this.arguments = arguments;
    }

    /**
     * Returns locale language used to compute flat string representation of
     * this component.
     *
     * @return locale language
     */
    public @Nullable LocaleLanguage getLocaleLanguage() {
        return localeLanguage;
    }

    /**
     * Returns new copy of this translation component with new locale language used
     * to compute flat string representation of the component.
     *
     * @param localeLanguage new locale language
     * @return copy of this component
     */
    @Contract("_ -> new")
    public TranslationComponent withLocaleLanguage(LocaleLanguage localeLanguage) {
        TranslationComponent component = new TranslationComponent(localeLanguage, translation, fallback, arguments);
        component.merge(this);
        return component;
    }

    /**
     * @return translation key
     */
    public String getTranslation() {
        return translation;
    }

    /**
     * @param translation new translation key
     */
    public void setTranslation(String translation) {
        if (this.translation.equals(translation)) return;
        decomposedParts = null;
        this.translation = translation;
    }

    /**
     * @return fallback key
     */
    public @Nullable String getFallback() {
        return fallback;
    }

    /**
     * @param fallback new fallback key
     */
    public void setFallback(@Nullable String fallback) {
        if (Objects.equals(this.fallback, fallback)) return;
        decomposedParts = null;
        this.fallback = fallback;
    }

    /**
     * @return arguments
     */
    public Component[] getArguments() {
        return arguments;
    }

    /**
     * @param arguments new arguments
     */
    public void setArguments(Component @Nullable ... arguments) {
        if (Arrays.equals(this.arguments, arguments)) return;
        decomposedParts = null;
        if (arguments == null) arguments = new Component[0];
        for (Component argument : arguments) Objects.requireNonNull(argument, "Argument can not be null");
        this.arguments = arguments;
    }

    @Override
    public String getName() {
        return "translatable";
    }

    @Override
    public List<String> getUniqueKeys() {
        return List.of("translate", "fallback", "with");
    }

    @Override
    public List<Component> toFlatList() {
        decompose();
        List<Component> separated = super.toFlatList();
        separated.removeFirst();
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
    public TranslationComponent clone() {
        TranslationComponent clone = new TranslationComponent(localeLanguage, translation, fallback, arguments);
        clone.merge(this);
        return clone;
    }

    @Override
    public Class<TranslationComponent> getType() {
        return TranslationComponent.class;
    }

    @Override
    public void loadProperties(ComponentProperties properties, ComponentSerializer serializer) {
        super.loadProperties(properties, serializer);
        translation = properties.getValue("translate", String.class).orElseThrow();
        fallback = properties.getValue("translate", String.class).orElse(null);
        arguments = properties.get("with", ComponentProperty.Array.class)
                .map(ComponentProperty.Array::value)
                .map(array -> {
                    Component[] components = new Component[array.length];
                    for (int i = 0; i < array.length; i++)
                        components[i] = serializer.deserialize(array[i]);
                    return components;
                }).orElse(null);
    }

    @Override
    public @UnmodifiableView ComponentProperties getProperties() {
        ComponentProperties properties = super.getProperties();
        properties.set("translate", translation);
        properties.set("fallback", fallback);
        if (arguments.length == 0) return properties;
        ComponentProperties[] array = new ComponentProperties[arguments.length];
        for (int i = 0; i < arguments.length; i++) array[i] = arguments[i].getProperties();
        properties.set("with", array);
        return properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TranslationComponent that)) return false;
        return Objects.equals(localeLanguage, that.localeLanguage)
                && translation.equals(that.translation)
                && Objects.equals(fallback, that.fallback)
                && Arrays.equals(arguments, that.arguments)

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

        // base component
        result = 31 * result + getSiblings().hashCode();
        result = 31 * result + getTextFormat().hashCode();
        result = 31 * result + Objects.hashCode(getInsertion());
        result = 31 * result + Objects.hashCode(getClickEvent());
        result = 31 * result + Objects.hashCode(getHoverEvent());

        return result;
    }

    public static final class ComponentModifier extends Component.ComponentModifier<ComponentModifier, TranslationComponent> {

        private ComponentModifier(TranslationComponent component) {
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

    }

}
