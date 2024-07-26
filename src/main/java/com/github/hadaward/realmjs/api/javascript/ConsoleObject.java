package com.github.hadaward.realmjs.api.javascript;

public class ConsoleObject {
    public void log(String... args) {
        System.out.println(String.join("\t", args));
    }
}
