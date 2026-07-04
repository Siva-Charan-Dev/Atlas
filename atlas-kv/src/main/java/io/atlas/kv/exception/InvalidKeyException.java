package io.atlas.kv.exception;

/**
 * Thrown when a key supplied to the storage engine is invalid.
 */
public class InvalidKeyException extends IllegalArgumentException {

    public InvalidKeyException(String message) {
        super(message);
    }
}