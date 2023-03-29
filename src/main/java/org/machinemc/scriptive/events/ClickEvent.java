package org.machinemc.scriptive.events;

import org.machinemc.scriptive.Contents;

import java.util.Map;

public class ClickEvent implements Contents {

    private final Action action;
    private final String value;

    public ClickEvent(Action action, String value) {
        this.action = action;
        this.value = value;
    }

    public Action getAction() {
        return action;
    }

    public String getValue() {
        return value;
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
