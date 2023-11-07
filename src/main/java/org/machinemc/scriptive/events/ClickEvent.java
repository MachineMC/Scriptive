package org.machinemc.scriptive.events;

import org.machinemc.scriptive.Contents;

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

    public enum Action {
        OPEN_URL,
        RUN_COMMAND,
        SUGGEST_COMMAND,
        CHANGE_PAGE,
        COPY_TO_CLIPBOARD
    }

}
