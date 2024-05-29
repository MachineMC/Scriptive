package org.machinemc.scriptive.serialization;

import org.machinemc.nbt.*;
import org.machinemc.scriptive.components.Component;

import java.util.Arrays;
import java.util.Objects;

/**
 * Serializer for NBT format.
 */
public class NBTPropertiesSerializer implements PropertiesSerializer<NBTCompound> {

    private static final NBTPropertiesSerializer INSTANCE = new NBTPropertiesSerializer();

    private NBTPropertiesSerializer() {}

    @Override
    public NBTCompound serialize(ComponentProperties properties) {
        Objects.requireNonNull(properties, "Component properties can not be null");
        return (NBTCompound) unwrap(ComponentProperty.properties(properties));
    }

    @Override
    public ComponentProperties deserialize(NBTCompound value) {
        Objects.requireNonNull(value, "NBT can not be null");
        return ComponentProperty.convertToProperties(wrap(value)).value();
    }

    private NBT<?> unwrap(ComponentProperty<?> property) {
        return switch (property) {
            case ComponentProperty.String string -> new NBTString(string.value());
            case ComponentProperty.Boolean bool -> new NBTByte(bool.value() ? 1 : 0);
            case ComponentProperty.Integer integer -> new NBTInt(integer.value());
            case ComponentProperty.Properties properties -> {
                NBTCompound compound = new NBTCompound();
                properties.value().forEach((k, p) -> compound.set(k, unwrap(p)));
                yield compound;
            }
            case ComponentProperty.Array array -> {
                NBTList nbtList = new NBTList();
                Arrays.stream(array.value())
                        .map(ComponentProperty::properties)
                        .map(this::unwrap)
                        .forEach(nbtList::add);
                yield nbtList;
            }
        };
    }

    private ComponentProperty<?> wrap(NBT<?> nbt) {
        return switch (nbt) {
            case NBTList nbtList -> {
                ComponentProperties[] array = nbtList.listView().stream()
                        .map(this::wrap)
                        .map(ComponentProperty::convertToProperties)
                        .map(ComponentProperty::value)
                        .toArray(ComponentProperties[]::new);
                yield ComponentProperty.array(array);
            }
            case NBTCompound compound -> {
                ComponentProperties properties = new ComponentProperties();
                compound.mapView().forEach((k, e) -> properties.set(k, wrap(e)));
                yield ComponentProperty.properties(properties);
            }
            case NBTString string -> ComponentProperty.string(string.revert());
            case NBTByte bool -> ComponentProperty.bool(bool.revert() == 1);
            case NBTInt integer -> ComponentProperty.integer(integer.revert());
            default -> throw new IllegalStateException("Unexpected value: " + nbt);
        };
    }

    public static NBTPropertiesSerializer get() {
        return INSTANCE;
    }

}
