package com.github.hadaward.realmjs;

import com.github.hadaward.realmjs.api.Module;
import com.github.hadaward.realmjs.api.registries.ModuleRegistry;
import com.github.hadaward.realmjs.util.ResourceLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import org.openjdk.nashorn.api.scripting.NashornScriptEngine;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;

import javax.script.ScriptException;
import java.io.IOException;
import java.nio.file.Paths;

public class Realm {
    private static final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private static final NashornScriptEngine internalEngine = (NashornScriptEngine) factory.getScriptEngine("--language=es6", "-strict=true");

    private final NashornScriptEngine engine = (NashornScriptEngine) factory.getScriptEngine(
            "--language=es6",
            "-scripting=false",
            "-strict=true",
            "--no-java",
            "--no-syntax-extensions"
    );

    public static Object getJavaModule(String module) throws ScriptException {
        return internalEngine.eval("Java.type(\""+module.replaceAll("[^a-zA-Z0-9.*_]", "")+"\")");
    }

    public void start(Class<?> mainClass, String entryPath) throws IOException, ScriptException {
        var path = Paths.get(entryPath);

        var script = ResourceLoader.LoadString(mainClass, entryPath);
        var baseModule = new Module(
                mainClass,
                engine,
                path.getParent().toString(),
                path.toString(),
                path.normalize().toAbsolutePath().toString()
        );

        engine.eval(script, baseModule.ctx);
        ModuleRegistry.register(baseModule.mObject.id, baseModule);
    }
}
