package org.machinemc.scriptive.formatify.tree;

import org.jetbrains.annotations.NotNull;
import org.machinemc.scriptive.components.TextComponent;

public class TextNode extends Node {

    private final String text;

    public TextNode(Node parent, String text) {
        super(parent);
        this.text = text;
    }

    @Override
    public @NotNull TextComponent evaluate() {
        return TextComponent.of(text);
    }

}
