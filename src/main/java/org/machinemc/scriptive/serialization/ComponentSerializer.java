package org.machinemc.scriptive.serialization;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import org.machinemc.scriptive.components.Component;

import java.util.Map;

public interface ComponentSerializer {

    <T extends Component> void register(Class<T> type, ComponentCreator<T> creator, String... uniqueKeys);

    Map<String, Object> serialize(Component component);

    default  <C extends Component> C deserializeJson(String json) {
        return deserializeJson(JsonParser.parseString(json));
    }

    default  <C extends Component> C deserializeJson(JsonElement json) {
        return deserialize(new Gson().fromJson(json, new TypeToken<Map<String, Object>>() {}.getType()));
    }

    <C extends Component> C deserialize(Map<String, Object> map);

}
