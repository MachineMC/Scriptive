package org.machinemc.scriptive.formatify.tree;

import org.jetbrains.annotations.NotNull;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.formatify.tags.ParsedTag;

public class TagNode extends Node {

    private final ParsedTag tag;

    public TagNode(Node parent, ParsedTag tag) {
        super(parent);
        this.tag = tag;
    }

    @Override
    public @NotNull TextComponent evaluate() {
        return tag.newComponent();
    }

}
