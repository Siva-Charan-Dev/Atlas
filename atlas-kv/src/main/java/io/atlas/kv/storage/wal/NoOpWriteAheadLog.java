package io.atlas.kv.storage.wal;

import java.util.List;

public final class NoOpWriteAheadLog implements WriteAheadLog {

    @Override
    public void append(WalEntry entry) {
        // intentionally no-op
    }

    @Override
    public List<WalEntry> readAll() {
        return List.of();
    }

    @Override
    public void close() {
        // nothing to close
    }
}