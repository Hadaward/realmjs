package com.github.hadaward.realmjs.api.registries;

import com.github.hadaward.realmjs.api.Module;

import java.util.HashMap;

public class ModuleRegistry {
    public static final HashMap<String, Module> MODULES = new HashMap<>();

    public static boolean hasCache(String id) {
        return MODULES.containsKey(id);
    }

    public static Module get(String id) {
        return MODULES.get(id);
    }

    public static void register(String id, Module module) {
        if (!MODULES.containsKey(id)) {
            MODULES.put(id, module);
        }
    }
}
