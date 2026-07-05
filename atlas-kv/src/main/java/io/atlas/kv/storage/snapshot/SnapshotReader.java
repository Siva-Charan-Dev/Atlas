package io.atlas.kv.storage.snapshot;

import java.nio.file.Path;

public interface SnapshotReader {

    Snapshot read(Path path);
}