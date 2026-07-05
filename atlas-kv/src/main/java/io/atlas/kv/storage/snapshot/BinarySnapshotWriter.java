package io.atlas.kv.storage.snapshot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public final class BinarySnapshotWriter implements SnapshotWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BinarySnapshotWriter.class);

    @Override
    public void write(Path path, Snapshot snapshot) {

        try {

            Files.createDirectories(path.getParent());

            LOGGER.info("Writing snapshot to {} ({} entries)", path, snapshot.size());

            try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {

                out.writeInt(SnapshotFormat.MAGIC);
                out.writeInt(SnapshotFormat.VERSION);
                out.writeInt(snapshot.size());

                for (Map.Entry<String, byte[]> entry : snapshot.entries().entrySet()) {

                    byte[] key = entry.getKey().getBytes(StandardCharsets.UTF_8);

                    byte[] value = entry.getValue();

                    out.writeInt(key.length);
                    out.write(key);

                    out.writeInt(value.length);
                    out.write(value);
                }

                out.flush();
            }

            LOGGER.info("Snapshot successfully written.");

        } catch (IOException e) {

            throw new SnapshotException("Failed to write snapshot.", e);
        }
    }
}