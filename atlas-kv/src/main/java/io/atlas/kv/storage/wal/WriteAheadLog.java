package io.atlas.kv.storage.wal;

import java.io.IOException;
import java.util.List;

public interface WriteAheadLog extends AutoCloseable {

    void append(WalEntry entry) throws IOException;

    List<WalEntry> readAll() throws IOException;

    @Override
    void close() throws IOException;
}