package com.github.hadaward.realmjs.api.registries;

import com.github.hadaward.realmjs.api.Module;

import java.util.HashMap;

public class JavaModuleRegistry {
    public static final HashMap<String, Object> MODULES = new HashMap<>();

    public static boolean hasCache(String id) {
        return MODULES.containsKey(id);
    }

    public static Object get(String id) {
        return MODULES.get(id);
    }

    public static void register(String id, Object module) {
        if (!MODULES.containsKey(id)) {
            MODULES.put(id, module);
        }
    }
}
