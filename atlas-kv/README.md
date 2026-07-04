# Atlas KV

Atlas KV is a production-oriented key-value storage engine written in Java 21.

It is the first module of the Atlas platform and is being built incrementally, beginning with a single-node in-memory engine before evolving toward a distributed storage system.

## Requirements

- Java 21 (Gradle toolchain manages the JDK when configured)

## Build

```bash
./gradlew build
./gradlew test
./gradlew run
```

Expected application output:

```
Atlas KV starting...
```

## Project Layout

```
atlas/
├── src/main/java/io/atlas/kv/   Application and engine packages
├── src/test/java/               Unit tests
├── docs/                        Architecture decisions and roadmap
├── benchmarks/                  Reserved for JMH benchmarks
└── scripts/                     Operational and development scripts
```

## Package Structure

Packages are organized by engineering responsibility rather than layered abstractions:

| Package     | Purpose                          |
|-------------|----------------------------------|
| `engine`    | Storage engine core              |
| `cli`       | Command-line interface           |
| `config`    | Configuration                    |
| `exception` | Domain-specific exceptions       |
| `util`      | Shared utilities                 |

## Documentation

- [Roadmap](docs/roadmap.md)
- [Architecture Decision Records](docs/adr/)

## License

See [LICENSE](LICENSE).
