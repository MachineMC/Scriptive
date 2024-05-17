package org.machinemc.scriptive.formatify.parser.node;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.machinemc.scriptive.components.TextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Node {

    private final Node parent;
    private final List<Node> children = new ArrayList<>();

    public Node(@Nullable Node parent) {
        this.parent = parent;
        if (parent != null) parent.addChild(this);
    }

    public Node parent() {
        return parent;
    }

    public @UnmodifiableView List<Node> children() {
        return Collections.unmodifiableList(children);
    }

    public void addChild(Node node) {
        children.add(node);
    }

    public abstract TextComponent evaluate();

}
