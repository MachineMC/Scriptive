package org.machinemc.scriptive.events;

import org.machinemc.scriptive.Contents;

import java.util.Locale;
import java.util.Map;

public record ClickEvent(Action action, String value) implements Contents {

    public ClickEvent withAction(Action action) {
        return new ClickEvent(action, value);
    }

    public ClickEvent withValue(String value) {
        return new ClickEvent(action, value);
    }

    @Override
    public Map<String, Object> asMap() {
        return Map.of(
                "action", action,
                "value", value
        );
    }

    public static ClickEvent deserialize(Map<String, String> map) {
        Action action;
        try {
            action = map.containsKey("action") ? Action.valueOf(map.get("action").toUpperCase(Locale.ENGLISH)) : null;
        } catch (IllegalArgumentException ignored) {
            return null;
        }
        String value = map.get("action");
        if (value == null)
            return null;
        return new ClickEvent(action, value);
    }

    public enum Action {
        OPEN_URL,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE,
        COPY_TO_CLIPBOARD
    }

}
