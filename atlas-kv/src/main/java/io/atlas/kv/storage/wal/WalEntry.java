package io.atlas.kv.storage.wal;

import java.util.Objects;

public record WalEntry(WalOperation operation, String key, byte[] value) {

    public WalEntry {
        Objects.requireNonNull(operation, "operation must not be null");
        Objects.requireNonNull(key, "key must not be null");

        if (key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }

        if (operation == WalOperation.SET && value == null) {
            throw new IllegalArgumentException("SET operation requires a value");
        }

        if (operation == WalOperation.DELETE && value != null) {
            throw new IllegalArgumentException("DELETE operation must not contain a value");
        }
    }

    public static WalEntry set(String key, byte[] value) {
        return new WalEntry(WalOperation.SET, key, value);
    }

    public static WalEntry delete(String key) {
        return new WalEntry(WalOperation.DELETE, key, null);
    }
}