package org.machinemc.scriptive;

import com.google.gson.GsonBuilder;

import java.util.Map;

public interface Contents {

    Map<String, Object> asMap();

    default String toJson() {
        return new GsonBuilder().create().toJson(asMap());
    }

}
