package com.github.hadaward.realmjs.api.javascript.interfaces;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

@FunctionalInterface
public interface ISetTimeoutFunction {
    Integer apply(ScriptObjectMirror func, Integer delay, Object... params);
}