package org.machinemc.scriptive.serialization;

import org.machinemc.nbt.*;
import org.machinemc.scriptive.components.TextComponent;
import org.machinemc.scriptive.components.ClientComponent;
import org.machinemc.scriptive.events.HoverEvent;

import java.util.*;
import java.util.function.Function;

public class NBTComponentSerializer implements ComponentSerializer<NBT<?>> {

    private static final NBTComponentSerializer INSTANCE = new NBTComponentSerializer();

    public static NBTComponentSerializer get() {
        return INSTANCE;
    }

    private NBTComponentSerializer() {
    }

    @Override
    public ClientComponent deserialize(NBT<?> input) {
        if (input instanceof NBTString)
            return TextComponent.of(((NBTString) input).revert());

        NBTCompound compound;

        if (input instanceof NBTCompound) {
            compound = (NBTCompound) input.clone();
        } else if (input instanceof NBTList list) {
            if (list.isEmpty()) return TextComponent.empty();
            if (list.tag() != NBT.Tag.COMPOUND) throw new IllegalArgumentException("Unsupported NBTList type");

            NBTCompound first = (NBTCompound) list.get(0).clone();

            List<NBTCompound> children = new ArrayList<>();
            for (int i = 1; i < list.size(); i++) children.add((NBTCompound) list.get(i).clone());

            NBTList extra = first.getNBT("extra", new NBTList()).clone();
            children.forEach(extra::add);

            first.set("extra", extra);

            compound = first;
        } else {
            throw new IllegalArgumentException("Unsupported NBT type " + input.tag());
        }

        return deserializeCompound(compound);
    }

    private ClientComponent deserializeCompound(NBTCompound compound) {
        Map<String, Object> map = new HashMap<>();
        Map<String, NBT<?>> view = compound.mapView();

        putValue(view, map, "keybind", NBTString::revert);
        putValue(view, map, "text", NBTString::revert);
        putValue(view, map, "translate", NBTString::revert);
        this.<NBTList>putValue(view, map, "with", list -> {
            List<Object> with = new ArrayList<>();
            for (NBT<?> nbt : list)
                with.add(deserialize(nbt));
            return with;
        });

        putValue(view, map, "color", NBTString::revert);

        this.<NBTByte>putValue(view, map, "bold", b -> b.revert() > 0);
        this.<NBTByte>putValue(view, map, "italic", b -> b.revert() > 0);
        this.<NBTByte>putValue(view, map, "underlined", b -> b.revert() > 0);
        this.<NBTByte>putValue(view, map, "strikethrough", b -> b.revert() > 0);
        this.<NBTByte>putValue(view, map, "obfuscated", b -> b.revert() > 0);

        putValue(view, map, "insertion", NBTString::revert);
        putValue(view, map, "font", NBTString::revert);

        putValue(view, map, "clickEvent", NBTCompound::revert);

        this.<NBTCompound>putValue(view, map, "hoverEvent", hoverCompound -> {
            Map<String, Object> properties = new HashMap<>();
            String action = hoverCompound.getValue("action");
            NBT<?> contents = hoverCompound.getNBT("contents");

            properties.put("action", action);

            if (action.equals(HoverEvent.Action.SHOW_TEXT.name())) {
                ClientComponent componentContent = deserialize(contents);
                properties.put("contents", ObjectComponentSerializer.get().serialize(componentContent));
            } else if (contents instanceof NBTCompound) {
                properties.put("contents", contents.revert());
            } else {
                throw new IllegalArgumentException("Unsupported NBT type " + contents.tag());
            }

            return properties;
        });

        if (compound.containsKey("extra")) {
            NBTList extra = compound.getNBT("extra");
            List<Map<String, Object>> extraMap = extra.listView().stream()
                    .map(this::deserialize)
                    .map(MapComponentSerializer.get()::serialize)
                    .toList();
            map.put("extra", extraMap);
        }

        return MapComponentSerializer.get().deserialize(map);
    }

    @SuppressWarnings("unchecked")
    private <T extends NBT<?>> void putValue(Map<String, NBT<?>> from, Map<String, Object> to, String key, Function<T, Object> mapper) {
        NBT<?> nbt = from.get(key);
        if (nbt == null) return;
        to.put(key, mapper.apply((T) nbt));
    }

    @Override
    public NBTCompound serialize(ClientComponent component) {
        return serializeCompound(component.clone());
    }

    private NBTCompound serializeCompound(ClientComponent component) {
        Map<String, Object> map = component.asMap();
        NBTCompound compound = new NBTCompound();

        revertValue(map, compound, "keybind", NBTString::new);
        revertValue(map, compound, "text", NBTString::new);
        revertValue(map, compound, "translate", NBTString::new);
        this.<List<Object>>revertValue(map, compound, "with", with -> {
            List<ClientComponent> components = new ArrayList<>();
            for (Object o : with)
                components.add(ObjectComponentSerializer.get().deserialize(o));
            return new NBTList(components.stream().map(this::serialize).toList());
        });

        revertValue(map, compound, "color", NBTString::new);

        this.<Boolean>revertValue(map, compound, "bold", b -> new NBTByte(b ? 1 : 0));
        this.<Boolean>revertValue(map, compound, "italic", b -> new NBTByte(b ? 1 : 0));
        this.<Boolean>revertValue(map, compound, "underlined", b -> new NBTByte(b ? 1 : 0));
        this.<Boolean>revertValue(map, compound, "strikethrough", b -> new NBTByte(b ? 1 : 0));
        this.<Boolean>revertValue(map, compound, "obfuscated", b -> new NBTByte(b ? 1 : 0));

        revertValue(map, compound, "insertion", NBTString::new);
        revertValue(map, compound, "font", NBTString::new);

        this.<Map<String, Object>>revertValue(map, compound, "clickEvent", NBTCompound::new);

        this.<Map<String, Object>>revertValue(map, compound, "hoverEvent", hoverMap -> {
            NBTCompound properties = new NBTCompound();
            String action = (String) hoverMap.get("action");
            Object contents = hoverMap.get("contents");

            properties.set("action", action);

            if (action.equals(HoverEvent.Action.SHOW_TEXT.name())) {
                ClientComponent componentContent = ObjectComponentSerializer.get().deserialize(contents);
                properties.set("contents", serialize(componentContent));
            } else if (contents instanceof NBTCompound) {
                properties.set("contents", new NBTCompound((Map<?, ?>) contents));
            } else {
                throw new IllegalArgumentException();
            }

            return properties;
        });

        if (map.containsKey("extra")) {
            List<?> extra = (List<?>) map.get("extra");
            NBTList nbtExtra = new NBTList(extra.stream()
                    .map(ObjectComponentSerializer.get()::deserialize)
                    .map(this::serialize)
                    .toList());
            compound.set("extra", nbtExtra);
        }

        return compound;
    }

    @SuppressWarnings("unchecked")
    private <T> void revertValue(Map<String, Object> from, NBTCompound to, String key, Function<T, NBT<?>> mapper) {
        Object o = from.get(key);
        if (o == null) return;
        to.set(key, mapper.apply((T) o));
    }

}
