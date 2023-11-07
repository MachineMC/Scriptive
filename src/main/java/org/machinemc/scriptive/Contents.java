package org.machinemc.scriptive;

import java.util.Map;

public interface Contents {

    Map<String, Object> asMap();

    default String toJson() {
        return GsonInstance.get().toJson(asMap());
    }

}
