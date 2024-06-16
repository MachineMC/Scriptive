package org.machinemc.scriptive.formatify.parser.node;

import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.tag.Tag;

import java.util.Objects;

public class TagNode extends Node {

    private final String name;
    private final Tag tag;

    public TagNode(Node parent, String name, Tag tag) {
        super(Objects.requireNonNull(parent, "Non-root node must have a parent node"));
        this.name = Objects.requireNonNull(name, "name cannot be null");
        this.tag = Objects.requireNonNull(tag, "tag cannot be null");
    }

    public String name() {
        return name;
    }

    public Tag tag() {
        return tag;
    }

    @Override
    public TextComponent evaluate() {
        TextComponent component = TextComponent.empty();
        tag.apply(component);
        return component;
    }

}
