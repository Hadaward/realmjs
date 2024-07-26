package com.github.hadaward.realmjs.api.javascript;

import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;

import java.util.Map;
import java.util.concurrent.*;

public class GlobalObject {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Map<Integer, ScheduledFuture<?>> tasks = new ConcurrentHashMap<>();
    private Integer lastId = 0;

    public Integer setTimeout(ScriptObjectMirror func, Integer delay, Object... params) {
        if (!func.isFunction()) {
            throw new IllegalArgumentException("setTimeout expected to receive a function as first argument.");
        }

        var id = lastId++;

        if (lastId > 3000000) {
            lastId = 0;
        }

        Runnable callback = () -> {
            tasks.remove(id);

            if (tasks.isEmpty()) {
                lastId = 0;
            }

            try {
                func.call(null, params);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        var task = scheduler.schedule(callback, delay, TimeUnit.MILLISECONDS);
        tasks.put(id, task);

        return id;
    }

    public void clearTimeout(Integer id) {
        if (tasks.containsKey(id)) {
            tasks.get(id).cancel(false);
            tasks.remove(id);
        }
    }
}
