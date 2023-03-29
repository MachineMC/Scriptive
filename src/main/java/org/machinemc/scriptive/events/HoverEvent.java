package org.machinemc.scriptive.events;

import org.machinemc.scriptive.Contents;

import java.util.Map;

public class HoverEvent implements Contents {

    private final Action action;
    private final Contents contents;

    public HoverEvent(Action action, Contents contents) {
        this.action = action;
        this.contents = contents;
    }

    public Action getAction() {
        return action;
    }

    public Contents getContents() {
        return contents;
    }

    @Override
    public Map<String, Object> asMap() {
        return Map.of(
                "action", action,
                "contents", contents.asMap()
        );
    }

    public enum Action {
        SHOW_TEXT,
        SHOW_ITEM,
        SHOW_ENTITY
    }

}
