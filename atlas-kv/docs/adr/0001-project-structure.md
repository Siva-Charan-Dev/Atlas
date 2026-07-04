# ADR 0001: Project Structure and Build Infrastructure

## Status

Accepted

## Context

Atlas KV is a long-lived infrastructure project. A storage engine evolves over months. Rushing into implementation without establishing repository conventions leads to costly reorganization later.

We need a structure that is simple today but can grow into multiple modules without premature abstraction.

## Decision

Use a **single functional Gradle module** with Kotlin DSL, Java 21 toolchain, and a package layout organized by engineering responsibility.

### Repository layout

```
atlas/
├── build.gradle.kts
├── settings.gradle.kts
├── src/main/java/io/atlas/kv/
├── src/test/java/io/atlas/kv/
├── docs/
├── benchmarks/    (reserved)
└── scripts/       (reserved)
```

### Build

- Gradle Kotlin DSL (`build.gradle.kts`)
- Java 21 via toolchain (no preview features)
- Plugins: `java`, `application`, `jacoco`
- Dependencies: SLF4J, Logback, JUnit 5, AssertJ only

### Package philosophy

Organize by engineering responsibility (`engine`, `cli`, `config`, `exception`, `util`) rather than layered abstractions (`service`, `manager`, `impl`).

No `storage` package in Step 1 — that arrives in the next milestone.

## Alternatives Considered

### Option A — Single flat module with minimal structure

Too simple; no room for benchmarks, docs, or scripts without clutter.

### Option B — Full multi-module build immediately

Violates YAGNI. Most modules would remain empty for months.

## Consequences

**Positive**

- Clean foundation for CI and future modules
- Consistent conventions from day one
- ADR trail documents major decisions

**Negative**

- Slightly more directories than a minimal hello-world project

## References

- LevelDB, RocksDB, etcd package organization patterns
- Gradle application plugin for runnable entry point
