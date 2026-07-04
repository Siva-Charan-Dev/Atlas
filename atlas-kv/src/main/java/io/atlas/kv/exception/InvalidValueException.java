package io.atlas.kv.exception;

/**
 * Thrown when a value supplied to the storage engine is invalid.
 */
public class InvalidValueException extends IllegalArgumentException {

    public InvalidValueException(String message) {
        super(message);
    }
}