package org.machinemc.scriptive.serialization;

import org.machinemc.scriptive.components.Component;

import java.util.Map;

public record SerializedComponent(Map<String, Object> properties) {

    public Component deserialize(ComponentSerializer serializer) {
        return serializer.deserialize(properties);
    }

}
