package io.atlas.kv.engine;

import java.util.Optional;

/**
 * Defines the contract for a key-value storage engine.
 *
 * <p>The storage engine is intentionally serialization-agnostic.
 * Keys are represented as Strings, while values are raw byte arrays.
 * Implementations are responsible for enforcing input validation
 * and thread safety.</p>
 */
public interface KeyValueEngine {

    /**
     * Stores or replaces a value associated with the specified key.
     *
     * @param key   the key
     * @param value the value
     */
    void put(String key, byte[] value);

    /**
     * Retrieves the value associated with the specified key.
     *
     * @param key the key
     * @return Optional containing the value if present
     */
    Optional<byte[]> get(String key);

    /**
     * Removes the specified key.
     *
     * @param key the key
     * @return true if the key existed and was removed
     */
    boolean delete(String key);

    /**
     * Checks whether the specified key exists.
     *
     * @param key the key
     * @return true if the key exists
     */
    boolean exists(String key);

    /**
     * Returns the number of key-value pairs.
     *
     * @return number of entries
     */
    long size();
}