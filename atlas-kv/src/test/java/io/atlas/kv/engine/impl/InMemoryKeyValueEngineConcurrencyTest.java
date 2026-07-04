package io.atlas.kv.engine.impl;

import io.atlas.kv.engine.KeyValueEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryKeyValueEngineConcurrencyTest {

    private KeyValueEngine engine;

    @BeforeEach
    void setUp() {
        engine = new InMemoryKeyValueEngine();
    }

    @Test
    @DisplayName("should support concurrent writes")
    void shouldSupportConcurrentWrites() throws Exception {

        int threadCount = 20;
        int keysPerThread = 500;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int t = 0; t < threadCount; t++) {

            final int threadId = t;

            executor.submit(() -> {

                try {

                    for (int i = 0; i < keysPerThread; i++) {

                        String key = "thread-" + threadId + "-key-" + i;

                        engine.put(
                                key,
                                ("value-" + i).getBytes(StandardCharsets.UTF_8)
                        );
                    }

                } finally {
                    latch.countDown();
                }

            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS));

        executor.shutdown();

        assertEquals(threadCount * keysPerThread, engine.size());
    }

    @Test
    @DisplayName("should support concurrent reads")
    void shouldSupportConcurrentReads() throws Exception {

        int entries = 1000;

        for (int i = 0; i < entries; i++) {

            engine.put(
                    "key-" + i,
                    ("value-" + i).getBytes(StandardCharsets.UTF_8)
            );
        }

        ExecutorService executor = Executors.newFixedThreadPool(16);

        List<Callable<Void>> tasks = new ArrayList<>();

        for (int t = 0; t < 16; t++) {

            tasks.add(() -> {

                for (int i = 0; i < entries; i++) {

                    assertTrue(engine.exists("key-" + i));

                    assertTrue(engine.get("key-" + i).isPresent());

                }

                return null;

            });
        }

        executor.invokeAll(tasks);

        executor.shutdown();

        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("should support mixed concurrent reads and writes")
    void shouldSupportMixedReadsAndWrites() throws Exception {

        ExecutorService executor = Executors.newFixedThreadPool(12);

        CountDownLatch latch = new CountDownLatch(12);

        for (int t = 0; t < 6; t++) {

            final int id = t;

            executor.submit(() -> {

                try {

                    for (int i = 0; i < 1000; i++) {

                        engine.put(
                                "writer-" + id + "-" + i,
                                ("value-" + i).getBytes(StandardCharsets.UTF_8)
                        );
                    }

                } finally {
                    latch.countDown();
                }

            });
        }

        for (int t = 0; t < 6; t++) {

            executor.submit(() -> {

                try {

                    for (int i = 0; i < 1000; i++) {

                        engine.exists("writer-0-" + i);

                        engine.get("writer-0-" + i);

                    }

                } finally {
                    latch.countDown();
                }

            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS));

        executor.shutdown();

        assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));

        assertEquals(6000, engine.size());
    }

    @Test
    @DisplayName("returned values should remain immutable from caller perspective")
    void shouldProtectInternalStateFromReturnedArray() {

        engine.put(
                "key",
                "atlas".getBytes(StandardCharsets.UTF_8)
        );

        byte[] value = engine.get("key").orElseThrow();

        value[0] = 'X';

        byte[] secondRead = engine.get("key").orElseThrow();

        assertArrayEquals(
                "atlas".getBytes(StandardCharsets.UTF_8),
                secondRead
        );
    }

    @Test
    @DisplayName("stored value should remain immutable after caller modifies original array")
    void shouldProtectInternalStateFromOriginalArray() {

        byte[] original = "atlas".getBytes(StandardCharsets.UTF_8);

        engine.put("key", original);

        original[0] = 'X';

        byte[] stored = engine.get("key").orElseThrow();

        assertArrayEquals(
                "atlas".getBytes(StandardCharsets.UTF_8),
                stored
        );
    }
}