package com.github.hadaward.realmjs.api;

import com.github.hadaward.realmjs.RealmJSMod;
import com.github.hadaward.realmjs.util.ResourceLoader;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import java.io.IOException;

public class Extensions {
    public static final String PromiseExtension;

    static {
        try {
            PromiseExtension = ResourceLoader.LoadString(RealmJSMod.class, "/javascript/promises.js");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void LoadExtensions(Module module) {
        try {
            var Promise = module.engine.eval(PromiseExtension, module.ctx);
            module.ctx.setAttribute("Promise", Promise, ScriptContext.ENGINE_SCOPE);
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
