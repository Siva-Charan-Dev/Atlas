package io.atlas.kv.storage.snapshot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class BinarySnapshotReader implements SnapshotReader {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinarySnapshotReader.class);

    @Override
    public Snapshot read(Path path) {

        try (DataInputStream in = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {

            int magic = in.readInt();

            if (magic != SnapshotFormat.MAGIC) {
                throw new SnapshotException("Invalid snapshot file.");
            }

            int version = in.readInt();

            if (version != SnapshotFormat.VERSION) {
                throw new SnapshotException("Unsupported snapshot version: " + version);
            }

            int count = in.readInt();

            if (count < 0) {
                throw new SnapshotException("Invalid snapshot entry count.");
            }

            Map<String, byte[]> map = new HashMap<>(count);

            for (int i = 0; i < count; i++) {

                int keyLength = in.readInt();

                if (keyLength < 0) {
                    throw new SnapshotException("Invalid key length.");
                }

                byte[] keyBytes = in.readNBytes(keyLength);

                if (keyBytes.length != keyLength) {
                    throw new SnapshotException("Unexpected end of snapshot.");
                }

                String key = new String(keyBytes, StandardCharsets.UTF_8);

                int valueLength = in.readInt();

                if (valueLength < 0) {
                    throw new SnapshotException("Invalid value length.");
                }

                byte[] value = in.readNBytes(valueLength);

                if (value.length != valueLength) {
                    throw new SnapshotException("Unexpected end of snapshot.");
                }

                map.put(key, value);
            }

            LOGGER.info("Loaded snapshot {} containing {} entries.", path, count);

            return new Snapshot(map);

        } catch (IOException e) {

            throw new SnapshotException("Failed to read snapshot.", e);
        }
    }
}