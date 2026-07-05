package io.atlas.kv.storage.wal;

import java.util.Base64;
import java.util.Objects;

public final class WalCodec {

    private static final String DELIMITER = "\t";

    private WalCodec() {
    }

    public static String serialize(WalEntry entry) {
        Objects.requireNonNull(entry);

        return switch (entry.operation()) {
            case SET ->
                    WalOperation.SET + DELIMITER + escape(entry.key()) + DELIMITER + Base64.getEncoder().encodeToString(entry.value());
            case DELETE -> WalOperation.DELETE + DELIMITER + escape(entry.key());
        };
    }

    public static WalEntry deserialize(String line) {

        Objects.requireNonNull(line);

        String[] parts = line.split(DELIMITER, -1);

        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid WAL record: " + line);
        }

        WalOperation operation = WalOperation.valueOf(parts[0]);

        return switch (operation) {

            case SET -> {

                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid SET record: " + line);
                }

                yield WalEntry.set(unescape(parts[1]), Base64.getDecoder().decode(parts[2]));
            }

            case DELETE -> {

                if (parts.length != 2) {
                    throw new IllegalArgumentException("Invalid DELETE record: " + line);
                }

                yield WalEntry.delete(unescape(parts[1]));
            }
        };
    }

    private static String escape(String value) {

        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String unescape(String value) {

        return value.replace("\\r", "\r").replace("\\n", "\n").replace("\\t", "\t").replace("\\\\", "\\");
    }
}