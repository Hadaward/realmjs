package com.github.hadaward.realmjs.api.javascript;

public class ModuleObject {
    public final Object exports;
    public final String filename;
    public final String path;
    public final String id;

    public ModuleObject(String path, String filename, String id, Object exports) {
        this.path = path;
        this.filename = filename;
        this.id = id;
        this.exports = exports;
    }
}
