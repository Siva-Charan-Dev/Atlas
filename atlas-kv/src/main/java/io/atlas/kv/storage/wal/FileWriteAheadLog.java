package io.atlas.kv.storage.wal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public final class FileWriteAheadLog implements WriteAheadLog {

    private static final Logger log = LoggerFactory.getLogger(FileWriteAheadLog.class);

    private final Path walFile;

    private final BufferedWriter writer;

    public FileWriteAheadLog(Path walFile) throws IOException {

        this.walFile = walFile;

        Path parent = walFile.getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }
        OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.APPEND, StandardOpenOption.WRITE};

        this.writer = Files.newBufferedWriter(walFile, StandardCharsets.UTF_8, options);

        log.info("Opened WAL at {}", walFile.toAbsolutePath());    }

    @Override
    public synchronized void append(WalEntry entry) throws IOException {

        writer.write(WalCodec.serialize(entry));
        writer.newLine();
        writer.flush();

        log.debug("Appended WAL entry {}", entry);
    }

    @Override
    public List<WalEntry> readAll() throws IOException {

        if (!Files.exists(walFile)) {
            return List.of();
        }

        try (Stream<String> stream = Files.lines(walFile, StandardCharsets.UTF_8)) {

            return stream.filter(line -> !line.isBlank()).map(WalCodec::deserialize).toList();
        }
    }

    @Override
    public synchronized void close() throws IOException {

        writer.close();

        log.info("Closed WAL {}", walFile);
    }
}