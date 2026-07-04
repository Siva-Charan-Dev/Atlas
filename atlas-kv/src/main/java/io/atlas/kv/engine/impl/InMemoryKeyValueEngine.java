package io.atlas.kv.engine.impl;

import io.atlas.kv.engine.KeyValueEngine;
import io.atlas.kv.exception.InvalidKeyException;
import io.atlas.kv.exception.InvalidValueException;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Thread-safe in-memory implementation of {@link KeyValueEngine}.
 *
 * <p>This implementation stores all key-value pairs in memory using a
 * {@link ConcurrentHashMap}. Values are defensively copied during both
 * writes and reads to prevent external mutation of internal state.</p>
 *
 * <p>This implementation is intended as the reference storage engine for
 * Atlas KV before persistence is introduced.</p>
 */
public final class InMemoryKeyValueEngine implements KeyValueEngine {

    private final ConcurrentMap<String, byte[]> storage;

    /**
     * Creates an empty in-memory key-value engine.
     */
    public InMemoryKeyValueEngine() {
        this.storage = new ConcurrentHashMap<>();
    }

    @Override
    public void put(String key, byte[] value) {
        validateKey(key);
        validateValue(value);

        storage.put(key, Arrays.copyOf(value, value.length));
    }

    @Override
    public Optional<byte[]> get(String key) {
        validateKey(key);

        byte[] value = storage.get(key);

        if (value == null) {
            return Optional.empty();
        }

        return Optional.of(Arrays.copyOf(value, value.length));
    }

    @Override
    public boolean delete(String key) {
        validateKey(key);

        return storage.remove(key) != null;
    }

    @Override
    public boolean exists(String key) {
        validateKey(key);

        return storage.containsKey(key);
    }

    @Override
    public long size() {
        return storage.size();
    }

    /**
     * Validates the supplied key.
     *
     * @param key key to validate
     */
    private static void validateKey(String key) {

        if (key == null) {
            throw new InvalidKeyException("Key must not be null.");
        }

        if (key.isBlank()) {
            throw new InvalidKeyException("Key must not be blank.");
        }
    }

    /**
     * Validates the supplied value.
     *
     * @param value value to validate
     */
    private static void validateValue(byte[] value) {

        if (value == null) {
            throw new InvalidValueException("Value must not be null.");
        }
    }
}