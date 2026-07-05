package io.atlas.kv.storage.snapshot;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record Snapshot(Map<String, byte[]> entries) {

    public Snapshot {
        Map<String, byte[]> copy = new HashMap<>();

        entries.forEach((key, value) ->
                copy.put(key, value.clone()));

        entries = Collections.unmodifiableMap(copy);
    }

    public static Snapshot empty() {
        return new Snapshot(Map.of());
    }

    public int size() {
        return entries.size();
    }
}