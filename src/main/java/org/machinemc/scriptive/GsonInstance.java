package org.machinemc.scriptive;

import com.google.gson.Gson;

public final class GsonInstance {

    private static final Gson GSON = new Gson();

    public static Gson get() {
        return GSON;
    }

}
