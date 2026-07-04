package io.atlas.kv.engine.impl;

import io.atlas.kv.engine.KeyValueEngine;
import io.atlas.kv.engine.impl.InMemoryKeyValueEngine;
import io.atlas.kv.exception.InvalidKeyException;
import io.atlas.kv.exception.InvalidValueException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryKeyValueEngineTest {

    private KeyValueEngine engine;

    @BeforeEach
    void setUp() {
        engine = new InMemoryKeyValueEngine();
    }

    @Test
    @DisplayName("put() should store value")
    void shouldStoreValue() {

        byte[] value = "atlas".getBytes(StandardCharsets.UTF_8);

        engine.put("key1", value);

        Optional<byte[]> result = engine.get("key1");

        assertTrue(result.isPresent());
        assertArrayEquals(value, result.get());
    }

    @Test
    @DisplayName("get() should return empty when key does not exist")
    void shouldReturnEmptyForMissingKey() {

        Optional<byte[]> result = engine.get("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("put() should overwrite existing value")
    void shouldOverwriteExistingValue() {

        engine.put("key", "one".getBytes(StandardCharsets.UTF_8));
        engine.put("key", "two".getBytes(StandardCharsets.UTF_8));

        assertArrayEquals(
                "two".getBytes(StandardCharsets.UTF_8),
                engine.get("key").orElseThrow()
        );
    }

    @Test
    @DisplayName("delete() should remove existing key")
    void shouldDeleteKey() {

        engine.put("key", "value".getBytes(StandardCharsets.UTF_8));

        assertTrue(engine.delete("key"));
        assertFalse(engine.exists("key"));
        assertEquals(0, engine.size());
    }

    @Test
    @DisplayName("delete() should return false for missing key")
    void shouldReturnFalseWhenDeletingMissingKey() {

        assertFalse(engine.delete("missing"));
    }

    @Test
    @DisplayName("exists() should detect existing key")
    void shouldDetectExistingKey() {

        engine.put("key", "value".getBytes(StandardCharsets.UTF_8));

        assertTrue(engine.exists("key"));
    }

    @Test
    @DisplayName("exists() should return false for missing key")
    void shouldReturnFalseForMissingKey() {

        assertFalse(engine.exists("missing"));
    }

    @Test
    @DisplayName("size() should reflect number of entries")
    void shouldTrackSize() {

        engine.put("a", "1".getBytes(StandardCharsets.UTF_8));
        engine.put("b", "2".getBytes(StandardCharsets.UTF_8));
        engine.put("c", "3".getBytes(StandardCharsets.UTF_8));

        assertEquals(3, engine.size());

        engine.delete("b");

        assertEquals(2, engine.size());
    }

    @Test
    @DisplayName("put() should reject null key")
    void shouldRejectNullKey() {

        assertThrows(
                InvalidKeyException.class,
                () -> engine.put(null, new byte[]{1})
        );
    }

    @Test
    @DisplayName("put() should reject blank key")
    void shouldRejectBlankKey() {

        assertThrows(
                InvalidKeyException.class,
                () -> engine.put("   ", new byte[]{1})
        );
    }

    @Test
    @DisplayName("put() should reject null value")
    void shouldRejectNullValue() {

        assertThrows(
                InvalidValueException.class,
                () -> engine.put("key", null)
        );
    }

    @Test
    @DisplayName("get() should reject null key")
    void shouldRejectNullKeyOnGet() {

        assertThrows(
                InvalidKeyException.class,
                () -> engine.get(null)
        );
    }

    @Test
    @DisplayName("delete() should reject null key")
    void shouldRejectNullKeyOnDelete() {

        assertThrows(
                InvalidKeyException.class,
                () -> engine.delete(null)
        );
    }

    @Test
    @DisplayName("exists() should reject null key")
    void shouldRejectNullKeyOnExists() {

        assertThrows(
                InvalidKeyException.class,
                () -> engine.exists(null)
        );
    }

    @Test
    @DisplayName("engine should make defensive copy during put()")
    void shouldDefensivelyCopyOnPut() {

        byte[] original = "atlas".getBytes(StandardCharsets.UTF_8);

        engine.put("key", original);

        original[0] = 'X';

        assertArrayEquals(
                "atlas".getBytes(StandardCharsets.UTF_8),
                engine.get("key").orElseThrow()
        );
    }

    @Test
    @DisplayName("engine should make defensive copy during get()")
    void shouldDefensivelyCopyOnGet() {

        engine.put("key", "atlas".getBytes(StandardCharsets.UTF_8));

        byte[] returned = engine.get("key").orElseThrow();

        returned[0] = 'X';

        assertArrayEquals(
                "atlas".getBytes(StandardCharsets.UTF_8),
                engine.get("key").orElseThrow()
        );
    }

    @Test
    @DisplayName("empty byte array should be supported")
    void shouldSupportEmptyByteArray() {

        engine.put("empty", new byte[0]);

        assertEquals(0, engine.get("empty").orElseThrow().length);
    }

    @Test
    @DisplayName("overwriting key should not increase size")
    void shouldNotIncreaseSizeWhenOverwriting() {

        engine.put("key", new byte[]{1});
        engine.put("key", new byte[]{2});
        engine.put("key", new byte[]{3});

        assertEquals(1, engine.size());
    }
}