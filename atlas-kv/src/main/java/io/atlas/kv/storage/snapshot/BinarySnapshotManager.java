package io.atlas.kv.storage.snapshot;

import java.nio.file.Path;
import java.util.Objects;

public final class BinarySnapshotManager {

    private final SnapshotReader reader;
    private final SnapshotWriter writer;

    public BinarySnapshotManager(
            SnapshotReader reader,
            SnapshotWriter writer
    ) {
        this.reader = Objects.requireNonNull(reader);
        this.writer = Objects.requireNonNull(writer);
    }

    public Snapshot read(Path path) {
        return reader.read(path);
    }

    public void write(Path path, Snapshot snapshot) {
        writer.write(path, snapshot);
    }
}