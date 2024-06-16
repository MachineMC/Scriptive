package org.machinemc.scriptive.formatify.parser.node;

import org.machinemc.scriptive.components.TextComponent;

import java.util.Objects;

public class TextNode extends Node {

    private final String text;

    public TextNode(Node parent, String text) {
        super(Objects.requireNonNull(parent, "Non-root node must have a parent node"));
        this.text = Objects.requireNonNull(text, "text cannot be null");
    }

    @Override
    public TextComponent evaluate() {
        return TextComponent.of(text);
    }

}
