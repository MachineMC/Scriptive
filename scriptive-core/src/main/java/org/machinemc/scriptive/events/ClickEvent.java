package org.machinemc.scriptive.events;

import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.serialization.ComponentProperties;
import org.machinemc.scriptive.serialization.Contents;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents an event that happens when client clicks on a component.
 *
 * @param action action
 * @param value value
 */
public record ClickEvent(Action action, String value) implements Contents {

    /**
     * Different actions that may happen when client
     * clicks on the component.
     */
    public enum Action {

        /**
         * Click event that opens an url.
         */
        OPEN_URL,

        /**
         * Click event that runs a command.
         */
        RUN_COMMAND,

        /**
         * Click event that suggests a command.
         */
        SUGGEST_COMMAND,

        /**
         * Click event that changes to a page.
         */
        CHANGE_PAGE,

        /**
         * Click event that copies text to the clipboard.
         */
        COPY_TO_CLIPBOARD

    }

    public ClickEvent {
        Objects.requireNonNull(action, "Action can not be null");
        Objects.requireNonNull(value, "Value can not be null");
    }

    /**
     * Creates click event from component properties.
     *
     * @param properties properties
     * @return click event
     */
    public static Optional<ClickEvent> fromProperties(ComponentProperties properties) {
        if (!properties.contains("action")) return Optional.empty();
        Action action;
        try {
            action = Action.valueOf(properties.getValueOr("action", "").toUpperCase());
        } catch (Exception exception) {
            return Optional.empty();
        }

        String value = properties.getValue("value", String.class).orElse(null);
        if (value == null) return Optional.empty();
        return Optional.of(new ClickEvent(action, value));
    }

    @Override
    public @UnmodifiableView ComponentProperties getProperties() {
        ComponentProperties properties = new ComponentProperties();
        properties.set("action", action.name().toLowerCase());
        properties.set("value", value);
        return properties.unmodifiableView();
    }

    public ClickEvent withAction(Action action) {
        return new ClickEvent(action, value);
    }

    public ClickEvent withValue(String value) {
        return new ClickEvent(action, value);
    }

}
