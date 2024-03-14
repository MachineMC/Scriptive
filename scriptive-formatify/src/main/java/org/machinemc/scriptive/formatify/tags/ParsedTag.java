package org.machinemc.scriptive.formatify.tags;

import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.arguments.ParsedArguments;

public record ParsedTag(Tag tag, ParsedArguments arguments, String label) {

    public TextComponent newComponent() {
        TextComponent component = TextComponent.empty();
        tag.getUpdater().updateComponent(component, arguments);
        return component;
    }

    public boolean successful() {
        return arguments.successful();
    }

    public String error() {
        return arguments.error();
    }

    @Override
    public String toString() {
        return label;
    }

}
