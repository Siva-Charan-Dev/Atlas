package io.atlas.kv.storage.snapshot;

final class SnapshotFormat {

    static final int MAGIC = 0x41545350; // "ATSP"

    static final int VERSION = 1;

    private SnapshotFormat() {
    }
}