package com.github.hadaward.realmjs.api;

import com.github.hadaward.realmjs.api.javascript.ConsoleObject;
import com.github.hadaward.realmjs.api.javascript.GlobalObject;
import com.github.hadaward.realmjs.api.javascript.ModuleObject;
import com.github.hadaward.realmjs.api.javascript.interfaces.ISetTimeoutFunction;
import com.github.hadaward.realmjs.api.registries.ModuleRegistry;
import com.github.hadaward.realmjs.util.ResourceLoader;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

public class Module {
    public final SimpleScriptContext ctx = new SimpleScriptContext();
    public final ScriptEngine engine;
    public final String relativePath;
    private final Class<?> mainClass;
    public final ModuleObject mObject;

    public Module(Class<?> mainClass, ScriptEngine engine, String relativePath, String fullPath, String id) throws ScriptException {
        this.mainClass = mainClass;
        this.engine = engine;
        this.relativePath = relativePath;

        ctx.setAttribute("exports", engine.eval( "var exports = {}; exports;", ctx), ScriptContext.ENGINE_SCOPE);

        mObject = new ModuleObject(
                relativePath,
                fullPath,
                id,
                ctx.getAttribute("exports")
        );

        ctx.setAttribute("require", (Function<String, Object>) this::require, ScriptContext.ENGINE_SCOPE);
        ctx.setAttribute("module", this.mObject, ScriptContext.ENGINE_SCOPE);
        ctx.setAttribute("console", new ConsoleObject(), ScriptContext.ENGINE_SCOPE);

        var global = new GlobalObject();

        ctx.setAttribute("setTimeout", (ISetTimeoutFunction) global::setTimeout, ScriptContext.ENGINE_SCOPE);
        ctx.setAttribute("clearTimeout", (Consumer<Integer>) global::clearTimeout, ScriptContext.ENGINE_SCOPE);

        Extensions.LoadExtensions(this);
    }

    public Object require(String path) {
        var fullPath = Paths.get(relativePath, path);

        if (Files.isDirectory(fullPath)) {
            fullPath = Paths.get(fullPath.toString(), "index.js");
        } else if (!fullPath.endsWith(".js")) {
            fullPath = Paths.get(fullPath + ".js");
        }

        var moduleId = fullPath.normalize().toAbsolutePath().toString();

        if (moduleId.equals(this.mObject.id)) {
            throw new RuntimeException("A module cannot import itself.");
        }

        if (ModuleRegistry.hasCache(moduleId)) {
            var module = ModuleRegistry.get(moduleId);
            return ((ModuleObject) module.ctx.getAttribute("module")).exports;
        }

        var newRelativePath = fullPath.getParent();

        try {
            var script = ResourceLoader.LoadString(mainClass, fullPath.normalize().toString().replaceAll("\\\\", "/"));
            var newModule = new Module(
                    mainClass,
                    engine,
                    newRelativePath.toString(),
                    fullPath.toString(),
                    moduleId
            );

            engine.eval(script, newModule.ctx);

            ModuleRegistry.register(moduleId, newModule);
            return ((ModuleObject) newModule.ctx.getAttribute("module")).exports;
        } catch (IOException | ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
