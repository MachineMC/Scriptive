package org.machinemc.scriptive.formatify.tags;

import java.util.function.Predicate;

public class DynamicTag extends Tag {

    private final Predicate<String> matcher;
    private final Tag underlyingTag;

    protected DynamicTag(Predicate<String> matcher, Tag underlyingTag) {
        super(
                underlyingTag.getIdentifier(),
                underlyingTag.getAliases(),
                underlyingTag.arguments,
                underlyingTag.getUpdater(),
                underlyingTag.getTagParser()
        );
        this.matcher = matcher;
        this.underlyingTag = underlyingTag;
    }

    public Predicate<String> getMatcher() {
        return matcher;
    }

    public Tag getUnderlyingTag() {
        return underlyingTag;
    }

}
