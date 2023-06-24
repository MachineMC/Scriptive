package org.machinemc.scriptive.formatify.tags;

import org.machinemc.scriptive.style.Colour;
import org.machinemc.scriptive.style.HexColor;
import org.machinemc.scriptive.formatify.Result;
import org.machinemc.scriptive.formatify.arguments.ColorArgument;

import java.util.*;
import java.util.function.Predicate;

public class TagRegistry {

    private final Map<String, Tag> map;
    private final List<DynamicTag> dynamicTags;

    public TagRegistry() {
        this(true);
    }

    public TagRegistry(boolean includeDefaults) {
        this(new HashMap<>(), new ArrayList<>());
        if (includeDefaults)
            registerDefaults();
    }

    private TagRegistry(Map<String, Tag> map, List<DynamicTag> dynamicTags) {
        this.map = map;
        this.dynamicTags = dynamicTags;
    }

    private void registerDefaults() {
        registerTags(DefaultTags.ALL);

        registerDynamicTag(label -> {
            Result<Colour> result = new ColorArgument().parse(label);
            return result.successful() && (label.startsWith("#") || !(result.value() instanceof HexColor));
        }, DefaultTags.COLOR);
    }

    public void registerTag(Tag tag) {
        if (isRegistered(tag))
            throw new IllegalArgumentException("Tag with identifier '" + tag.getIdentifier() + "' is already registered.");
        map.put(tag.getIdentifier(), tag);
    }

    public void registerTags(Tag... tags) {
        for (Tag tag : tags)
            registerTag(tag);
    }

    public void registerTags(Collection<Tag> tags) {
        for (Tag tag : tags)
            registerTag(tag);
    }

    public void registerDynamicTag(Predicate<String> matcher, Tag tag) {
        dynamicTags.add(new DynamicTag(matcher, tag));
    }

    public boolean isRegistered(Tag tag) {
        return isRegistered(tag.getIdentifier());
    }

    public boolean isRegistered(String identifier) {
        return map.containsKey(identifier);
    }

    public Optional<Tag> getTag(String identifier, boolean checkAliases) {
        Tag tag = map.get(identifier);
        if (tag != null) {
            return Optional.of(tag);
        } else if (!checkAliases) {
            return Optional.empty();
        }

        for (Tag value : map.values()) {
            if (value.getAliases().contains(identifier))
                return Optional.of(value);
        }

        for (DynamicTag dynamicTag : dynamicTags) {
            if (dynamicTag.getMatcher().test(identifier))
                return Optional.of(dynamicTag);
        }
        return Optional.empty();
    }

}
