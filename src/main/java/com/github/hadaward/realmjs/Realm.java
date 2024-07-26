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
    private final NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
    private final NashornScriptEngine engine = (NashornScriptEngine) factory.getScriptEngine(
            "--language=es6",
            "-scripting=false",
            "-strict=true",
            "--no-java",
            "--no-syntax-extensions"
    );

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
