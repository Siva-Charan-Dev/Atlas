package io.atlas.kv.storage.snapshot;

import java.nio.file.Path;

public interface SnapshotWriter {

    void write(Path path, Snapshot snapshot);
}